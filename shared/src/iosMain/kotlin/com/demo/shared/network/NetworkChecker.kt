package com.demo.shared.network

/**
 * iOS actual：网络状态检测。
 *
 * 当前实现：直接返回 true（默认可用）。
 *
 * 完整实现需要 NWPathMonitor（异步 API），涉及 Kotlin/Native 的 cinterop 细节，
 * 本项目阶段 3 只做 iOS 编译验证，不实跑 iOS，故此处留作 TODO。
 * 实际接入 iOS 时可参考：
 *   ```kotlin
 *   import platform.Network.NWPathMonitor
 *   import platform.Network.nw_path_status_satisfied
 *   private val monitor = NWPathMonitor()
 *   init { monitor.start(dispatch_get_main_queue()) }
 *   ```
 */
actual fun isNetworkAvailable(): Boolean = true
