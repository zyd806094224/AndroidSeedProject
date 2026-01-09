package com.demo.framework.ext

import android.accounts.NetworkErrorException

sealed class RequestState<T> {


    // 加载开始
    class RequestStart<T> : RequestState<T>()

    // 加载成功
    class RequestSuccess<T>(val result: T) : RequestState<T>()

    // 加载错误
    class RequestError<T>(
        val code: Int,
        val message: String,
        val throwable: Throwable? = NetworkErrorException("网络错误，请重试")
    ) : RequestState<T>()


    // 请求完成
    class RequestCompleted<T> : RequestState<T>()
}