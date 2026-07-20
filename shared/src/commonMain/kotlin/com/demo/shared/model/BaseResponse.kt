package com.demo.shared.model

/**
 * @Description: 跨平台通用响应数据类（commonMain，Android/iOS 共用）
 *               与 lib_network 中的 BaseResponse 实现一致，仅包名不同。
 *               阶段 2 起 Repository 下沉后将直接复用此类型。
 * @Date: 2024/8/29 16:56
 * @author:  zhaoyudong
 * @version: 1.0
 */
data class BaseResponse<out T>(
    val data: T?,
    val errorCode: Int = 0,//服务器状态码 这里0表示请求成功
    val errorMsg: String = ""//错误信息
) {

    /**
     * 判定接口返回是否正常
     */
    fun isFailed(): Boolean {
        return errorCode != 0
    }
}
