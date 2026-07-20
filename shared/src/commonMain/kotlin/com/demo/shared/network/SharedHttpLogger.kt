package com.demo.shared.network

import io.ktor.client.plugins.logging.Logger
import com.demo.shared.logger.AppLog

/**
 * Ktor 日志输出器：通过 expect/actual 的 [AppLog] 适配到各平台日志系统。
 * - Android actual：android.util.Log
 * - iOS actual：os_log / NSLog
 */
object SharedHttpLogger : Logger {
    override fun log(message: String) {
        AppLog.d("SharedHttp", message)
    }
}
