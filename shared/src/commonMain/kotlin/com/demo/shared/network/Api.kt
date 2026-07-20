package com.demo.shared.network

import com.demo.shared.model.BaseResponse
import com.demo.shared.model.LoginInfo
import com.demo.shared.model.LoginRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
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
     * 登录接口（业务示例）
     *
     * @param request 登录请求参数
     * @return 登录成功后的用户信息（含 token）
     */
    suspend fun login(request: LoginRequest): BaseResponse<LoginInfo> {
        ensureNetworkAvailable()
        return httpClient.post("https://106.15.7.132:8443/user/login") {
            setBody(request)
        }.body()
    }
}
