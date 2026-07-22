package com.demo.shared.constant

/**
 * 服务端环境配置（expect/actual）
 *
 * commonMain 只定义取值接口，各平台根据构建类型（Debug/Release）返回不同的地址：
 *   - Android：由 [com.demo.shared.network.SharedAndroidContext.isDebug] 决定（Application.onCreate 时传 BuildConfig.DEBUG）
 *   - iOS：由编译标志（DEBUG）决定
 *
 * 使用方直接引用 [SERVER_BASE_URL] / [SERVER_WS_URL]，无需关心当前环境。
 *
 * @author zhaoyudong
 */

/** 当前环境的 HTTP 基础地址 */
expect val SERVER_BASE_URL: String

/** 当前环境的 WebSocket 地址 */
expect val SERVER_WS_URL: String

/** 当前是否为开发环境 */
expect val IS_DEBUG_ENV: Boolean
