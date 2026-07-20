package com.demo.network.api

import com.demo.network.interceptor.NetworkRetryInterceptor
import com.demo.network.response.BaseResponse
import retrofit2.http.GET
import retrofit2.http.Headers

/**
 * @Description:
 * @Date: 2024/8/29 17:09
 * @author:  zhaoyudong
 * @version: 1.0
 */
interface ApiInterface {

    @GET("/dataList")
    suspend fun getDataList(): BaseResponse<MutableList<String>>?

    @Headers(NetworkRetryInterceptor.RETRY_TIME_HEADER)
    @GET("https://106.15.7.132:8443/user/test")
    suspend fun testRequest(): BaseResponse<String>?


    @GET("https://106.15.7.132:8443/user/test2")
    suspend fun test2Request(): BaseResponse<String>?

}