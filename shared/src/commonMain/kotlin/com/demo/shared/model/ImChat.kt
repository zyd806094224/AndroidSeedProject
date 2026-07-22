package com.demo.shared.model

import kotlinx.serialization.Serializable

/**
 * @Description: IM 聊天业务数据模型（commonMain，Android/iOS 共用）
 *
 * 与服务端 seed-im 模块的 domain 对齐：
 *   - ImMessage      ↔ im_message 表 / WebSocket chat 推送帧
 *   - ImConversation ↔ im_conversation 表 / ConversationVO
 *
 * @author:  zhaoyudong
 * @version: 1.0
 */

/**
 * 消息类型
 */
enum class MsgType(val value: Int) {
    TEXT(1),
    IMAGE(2);

    companion object {
        fun of(value: Int?): MsgType = values().firstOrNull { it.value == value } ?: TEXT
    }
}

/**
 * IM 消息（REST 历史与 WebSocket 推送共用此模型）
 *
 * @param msgId 服务端消息ID（发送前为 0，服务端 ack 后回填）
 * @param conversationId 会话ID
 * @param senderId 发送者用户ID
 * @param receiverId 接收者用户ID
 * @param msgType 消息类型（1文本 2图片）
 * @param content 消息内容（文本内容 / 图片URL）
 * @param sendTime 发送时间（服务端返回 "yyyy-MM-dd HH:mm:ss" 字符串，解析为时间戳）
 * @param clientMsgId 客户端生成的临时ID，用于 ack 关联
 */
@Serializable
data class ImMessage(
    val msgId: Long = 0L,
    val conversationId: Long = 0L,
    val senderId: Long = 0L,
    val receiverId: Long = 0L,
    val msgType: Int = 1,
    val content: String = "",
    val sendTime: String = "",
    val clientMsgId: String = ""
)

/**
 * 会话列表项（对应服务端 ConversationVO）
 *
 * @param conversationId 会话ID
 * @param targetId 对方用户ID
 * @param targetName 对方昵称
 * @param targetAvatar 对方头像URL
 * @param lastMsgContent 最后一条消息摘要
 * @param lastMsgTime 最后一条消息时间
 * @param unreadCount 未读数
 */
@Serializable
data class ImConversation(
    val conversationId: Long = 0L,
    val targetId: Long = 0L,
    val targetName: String = "",
    val targetAvatar: String = "",
    val lastMsgContent: String = "",
    val lastMsgTime: String = "",
    val unreadCount: Int = 0
)

/**
 * 获取/创建会话接口的请求体
 */
@Serializable
data class CreateConversationRequest(
    val targetId: Long
)

/**
 * 标记已读接口的请求体
 */
@Serializable
data class MarkReadRequest(
    val conversationId: Long
)

/**
 * 未读数接口返回数据
 */
@Serializable
data class UnreadCount(
    val count: Int = 0
)
