package com.demo.shared.constant

import com.demo.shared.network.SharedAndroidContext

/**
 * Android actual：服务端环境配置。
 *
 * 根据 [SharedAndroidContext.isDebug] 决定走开发环境还是生产环境。
 * isDebug 在 Application.onCreate 中通过 SharedAndroidContext.init(this, BuildConfig.DEBUG) 设置。
 * 切换环境只需改构建类型（debug/release），无需改代码。
 *
 * @author zhaoyudong
 */

/** 开发环境（本机局域网） */
private const val DEV_BASE_URL = "http://192.168.213.145:8066"
private const val DEV_WS_URL = "ws://192.168.213.145:8066/ws"

/** 生产环境 */
private const val PROD_BASE_URL = "http://106.15.7.132:8066"
private const val PROD_WS_URL = "ws://106.15.7.132:8066/ws"

actual val SERVER_BASE_URL: String
    get() = if (SharedAndroidContext.isDebug) DEV_BASE_URL else PROD_BASE_URL

actual val SERVER_WS_URL: String
    get() = if (SharedAndroidContext.isDebug) DEV_WS_URL else PROD_WS_URL

actual val IS_DEBUG_ENV: Boolean
    get() = SharedAndroidContext.isDebug
