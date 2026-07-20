package com.demo.shared.error

/**
 * @Description: 跨平台 API 异常（从 lib_network 下沉）
 *               注意：原 NoNetWorkException 继承 java.io.IOException，commonMain 无 IO 体系，改为 Exception。
 * @Date: 2024/8/29 17:10
 * @author:  zhaoyudong
 * @version: 1.0
 */
open class ApiException : Exception {
    var errCode: Int
    var errMsg: String

    constructor(error: ERROR, e: Throwable? = null) : super(e) {
        errCode = error.code
        errMsg = error.errMsg
    }

    constructor(code: Int, msg: String, e: Throwable? = null) : super(e) {
        this.errCode = code
        this.errMsg = msg
    }
}

/**
 * 无网络连接异常
 * 注意：commonMain 中没有 java.io.IOException，这里改为继承 Exception。
 */
class NoNetWorkException : Exception {
    var errCode: Int
    var errMsg: String

    constructor(error: ERROR, e: Throwable? = null) : super(e) {
        errCode = error.code
        errMsg = error.errMsg
    }
}
