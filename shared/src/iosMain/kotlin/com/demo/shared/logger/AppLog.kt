package com.demo.shared.logger

import platform.Foundation.NSLog

/**
 * iOS actual：日志走 NSLog。
 * 注意：NSLog 的格式化参数需用 NSString，Kotlin String 会自动桥接。
 */
actual object AppLog {
    actual fun d(tag: String, message: String) {
        NSLog("%@: %@", tag, message)
    }

    actual fun e(tag: String, message: String, throwable: Throwable?) {
        val detail = if (throwable != null) "$message | ${throwable.message ?: ""}" else message
        NSLog("%@ [ERROR]: %@", tag, detail)
    }
}
