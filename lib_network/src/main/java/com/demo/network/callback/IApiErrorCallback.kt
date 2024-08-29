package com.demo.network.callback

/**
 * @Description:  接口请求错误回调
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