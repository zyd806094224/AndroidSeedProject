package com.demo.shared.error

import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.TimeoutCancellationException

/**
 * @Description: 跨平台统一错误处理工具类（从 lib_network 下沉，原版基于 Retrofit/Gson，此处改为 Ktor 异常体系）
 *
 * 异常类型映射对照：
 *   - retrofit2.HttpException          → io.ktor.client.plugins.ResponseException 家族
 *       · 3xx RedirectResponseException
 *       · 4xx ClientRequestException   （401/403/404/408 等）
 *       · 5xx ServerResponseException  （500/502/503/504 等）
 *   - Gson JsonParseException 等       → io.ktor.serialization.JsonConvertException
 *   - java.net.ConnectException        → io.ktor.client.network.sockets.ConnectTimeoutException 等
 *   - java.net.SocketTimeoutException  → io.ktor.client.network.sockets.SocketTimeoutException
 *   - java.net.UnknownHostException    → 平台 actual 层转换为 [NoNetWorkException] 或保留为兜底
 *   - javax.net.ssl.SSLException       → 平台 actual 层转换为 [ERROR.SSL_ERROR]
 *
 * @Date: 2024/8/29 17:12
 * @author:  zhaoyudong
 * @version: 1.0
 */
object ExceptionHandler {

    fun handleException(e: Throwable): ApiException {

        val ex: ApiException
        if (e is ApiException) {
            ex = ApiException(e.errCode, e.errMsg, e)
            if (ex.errCode == ERROR.UNLOGIN.code) {
                //登录失效
            }
        } else if (e is TimeoutCancellationException) {
            // 协程超时异常（withTimeout 触发）
            ex = ApiException(ERROR.TIMEOUT_COROUTINE_ERROR, e)
        } else if (e is NoNetWorkException) {
            ex = ApiException(ERROR.NETWORK_ERROR, e)
        } else if (e is HttpRequestTimeoutException) {
            // Ktor 请求整体超时（对应原 SocketTimeoutException 的部分场景）
            ex = ApiException(ERROR.TIMEOUT_ERROR, e)
        } else if (e is ClientRequestException) {
            // 4xx 客户端错误
            ex = when (e.response.status.value) {
                ERROR.UNAUTHORIZED.code -> ApiException(ERROR.UNAUTHORIZED, e)
                ERROR.FORBIDDEN.code -> ApiException(ERROR.FORBIDDEN, e)
                ERROR.NOT_FOUND.code -> ApiException(ERROR.NOT_FOUND, e)
                ERROR.REQUEST_TIMEOUT.code -> ApiException(ERROR.REQUEST_TIMEOUT, e)
                else -> ApiException(e.response.status.value, e.message ?: "", e)
            }
        } else if (e is ServerResponseException) {
            // 5xx 服务端错误
            ex = when (e.response.status.value) {
                ERROR.INTERNAL_SERVER_ERROR.code -> ApiException(ERROR.INTERNAL_SERVER_ERROR, e)
                ERROR.BAD_GATEWAY.code -> ApiException(ERROR.BAD_GATEWAY, e)
                ERROR.SERVICE_UNAVAILABLE.code -> ApiException(ERROR.SERVICE_UNAVAILABLE, e)
                ERROR.GATEWAY_TIMEOUT.code -> ApiException(ERROR.GATEWAY_TIMEOUT, e)
                else -> ApiException(e.response.status.value, e.message ?: "", e)
            }
        } else if (e is RedirectResponseException) {
            // 3xx 重定向异常
            ex = ApiException(e.response.status.value, e.message ?: "", e)
        } else if (e is ResponseException) {
            // 其他 ResponseException 兜底
            ex = ApiException(e.response.status.value, e.message ?: "", e)
        } else if (e is JsonConvertException) {
            // JSON 序列化/反序列化异常（替代原 Gson 异常）
            ex = ApiException(ERROR.PARSE_ERROR, e)
        } else if (e is ConnectTimeoutException) {
            // 建立连接超时
            ex = ApiException(ERROR.NETWORK_ERROR, e)
        } else if (e is SocketTimeoutException) {
            // Socket 读写超时
            ex = ApiException(ERROR.TIMEOUT_ERROR, e)
        } else {
            // 兜底：未知异常（包含 UnknownHostException 等平台特有异常）
            ex = if (!e.message.isNullOrEmpty()) ApiException(1000, e.message ?: "", e)
            else ApiException(ERROR.UNKNOWN, e)
        }
        return ex
    }
}
