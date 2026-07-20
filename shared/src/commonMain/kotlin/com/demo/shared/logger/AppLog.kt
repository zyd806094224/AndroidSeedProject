package com.demo.shared.logger

/**
 * 跨平台日志门面（expect/actual）。
 * commonMain 只声明接口，各平台实现对接到原生日志系统。
 */
expect object AppLog {
    fun d(tag: String, message: String)
    fun e(tag: String, message: String, throwable: Throwable? = null)
}
