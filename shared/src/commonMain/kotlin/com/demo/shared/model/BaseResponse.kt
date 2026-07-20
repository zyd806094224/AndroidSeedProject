package com.demo.shared.model

import kotlinx.serialization.Serializable

/**
 * @Description: 跨平台通用响应数据类（commonMain，Android/iOS 共用）。
 *
 * 字段与服务端 Result.java 完全对齐（com.zyd...common.core.domain.Result）：
 *   - code: 200 表示成功，其他为失败
 *   - msg:  提示信息
 *   - total: 列表场景的总数（非列表接口为 0）
 *   - data: 业务数据
 *
 * 注意：lib_network 版（Gson）仍保留旧的 errorCode/errorMsg 字段，两套互不影响。
 * @Date: 2024/8/29 16:56
 * @author:  zhaoyudong
 * @version: 1.0
 */
@Serializable
data class BaseResponse<out T>(
    val code: Int = 0,//服务器状态码，200 表示成功（与 Result.SUCCESS_CODE 对齐）
    val msg: String = "",//提示信息
    val total: Long = 0L,//列表总数，非列表接口为 0
    val data: T? = null//业务数据
) {

    /**
     * 判定接口返回是否正常（服务端 Result.success() 用 code=200）
     */
    fun isFailed(): Boolean {
        return code != 200
    }
}
