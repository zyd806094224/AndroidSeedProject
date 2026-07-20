package com.demo.shared.logger

import android.util.Log

/**
 * Android actual：日志走 android.util.Log。
 */
actual object AppLog {
    actual fun d(tag: String, message: String) {
        Log.d(tag, message)
    }

    actual fun e(tag: String, message: String, throwable: Throwable?) {
        if (throwable != null) Log.e(tag, message, throwable)
        else Log.e(tag, message)
    }
}
