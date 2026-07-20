package com.demo.shared.network

import com.demo.shared.error.ERROR
import com.demo.shared.error.NoNetWorkException

/**
 * 无网络时抛异常的统一入口。供 Api 层调用。
 * （从 NetworkChecker.kt 拆出，避免 commonMain 与 androidMain 同名文件生成的 JVM 类名冲突）
 */
fun ensureNetworkAvailable() {
    if (!isNetworkAvailable()) {
        throw NoNetWorkException(ERROR.NETWORK_ERROR)
    }
}
