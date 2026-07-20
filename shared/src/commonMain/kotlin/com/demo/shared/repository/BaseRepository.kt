package com.demo.shared.repository

import com.demo.shared.constant.DEFAULT_TIMEOUT_MILLIS
import com.demo.shared.error.ApiException
import com.demo.shared.model.BaseResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

/**
 * @Description: 跨平台基础仓库（从 lib_network 下沉，纯 Kotlin 协程实现）
 *               超时时间统一为 [DEFAULT_TIMEOUT_MILLIS]（原 lib_network 版本也是 30s）。
 * @Date: 2024/8/29 17:14
 * @author:  zhaoyudong
 * @version: 1.0
 */
open class BaseRepository {

    /**
     * IO中处理请求
     */
    suspend fun <T> requestResponse(requestCall: suspend () -> BaseResponse<T>?): T? {
        // 注意：commonMain 中 Dispatchers.IO 不保证所有平台可用（iOS Native 无默认 IO 实现），
        // 这里用 Dispatchers.Default（commonMain 保证可用）。Android 上 Default 同样是后台线程。
        val response = withContext(Dispatchers.Default) {
            withTimeout(DEFAULT_TIMEOUT_MILLIS) {
                requestCall()
            }
        } ?: return null

        if (response.isFailed()) {
            throw ApiException(response.errorCode, response.errorMsg)
        }
        return response.data
    }
}
