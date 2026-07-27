package com.demo.shared.constant

/**
 * iOS actual：服务端环境配置。
 *
 * 根据 DEBUG 编译标志决定走开发环境还是生产环境。
 * DEBUG 标志由 Xcode 的 Build Configuration 自动注入（Debug 配置默认定义 DEBUG）。
 *
 * @author zhaoyudong
 */

/** 开发环境（本机局域网，明文） */
private const val DEV_BASE_URL = "http://192.168.213.145:8066"
private const val DEV_WS_URL = "ws://192.168.213.145:8066/ws"

/** 生产环境（Nginx 在 8443 端口终止 TLS，反代到 8066） */
private const val PROD_BASE_URL = "https://106.15.7.132:8443"
private const val PROD_WS_URL = "wss://106.15.7.132:8443/ws"

actual val SERVER_BASE_URL: String
    get() = if (IS_DEBUG_ENV) DEV_BASE_URL else PROD_BASE_URL

actual val SERVER_WS_URL: String
    get() = if (IS_DEBUG_ENV) DEV_WS_URL else PROD_WS_URL

// 强制走生产环境（HTTPS）：与 RNHybrid iOS 业务侧（LoginViewController 写死生产地址）保持同源，
// 避免 Debug 构建走局域网 HTTP（192.168.213.145:8066）连不上导致请求挂起。
// 与安卓侧 MyApplication 中 SharedAndroidContext.init(this, false) 的处理思路一致。
actual val IS_DEBUG_ENV: Boolean
    get() = false
