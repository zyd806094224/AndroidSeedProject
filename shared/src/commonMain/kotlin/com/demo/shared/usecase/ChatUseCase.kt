package com.demo.shared.usecase

import com.demo.shared.error.ApiException
import com.demo.shared.model.ImConversation
import com.demo.shared.model.ImMessage
import com.demo.shared.repository.ChatRepository

/**
 * @Description: IM 聊天用例（commonMain，可选层）。
 *
 * 与 [LoginUseCase] 同样的定位：把 Repository 的调用包成对 UI 友好的 sealed Result，
 * 失败不抛异常而是返回 [ChatResult.Fail]，UI 层 `when` 分支处理即可。
 * 简单场景也可跳过 UseCase，直接调 [ChatRepository]。
 *
 * @author:  zhaoyudong
 * @version: 1.0
 */
class ChatUseCase(
    private val chatRepository: ChatRepository = ChatRepository()
) {

    /**
     * 启动实时连接（登录后调用）。
     */
    suspend fun start(token: String) {
        chatRepository.start(token)
    }

    /**
     * 断开连接。
     */
    fun stop() {
        chatRepository.stop()
    }

    /**
     * 实时消息流（直接透传 Repository）。
     */
    fun observeMessages() = chatRepository.observeMessages()

    /**
     * 连接状态流（直接透传 Repository）。
     */
    fun observeConnectionState() = chatRepository.observeConnectionState()

    /**
     * 发送消息。
     *
     * @param receiverId 接收者用户ID
     * @param msgType 消息类型（1文本 2图片）
     * @param content 内容
     * @return [SendResult]：成功带服务端消息ID，失败带错误信息
     */
    suspend fun sendMessage(receiverId: Long, msgType: Int, content: String): SendResult {
        return try {
            val msgId = chatRepository.sendMessage(receiverId, msgType, content)
            SendResult.Success(msgId)
        } catch (e: ApiException) {
            SendResult.Fail(e.errCode, e.errMsg)
        } catch (e: Exception) {
            SendResult.Fail(-1, e.message ?: "发送失败")
        }
    }

    /**
     * 获取会话列表。
     */
    suspend fun getConversations(): ListResult<ImConversation> {
        return try {
            ListResult.Success(chatRepository.getConversations())
        } catch (e: ApiException) {
            ListResult.Fail(e.errCode, e.errMsg)
        }
    }

    /**
     * 拉取历史消息。
     */
    suspend fun loadHistory(conversationId: Long, lastMsgId: Long? = null): ListResult<ImMessage> {
        return try {
            ListResult.Success(chatRepository.loadHistory(conversationId, lastMsgId))
        } catch (e: ApiException) {
            ListResult.Fail(e.errCode, e.errMsg)
        }
    }

    // ---- sealed 结果 ----

    /** 发送消息结果 */
    sealed class SendResult {
        data class Success(val msgId: Long) : SendResult()
        data class Fail(val errCode: Int, val errMsg: String) : SendResult()
    }

    /** 列表查询结果（泛型，复用给会话列表/历史消息） */
    sealed class ListResult<T> {
        data class Success<T>(val data: List<T>) : ListResult<T>()
        data class Fail<T>(val errCode: Int, val errMsg: String) : ListResult<T>()
    }
}
