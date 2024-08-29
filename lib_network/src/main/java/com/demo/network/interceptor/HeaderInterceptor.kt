package com.demo.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * @Description:
 * @Date: 2024/8/29 17:54
 * @author:  zhaoyudong
 * @version: 1.0
 */
class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newBuilder = request.newBuilder()
        newBuilder.addHeader("Content-type", "application/json; charset=utf-8")
        val host = request.url().host()
        val url = request.url().toString()
        //给有需要的接口添加Cookies
        /*if (!host.isNullOrEmpty()  && (url.contains(COLLECTION_WEBSITE)
                    || url.contains(NOT_COLLECTION_WEBSITE)
                    || url.contains(ARTICLE_WEBSITE)
                    || url.contains(COIN_WEBSITE))) {
            val cookies = CookiesManager.getCookies()
            if (!cookies.isNullOrEmpty()) {
                newBuilder.addHeader(KEY_COOKIE, cookies)
            }
        }*/
        return chain.proceed(newBuilder.build())
    }
}