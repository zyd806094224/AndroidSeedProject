package com.demo.shared.network

import com.demo.shared.error.ERROR
import com.demo.shared.error.NoNetWorkException

/**
 * expect 声明：当前网络是否可用。
 * - Android actual：用 ConnectivityManager
 * - iOS actual：用 NWPathMonitor 缓存状态
 *
 * commonMain 在发起请求前调用，无网络时直接抛 [NoNetWorkException]，避免等超时。
 */
expect fun isNetworkAvailable(): Boolean
