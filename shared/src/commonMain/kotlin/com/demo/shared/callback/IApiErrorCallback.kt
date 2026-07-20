package com.demo.shared.callback

/**
 * @Description:  跨平台接口请求错误回调（从 lib_network 下沉）
 * @Date: 2024/8/29 16:59
 * @author:  zhaoyudong
 * @version: 1.0
 */
interface IApiErrorCallback {
    /**
     * 错误回调处理
     */
    fun onError(code: Int?, error: String?) {

    }

    /**
     * 登录失效处理
     */
    fun onLoginFail(code: Int?, error: String?) {

    }
}
