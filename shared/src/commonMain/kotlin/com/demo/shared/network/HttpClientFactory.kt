package com.demo.shared.network

import com.demo.shared.constant.BASE_URL
import com.demo.shared.constant.DEFAULT_RETRY_COUNT
import com.demo.shared.constant.DEFAULT_TIMEOUT_MILLIS
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * @Description: 跨平台 Ktor HttpClient 工厂（替代 lib_network 的 HttpManager）。
 *
 * 与原 HttpManager 对应关系：
 *   - Retrofit Builder + GsonConverterFactory  → ContentNegotiation(json) + kotlinx.serialization
 *   - HeaderInterceptor（Content-type）         → DefaultRequest { header(...) }
 *   - HttpLoggingInterceptor                    → Logging 插件
 *   - NetworkRetryInterceptor（retry-time）     → HttpRequestRetry 插件
 *   - 无网络拦截器（throw NoNetWorkException）   → installNetworkCheckPlugin（基于 [isNetworkAvailable]）
 *   - 超时 connect/write/read 10s               → HttpTimeout 插件
 *   - baseUrl                                   → DefaultRequest { url = ... }
 *
 * 引擎（OkHttp / Darwin）由各平台 actual 通过 [newHttpClientEngine] 提供。
 *
 * @Date: 2024/8/29 17:09
 * @author:  zhaoyudong
 * @version: 1.0
 */

/**
 * 全局 JSON 配置：宽松解析，兼容服务端返回的默认值缺失等场景。
 */
val sharedJson: Json = Json {
    ignoreUnknownKeys = true      // 忽略服务端多余字段
    isLenient = true              // 宽松语法
    encodeDefaults = true         // 序列化时输出默认值
    explicitNulls = false         // 不强制输出 null 字段
}

/**
 * 创建跨平台 [HttpClient]。
 *
 * @param enableLogging 是否开启请求日志（建议 DEBUG 构建 opens）
 */
fun createSharedHttpClient(enableLogging: Boolean = true): HttpClient {
    return HttpClient(newHttpClientEngine()) {
        // 1. JSON 序列化（替代 GsonConverterFactory）
        install(ContentNegotiation) {
            json(sharedJson)
        }

        // 2. 默认请求配置：baseUrl + Content-type header（替代 HeaderInterceptor）
        //    Token 通过 [AuthHeadersPlugin] 在每次请求时动态注入（DefaultRequest 配置是静态的，
        //    无法在登录后变化时刷新）。
        install(DefaultRequest) {
            url(BASE_URL)
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
        }

        // 2.1 动态注入 Authorization header：从 TokenManager 读取当前 token。
        //     用自定义 BaseClientPlugin，在每个请求发出前回调，保证 token 是最新值。
        install(AuthHeadersPlugin)

        // 3. 超时（替代 OkHttp 的 connect/write/read timeout）
        install(HttpTimeout) {
            connectTimeoutMillis = DEFAULT_TIMEOUT_MILLIS
            requestTimeoutMillis = DEFAULT_TIMEOUT_MILLIS
            socketTimeoutMillis = DEFAULT_TIMEOUT_MILLIS
        }

        // 4. 重试（替代 NetworkRetryInterceptor 的 retry-time 逻辑）
        install(HttpRequestRetry) {
            retryOnException(maxRetries = DEFAULT_RETRY_COUNT)
            retryOnServerErrors(maxRetries = DEFAULT_RETRY_COUNT)
            exponentialDelay()
        }

        // 5. 日志（替代 HttpLoggingInterceptor + NetworkMonitorInterceptor）
        //     LogLevel.HEADERS 只打 header；排查响应体用 LogLevel.ALL（含 body），线上建议改回 HEADERS
        if (enableLogging) {
            install(Logging) {
                logger = SharedHttpLogger
                level = LogLevel.ALL
            }
        }

        // 6. 无网络检测：在 Api 层调用 [ensureNetworkAvailable] 前置检查，
        //    不在 HttpClient 插件层做（Ktor 1.8/2.3 的 createClientPlugin API 在 common 使用较繁琐）。
    }
}
