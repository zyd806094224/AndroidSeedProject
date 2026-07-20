package com.demo.shared.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Android actual：网络状态检测。
 *
 * 需要先调用 [SharedAndroidContext.init] 注入 Application Context（建议在 Application.onCreate 中调用）。
 * 用 ConnectivityManager + NetworkCapabilities 判断（API ≥ 23 的标准写法）。
 */

/**
 * 跨平台模块的 Android Context 容器，需在 App 启动时初始化。
 */
object SharedAndroidContext {
    @Volatile
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    internal fun get(): Context =
        appContext ?: error("SharedAndroidContext 未初始化，请先在 Application.onCreate 调用 SharedAndroidContext.init(this)")
}

@SuppressLint("MissingPermission")
actual fun isNetworkAvailable(): Boolean {
    val context = SharedAndroidContext.get()
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        ?: return false
    val network = cm.activeNetwork ?: return false
    val caps = cm.getNetworkCapabilities(network) ?: return false
    return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
