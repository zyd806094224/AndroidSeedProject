package com.demo.shared.network

import com.demo.shared.constant.SERVER_WS_URL
import com.demo.shared.logger.AppLog
import com.demo.shared.model.ImMessage
import com.demo.shared.model.WsChatOutbound
import com.demo.shared.model.WsFrameType
import com.demo.shared.model.WsInbound
import com.demo.shared.model.WsPingOutbound
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.url
import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.encodeToString
import kotlin.random.Random

/**
 * @Description: IM WebSocket 客户端（commonMain，Android/iOS 共用）
 *
 * 职责：
 *   1. 建立/断开 WebSocket 长连接（token 通过 URL query 参数鉴权）
 *   2. 暴露 [messages] SharedFlow 给 UI 订阅收到的聊天消息
 *   3. 暴露 [connectionState] StateFlow 让 UI 感知连接状态
 *   4. [sendMessage] 发送消息并等待服务端 ack（基于 clientMsgId 关联）
 *   5. 后台心跳保活（每 30s 发 ping）
 *
 * 关键设计：
 *   - 用独立的 [HttpClient]（仅装 WebSockets 插件），不复用 [Api.httpClient]——
 *     后者装了 HttpTimeout(30s)/HttpRequestRetry 插件，会杀掉长连接。
 *   - WS 引擎复用 [newHttpClientEngine]（Android OkHttp / iOS Darwin 均原生支持 WS）。
 *   - ack 等待用 MutableMap<String, CompletableDeferred> + Mutex（commonMain 无 ConcurrentHashMap）。
 *
 * @author:  zhaoyudong
 * @version: 1.0
 */

/** 连接状态 */
enum class ConnectionState { DISCONNECTED, CONNECTING, CONNECTED }

/** ack 等待超时（毫秒） */
private const val ACK_TIMEOUT_MILLIS = 5_000L

/** 心跳间隔（毫秒） */
private const val HEARTBEAT_INTERVAL_MILLIS = 30_000L

class ChatSocketClient private constructor() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    /** 独立的 HttpClient：只装 WebSockets 插件，不装超时/重试（避免杀长连接） */
    private val wsClient: HttpClient by lazy {
        HttpClient(newHttpClientEngine()) {
            install(WebSockets)
        }
    }

    /** 收到的聊天消息流，UI 订阅这个 */
    private val _messages = MutableSharedFlow<ImMessage>(extraBufferCapacity = 64)
    val messages: SharedFlow<ImMessage> = _messages.asSharedFlow()

    /** 连接状态流 */
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    /** 当前 WebSocket Session（在协程内访问，由 connectionJob 串行化） */
    private var session: DefaultWebSocketSession? = null

    /** 连接协程（含接收循环 + 心跳） */
    private var connectionJob: Job? = null

    /** 待响应的 ack：clientMsgId → CompletableDeferred<服务端 msgId> */
    private val pendingAcks = mutableMapOf<String, CompletableDeferred<Long>>()
    private val ackMutex = Mutex()

    /** 客户端消息 ID 自增（配合随机数，保证唯一） */
    private var msgIdSeq: Long = 0L

    /**
     * 建立 WebSocket 连接。
     *
     * @param token 登录 token（放 URL query 参数，服务端 HandshakeInterceptor 解析）
     */
    suspend fun connect(token: String) {
        if (_connectionState.value == ConnectionState.CONNECTING ||
            _connectionState.value == ConnectionState.CONNECTED
        ) {
            AppLog.d(TAG, "connect: 已连接或连接中，跳过")
            return
        }
        if (token.isEmpty()) {
            throw IllegalStateException("token 为空，无法建立 WS 连接")
        }
        _connectionState.value = ConnectionState.CONNECTING
        connectionJob?.cancel()
        connectionJob = scope.launch {
            try {
                val wsUrl = "$SERVER_WS_URL?token=$token"
                AppLog.d(TAG, "connect: $wsUrl")
                wsClient.webSocket({
                    this.url(wsUrl)
                }) {
                    // 此 lambda 在 session 上下文内执行，会挂起到连接关闭
                    session = this
                    _connectionState.value = ConnectionState.CONNECTED
                    AppLog.d(TAG, "connect: 连接成功")
                    launchHeartbeat()
                    receiveLoop()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                AppLog.e(TAG, "connect: 连接异常 ${e.message}")
            } finally {
                _connectionState.value = ConnectionState.DISCONNECTED
                session = null
                failAllPendingAcks("连接已断开")
                AppLog.d(TAG, "connect: 连接结束")
            }
        }
    }

    /**
     * 发送聊天消息（同步等待服务端 ack）。
     *
     * @param receiverId 接收者用户ID
     * @param msgType 消息类型（1文本 2图片）
     * @param content 消息内容
     * @return 服务端分配的消息ID（ack 回填），ack 失败或超时抛 [Exception]
     */
    suspend fun sendMessage(receiverId: Long, msgType: Int, content: String): Long {
        val currentSession = session
        if (currentSession == null || _connectionState.value != ConnectionState.CONNECTED) {
            throw IllegalStateException("未连接，无法发送消息")
        }
        val clientMsgId = nextClientMsgId()
        val deferred = CompletableDeferred<Long>()
        ackMutex.withLock { pendingAcks[clientMsgId] = deferred }

        val frame = WsChatOutbound(
            receiverId = receiverId,
            msgType = msgType,
            content = content,
            clientMsgId = clientMsgId
        )
        val json = sharedJson.encodeToString(WsChatOutbound.serializer(), frame)
        AppLog.d(TAG, "send: $json")
        currentSession.send(Frame.Text(json))

        return try {
            withTimeout(ACK_TIMEOUT_MILLIS) { deferred.await() }
        } catch (e: TimeoutCancellationException) {
            ackMutex.withLock { pendingAcks.remove(clientMsgId) }
            throw Exception("消息发送超时，未收到服务端确认")
        }
    }

    /**
     * 主动断开连接。
     *
     * 取消连接协程会触发 webSocket{} 的 finally，session 自动关闭。
     */
    fun disconnect() {
        AppLog.d(TAG, "disconnect")
        connectionJob?.cancel()
        connectionJob = null
        _connectionState.value = ConnectionState.DISCONNECTED
        scope.launch { failAllPendingAcks("主动断开") }
    }

    // ---- 内部 ----

    /**
     * 接收循环：读取 incoming 帧 → 解析 JSON → 按 type 派发。
     */
    private suspend fun DefaultWebSocketSession.receiveLoop() {
        incoming.consumeEach { frame ->
            if (frame !is Frame.Text) return@consumeEach
            val text = frame.readText()
            try {
                val inbound = sharedJson.decodeFromString(WsInbound.serializer(), text)
                when (inbound.type) {
                    WsFrameType.CHAT -> handleChatFrame(inbound)
                    WsFrameType.ACK -> handleAckFrame(inbound)
                    WsFrameType.PONG -> { /* 心跳响应，无需处理 */ }
                    else -> AppLog.d(TAG, "receive: 未知帧类型 ${inbound.type}")
                }
            } catch (e: Exception) {
                AppLog.e(TAG, "receive: 解析失败 ${e.message}, raw=$text")
            }
        }
    }

    private fun handleChatFrame(frame: WsInbound) {
        val message = ImMessage(
            msgId = frame.msgId ?: 0L,
            conversationId = frame.conversationId ?: 0L,
            senderId = frame.senderId ?: 0L,
            receiverId = frame.receiverId ?: 0L,
            msgType = frame.msgType ?: 1,
            content = frame.content ?: "",
            sendTime = frame.sendTime ?: "",
            clientMsgId = frame.clientMsgId ?: ""
        )
        scope.launch { _messages.emit(message) }
    }

    private suspend fun handleAckFrame(frame: WsInbound) {
        val clientMsgId = frame.clientMsgId ?: return
        val deferred = ackMutex.withLock { pendingAcks.remove(clientMsgId) } ?: return
        if (frame.success == true) {
            deferred.complete(frame.msgId ?: 0L)
        } else {
            deferred.completeExceptionally(Exception(frame.errMsg ?: "消息发送失败"))
        }
    }

    /**
     * 心跳：每 30s 发一次 ping。
     */
    private fun DefaultWebSocketSession.launchHeartbeat(): Job {
        return scope.launch {
            while (true) {
                delay(HEARTBEAT_INTERVAL_MILLIS)
                try {
                    val json = sharedJson.encodeToString(WsPingOutbound.serializer(), WsPingOutbound())
                    send(Frame.Text(json))
                } catch (e: Exception) {
                    AppLog.e(TAG, "heartbeat 发送失败 ${e.message}")
                    break
                }
            }
        }
    }

    private suspend fun failAllPendingAcks(reason: String) {
        val snapshot = ackMutex.withLock {
            val copy = pendingAcks.toMap()
            pendingAcks.clear()
            copy
        }
        snapshot.forEach { (_, deferred) ->
            deferred.completeExceptionally(Exception(reason))
        }
    }

    /**
     * 生成客户端消息ID：自增序列 + 随机数，保证唯一（不依赖平台时钟 API）。
     */
    private fun nextClientMsgId(): String {
        msgIdSeq += 1
        return "$msgIdSeq-${Random.nextLong()}"
    }

    companion object {
        private const val TAG = "ChatSocketClient"

        /** 全局单例：所有 Repository/UseCase 共享同一个 WebSocket 连接 */
        val instance: ChatSocketClient by lazy { ChatSocketClient() }
    }
}
