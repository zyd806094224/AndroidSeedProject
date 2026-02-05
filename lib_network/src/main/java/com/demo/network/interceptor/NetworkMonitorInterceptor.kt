package com.demo.network.interceptor

import android.util.Log
import okhttp3.Connection
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 网络请求监控拦截器
 */
class NetworkMonitorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // 记录请求开始时间（仅网络请求时生效）
        val start = System.currentTimeMillis()
        // 获取网络相关信息（应用拦截器无法获取）
        val connection: Connection? = chain.connection()
        val ip: String? = if (connection != null) connection.socket().getInetAddress().hostAddress else "unknown"
        // 打印网络请求详情（包括 IP、协议）
        Log.d("OkHttp-Net", "Network Request: " + chain.request().url() + " | 服务端IP: " + ip)
        // 执行网络请求
        val response = chain.proceed(chain.request())
        // 计算网络耗时
        val duration = System.currentTimeMillis() - start
        Log.d("OkHttp-Net", "Network Response: " + response.code() + " | Cost: " + duration + "ms")
        return response
    }
}