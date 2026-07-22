package com.demo.shared.network

import com.demo.shared.constant.SERVER_BASE_URL
import com.demo.shared.model.BaseResponse
import com.demo.shared.model.CreateConversationRequest
import com.demo.shared.model.ImConversation
import com.demo.shared.model.ImMessage
import com.demo.shared.model.LoginRequest
import com.demo.shared.model.MarkReadRequest
import com.demo.shared.model.RuoYiLoginResponse
import com.demo.shared.model.UnreadCount
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody

/**
 * @Description: Ktor 版网络接口（替代 lib_network 的 ApiInterface + ApiManager）。
 *
 * 原版用 Retrofit 注解（@GET/@Headers），Ktor 改为 suspend 扩展函数 + DSL。
 * 与原版一一对应：
 *   - getDataList()   → 对应 @GET("/dataList")
 *   - testRequest()   → 对应 @GET("http://192.168.213.9:8060/user/test")（绝对 URL，覆盖 baseUrl）
 *   - test2Request()  → 对应 @GET("http://192.168.213.9:8060/user/test2")
 *
 * 每个 API 调用前通过 [ensureNetworkAvailable] 做无网络检测（替代原 OkHttp 拦截器逻辑）。
 *
 * @Date: 2024/8/29 17:09
 * @author:  zhaoyudong
 * @version: 1.0
 */

/**
 * KMP 共享的 Api 单例：内部持有懒加载的 [HttpClient]。
 * 使用方直接 [Api.getDataList]，无需关心 HttpClient 生命周期。
 *
 * 注意：iOS 端需在协程作用域内调用（内部是 suspend）。
 */
object Api {

    /**
     * 全局 HttpClient（懒加载，首次访问时创建）。
     * 日志默认开启，可在发布构建时改为 false。
     */
    val httpClient: HttpClient by lazy { createSharedHttpClient(enableLogging = true) }

    /**
     * 获取数据列表（对应原 ApiInterface.getDataList）
     */
    suspend fun getDataList(): BaseResponse<List<String>> {
        ensureNetworkAvailable()
        // 相对路径：走 DefaultRequest 配置的 BASE_URL
        return httpClient.get("/dataList").body()
    }

    /**
     * 测试请求（对应原 ApiInterface.testRequest）
     * 使用绝对 URL，会覆盖 DefaultRequest 的 baseUrl。
     */
    suspend fun testRequest(): BaseResponse<String> {
        ensureNetworkAvailable()
        return httpClient.get("https://106.15.7.132:8443/user/test").body()
    }

    /**
     * 测试请求2（对应原 ApiInterface.test2Request）
     */
    suspend fun test2Request(): BaseResponse<String> {
        ensureNetworkAvailable()
        return httpClient.get("http://192.168.213.9:8060/user/test2").body()
    }

    /**
     * 登录接口（若依标准 /login）
     *
     * 对接服务端 [SysLoginController.login]，返回 token 与 code/msg 平级（AjaxResult）。
     * 验证码已关闭时只需传 username + password。
     *
     * @param request 登录请求参数（username/password）
     * @return 登录响应，成功时含 token
     */
    suspend fun login(request: LoginRequest): RuoYiLoginResponse {
        ensureNetworkAvailable()
        return httpClient.post("$SERVER_BASE_URL/login") {
            setBody(request)
        }.body()
    }

    // ==================== IM 聊天接口 ====================

    /**
     * 获取/创建会话（登录后发起聊天前调用）
     *
     * @param targetId 对方用户ID
     * @return 当前用户视角的会话
     */
    suspend fun getOrCreateConversation(targetId: Long): BaseResponse<ImConversation> {
        ensureNetworkAvailable()
        return httpClient.post("$SERVER_BASE_URL/chat/conversation") {
            setBody(CreateConversationRequest(targetId))
        }.body()
    }

    /**
     * 会话列表（含对方昵称/头像、最后消息摘要、未读数）
     */
    suspend fun getConversations(): BaseResponse<List<ImConversation>> {
        ensureNetworkAvailable()
        return httpClient.get("$SERVER_BASE_URL/chat/conversations").body()
    }

    /**
     * 历史消息分页（基于 msgId 游标向前翻）
     *
     * @param conversationId 会话ID
     * @param lastMsgId 游标（上一页最后一条 msgId），null 则查最新一页
     * @param size 每页条数
     */
    suspend fun getHistoryMessages(
        conversationId: Long,
        lastMsgId: Long? = null,
        size: Int = 20
    ): BaseResponse<List<ImMessage>> {
        ensureNetworkAvailable()
        return httpClient.get("$SERVER_BASE_URL/chat/history") {
            parameter("conversationId", conversationId)
            if (lastMsgId != null) {
                parameter("lastMsgId", lastMsgId)
            }
            parameter("size", size)
        }.body()
    }

    /**
     * 标记会话已读（清零未读数）
     */
    suspend fun markRead(conversationId: Long): BaseResponse<Unit> {
        ensureNetworkAvailable()
        return httpClient.post("$SERVER_BASE_URL/chat/read") {
            setBody(MarkReadRequest(conversationId))
        }.body()
    }

    /**
     * 未读消息总数
     */
    suspend fun getUnreadCount(): BaseResponse<UnreadCount> {
        ensureNetworkAvailable()
        return httpClient.get("$SERVER_BASE_URL/chat/unread/count").body()
    }
}
