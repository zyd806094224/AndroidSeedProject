package com.demo.network.interceptor

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.MediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 网络请求重试拦截器
 * 可通过自定义请求头设置需要重试请求的接口重试次数
 * 可监控上报 UnknownHostException 异常 dns或其他网络问题
 */
class NetworkRetryInterceptor : Interceptor {

    companion object {
        const val RETRY_TIME_HEADER_KEY = "retry-time"
        const val RETRY_TIME_HEADER = "$RETRY_TIME_HEADER_KEY:3"
    }

    /**
     * 请求拦截
     * @param chain 拦截器链
     * @return 响应
     */
    override fun intercept(chain: Chain): Response {
        val request = chain.request()
        val retryTime = request.header(RETRY_TIME_HEADER_KEY)?.toIntOrNull() ?: 0
        if (retryTime <= 0) {
            return try {
                chain.proceed(request)
            } catch (e: UnknownHostException) {
                //TODO UnknownHostException  reportException("网络请求失败", e, mapOf("url" to request.url().toString(), "ip" to getUserIP()))
                buildEmptyResponse(request)
            }
        }
        var response: Response? = handleRequest(chain, request, 0, 3)
        // 如果没有获取到响应，则进行重试
        if (response == null) {
            // 当前请求次数
            var requestTimes = 0
            while (requestTimes < retryTime) {
                response = handleRequest(chain, request, requestTimes, retryTime)
                // 获取到响应退出重试逻辑
                if (response != null) {
                    break
                }
                requestTimes++
            }
        }
        return response ?: buildEmptyResponse(request)
    }

    /**
     * 构建未知主机的响应
     * @param request 请求
     * @return 响应
     */
    private fun buildEmptyResponse(request: Request): Response {
        return Response.Builder()
            .code(404)
            .protocol(Protocol.HTTP_2)
            .message("未知主机")
            .request(request)
            .body(
                ResponseBody.create(
                    MediaType.parse("application/json"),
                    Gson().toJson(mapOf("code" to -1, "message" to "网络出现错误"))
                )
            )
            .build()
    }

    /**
     * 发起请求
     * @param chain 拦截器链
     * @param request 请求
     * @param nowTime 当前重试次数
     * @param maxTime 最大重试次数
     */
    private fun handleRequest(chain: Chain, request: Request, nowTime: Int, maxTime: Int): Response? {
        try {
            val retryRequest = request.newBuilder()
                .removeHeader(RETRY_TIME_HEADER_KEY)
                .build()
            return chain.proceed(retryRequest)
        } catch (t: Throwable) {
            // 仅在超时异常时进行重试
            if ((t is SocketTimeoutException || t is SocketException) && nowTime < maxTime) {
                return null
            }
            // 如果是 IOException，说明是正常的网络错误（如 DNS 失败、连接重置等），
            // 交给 OkHttp 的 onFailure 回调处理，不会导致崩溃。
            if (t is java.io.IOException) {
                throw t
            }
            // 其他未知的运行时异常（如 NoSuchElementException），先上报 APM，
            // 然后包装成 IOException 抛出，这样会进入 onFailure 回调，防止 App 崩溃。
            if (t is Exception) {
                //TODO reportException("网络请求失败", t, mapOf("url" to request.url().toString(), "ip" to getUserIP()))
            }
            throw java.io.IOException("Unexpected exception during retry", t)
        }
    }
}