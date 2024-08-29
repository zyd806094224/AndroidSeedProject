package com.demo.network.repository

import com.demo.network.error.ApiException
import com.demo.network.response.BaseResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

/**
 * @Description: 基础仓库
 * @Date: 2024/8/29 17:14
 * @author:  zhaoyudong
 * @version: 1.0
 */
open class BaseRepository {

    /**
     * IO中处理请求
     */
    suspend fun <T> requestResponse(requestCall: suspend () -> BaseResponse<T>?): T? {
        val response = withContext(Dispatchers.IO) {
            withTimeout(10 * 1000) {
                requestCall()
            }
        } ?: return null

        if (response.isFailed()) {
            throw ApiException(response.errorCode, response.errorMsg)
        }
        return response.data
    }
}