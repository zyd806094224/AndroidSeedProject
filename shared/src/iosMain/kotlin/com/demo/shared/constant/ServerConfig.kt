package com.demo.shared.constant

/**
 * iOS actual：服务端环境配置。
 *
 * 根据 DEBUG 编译标志决定走开发环境还是生产环境。
 * DEBUG 标志由 Xcode 的 Build Configuration 自动注入（Debug 配置默认定义 DEBUG）。
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
    get() = if (IS_DEBUG_ENV) DEV_BASE_URL else PROD_BASE_URL

actual val SERVER_WS_URL: String
    get() = if (IS_DEBUG_ENV) DEV_WS_URL else PROD_WS_URL

actual val IS_DEBUG_ENV: Boolean
    get() = Platform.isDebugBinary
