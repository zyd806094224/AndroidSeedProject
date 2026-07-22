package com.demo.androidseedproject

import com.demo.shared.error.ApiException
import com.demo.shared.model.ImMessage
import com.demo.shared.model.MsgType
import com.demo.shared.model.WsChatOutbound
import com.demo.shared.repository.ChatRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * KMP IM 业务逻辑测试：验证消息模型与业务校验逻辑。
 *
 * 这些逻辑写在 commonMain，两端共用，只在 Android JVM 跑一遍即可保证两端一致。
 * WebSocket 真实收发与 JSON 序列化需要后端/序列化运行时环境，这里只测纯逻辑。
 */
class ChatBusinessTest {

    @Test
    fun `ImMessage 默认值构造不报错`() {
        val msg = ImMessage()
        assertEquals(0L, msg.msgId)
        assertEquals(1, msg.msgType)
        assertEquals("", msg.content)
    }

    @Test
    fun `ImMessage 字段赋值正确`() {
        val msg = ImMessage(
            msgId = 123L,
            conversationId = 1L,
            senderId = 10L,
            receiverId = 20L,
            msgType = MsgType.IMAGE.value,
            content = "http://x/a.jpg",
            sendTime = "2026-07-22 15:00:00",
            clientMsgId = "c1"
        )
        assertEquals(123L, msg.msgId)
        assertEquals(10L, msg.senderId)
        assertEquals(20L, msg.receiverId)
        assertEquals(2, msg.msgType)
        assertEquals("http://x/a.jpg", msg.content)
    }

    @Test
    fun `WsChatOutbound 构造为 chat 类型`() {
        val out = WsChatOutbound(
            receiverId = 22L,
            msgType = 1,
            content = "hello",
            clientMsgId = "c3"
        )
        assertEquals("chat", out.type)
        assertEquals(22L, out.receiverId)
        assertEquals("c3", out.clientMsgId)
    }

    @Test
    fun `MsgType of 正确映射`() {
        assertEquals(MsgType.TEXT, MsgType.of(1))
        assertEquals(MsgType.IMAGE, MsgType.of(2))
        // 未知值降级为 TEXT
        assertEquals(MsgType.TEXT, MsgType.of(99))
        assertEquals(MsgType.TEXT, MsgType.of(null))
    }

    @Test
    fun `ChatRepository sendMessage 内容为空时抛异常`() = runBlocking {
        val repo = ChatRepository()
        try {
            repo.sendMessage(receiverId = 2L, msgType = 1, content = "   ")
            fail("应抛 ApiException")
        } catch (e: ApiException) {
            assertTrue(e.errMsg.contains("内容不能为空"))
        }
    }

    @Test
    fun `ChatRepository sendMessage 空字符串内容抛异常`() = runBlocking {
        val repo = ChatRepository()
        try {
            repo.sendMessage(receiverId = 2L, msgType = 1, content = "")
            fail("应抛 ApiException")
        } catch (e: ApiException) {
            assertTrue(e.errMsg.contains("内容不能为空"))
        }
    }
}
