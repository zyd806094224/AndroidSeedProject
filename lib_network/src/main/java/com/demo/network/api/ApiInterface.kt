package com.demo.network.api

import com.demo.network.response.BaseResponse
import retrofit2.http.GET

/**
 * @Description:
 * @Date: 2024/8/29 17:09
 * @author:  zhaoyudong
 * @version: 1.0
 */
interface ApiInterface {

    @GET("/dataList")
    suspend fun getDataList(): BaseResponse<MutableList<String>>?

}