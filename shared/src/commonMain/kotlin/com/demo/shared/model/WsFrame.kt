package com.demo.shared.model

import kotlinx.serialization.Serializable

/**
 * @Description: WebSocket 协议帧模型（commonMain）
 *
 * 服务端用 fastjson2 发送 JSON 文本帧，统一带 `type` 字段区分：
 *   - "chat"  新消息推送（服务端→客户端）
 *   - "ack"   投递确认（服务端→客户端，回应客户端发送的 chat）
 *   - "pong"  心跳响应（服务端→客户端）
 *   - "ping"  心跳请求（客户端→服务端）
 *
 * 客户端用 [WsInbound] 统一解析入站帧，按 type 派发。
 * 出站帧用 [WsChatOutbound] / [WsPingOutbound]。
 *
 * @author:  zhaoyudong
 * @version: 1.0
 */

/**
 * 入站帧：用 data class + type 字段手动派发，避免 polymorphic 序列化的 classDiscriminator 与
 * 服务端 fastjson 不一致带来的兼容问题。
 */
@Serializable
data class WsInbound(
    val type: String,
    // chat 帧
    val msgId: Long? = null,
    val conversationId: Long? = null,
    val senderId: Long? = null,
    val receiverId: Long? = null,
    val msgType: Int? = null,
    val content: String? = null,
    val sendTime: String? = null,
    val clientMsgId: String? = null,
    // ack 帧
    val success: Boolean? = null,
    val errMsg: String? = null
)

/**
 * 出站：发送聊天消息
 */
@Serializable
data class WsChatOutbound(
    val type: String = "chat",
    val receiverId: Long,
    val msgType: Int,
    val content: String,
    val clientMsgId: String
)

/**
 * 出站：心跳
 */
@Serializable
data class WsPingOutbound(
    val type: String = "ping"
)

/** 入站帧类型常量 */
object WsFrameType {
    const val CHAT = "chat"
    const val ACK = "ack"
    const val PONG = "pong"
}
