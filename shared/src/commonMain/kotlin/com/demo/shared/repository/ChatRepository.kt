package com.demo.shared.repository

import com.demo.shared.error.ApiException
import com.demo.shared.error.ERROR
import com.demo.shared.model.ImConversation
import com.demo.shared.model.ImMessage
import com.demo.shared.network.Api
import com.demo.shared.network.ChatSocketClient
import com.demo.shared.network.ConnectionState
import kotlinx.coroutines.flow.SharedFlow

/**
 * @Description: IM 聊天仓库（commonMain 业务逻辑）
 *
 * 组合 REST 接口（[Api]，会话/历史/已读/未读）和 WebSocket 客户端（[ChatSocketClient]，实时消息）。
 * UI 层只依赖本仓库，通过 [observeMessages] 订阅实时消息，通过 [sendMessage] 发消息。
 *
 * 与 [LoginRepository] 同样继承 [BaseRepository]，复用统一的超时 + 状态码校验。
 *
 * @author:  zhaoyudong
 * @version: 1.0
 */
class ChatRepository : BaseRepository() {

    private val socketClient = ChatSocketClient()

    /**
     * 建立 WebSocket 实时连接（登录成功后调用）。
     *
     * @param token 登录 token
     */
    suspend fun start(token: String) {
        socketClient.connect(token)
    }

    /**
     * 断开 WebSocket 连接（退出登录/退后台时调用）。
     */
    fun stop() {
        socketClient.disconnect()
    }

    /**
     * 实时消息流：UI 订阅这个 Flow 接收对方发来的新消息。
     */
    fun observeMessages(): SharedFlow<ImMessage> = socketClient.messages

    /**
     * 连接状态流。
     */
    fun observeConnectionState() = socketClient.connectionState

    /**
     * 发送消息（文本/图片）。
     *
     * 通过 WebSocket 发送并等待服务端 ack。ack 成功返回服务端消息ID。
     *
     * @param receiverId 接收者用户ID
     * @param msgType 消息类型（1文本 2图片）
     * @param content 消息内容（文本 / 图片URL）
     * @return 服务端分配的消息ID
     * @throws ApiException 内容为空 / 未连接 / ack 失败
     */
    suspend fun sendMessage(receiverId: Long, msgType: Int, content: String): Long {
        if (content.isBlank()) {
            throw ApiException(ERROR.UNKNOWN.code, "消息内容不能为空")
        }
        if (socketClient.connectionState.value != ConnectionState.CONNECTED) {
            throw ApiException(ERROR.UNKNOWN.code, "WebSocket 未连接")
        }
        return socketClient.sendMessage(receiverId, msgType, content)
    }

    /**
     * 获取/创建会话。
     */
    suspend fun getOrCreateConversation(targetId: Long): ImConversation {
        return requestResponse { Api.getOrCreateConversation(targetId) }
            ?: throw ApiException(ERROR.PARSE_ERROR)
    }

    /**
     * 会话列表。
     */
    suspend fun getConversations(): List<ImConversation> {
        return requestResponse { Api.getConversations() } ?: emptyList()
    }

    /**
     * 历史消息分页。
     *
     * @param conversationId 会话ID
     * @param lastMsgId 游标，null 查最新一页
     * @param size 每页条数
     */
    suspend fun loadHistory(conversationId: Long, lastMsgId: Long? = null, size: Int = 20): List<ImMessage> {
        return requestResponse { Api.getHistoryMessages(conversationId, lastMsgId, size) } ?: emptyList()
    }

    /**
     * 标记会话已读。
     */
    suspend fun markRead(conversationId: Long) {
        requestResponse { Api.markRead(conversationId) }
    }

    /**
     * 未读消息总数。
     */
    suspend fun getUnreadCount(): Int {
        return requestResponse { Api.getUnreadCount() }?.count ?: 0
    }
}
