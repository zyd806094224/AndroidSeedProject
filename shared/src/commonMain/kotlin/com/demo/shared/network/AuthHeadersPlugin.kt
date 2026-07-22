package com.demo.shared.network

import com.demo.shared.repository.TokenManager
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.HttpHeaders

/**
 * 动态 Authorization Header 插件
 *
 * 在每个请求发出前，从 [TokenManager] 读取当前 token 并注入 `Authorization: Bearer xxx`。
 * 用 createClientPlugin（Ktor 2.3 推荐写法）在请求阶段拦截，保证 token 是登录后的最新值——
 * DefaultRequest 的配置在 HttpClient 构建时就固化了，无法反映 token 变化。
 *
 * 已手动设置过 Authorization 的请求不会被覆盖。
 *
 * @author zhaoyudong
 */
val AuthHeadersPlugin = createClientPlugin("AuthHeadersPlugin") {
    onRequest { request, _ ->
        // 已手动设置 Authorization 的请求不覆盖
        if (request.headers[HttpHeaders.Authorization] == null) {
            val token = TokenManager.getToken()
            if (token.isNotEmpty()) {
                request.headers.append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }
}
