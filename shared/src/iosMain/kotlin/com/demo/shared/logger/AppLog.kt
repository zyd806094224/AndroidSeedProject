package com.demo.shared.logger

/**
 * iOS actual：输出到 stdout，由 Xcode / Console 收集。
 *
 * 不使用 NSLog 的可变参数接口：旧版 Kotlin/Native 把 Kotlin String 传给 `%@`
 * 时可能没有正确桥接成 Objective-C 对象，网络日志一输出就会触发 EXC_BAD_ACCESS。
 */
actual object AppLog {
    actual fun d(tag: String, message: String) {
        println("[$tag] $message")
    }

    actual fun e(tag: String, message: String, throwable: Throwable?) {
        val detail = if (throwable != null) "$message | ${throwable.message ?: ""}" else message
        println("[$tag][ERROR] $detail")
    }
}
