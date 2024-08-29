package com.demo.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * @Description: cookie拦截器
 * @Date: 2024/8/29 17:56
 * @author:  zhaoyudong
 * @version: 1.0
 */
class CookiesInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newBuilder = request.newBuilder()

        val response = chain.proceed(newBuilder.build())
        val url = request.url().toString()
        val host = request.url().host()

        // set-cookie maybe has multi, login to save cookie
        /*if ((url.contains(KEY_SAVE_USER_LOGIN) || url.contains(KEY_SAVE_USER_REGISTER))
            && response.headers(KEY_SET_COOKIE).isNotEmpty()
        ) {
            val cookies = response.headers(KEY_SET_COOKIE)
            val cookiesStr = CookiesManager.encodeCookie(cookies)
            CookiesManager.saveCookies(cookiesStr)
        }*/
        return response
    }
}