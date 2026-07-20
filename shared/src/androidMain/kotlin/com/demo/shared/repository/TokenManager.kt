package com.demo.shared.repository

/**
 * Android actual：Token 存储。
 *
 * 当前用内存 Map 做最小实现（便于演示和单元测试）。
 * 生产环境建议替换为 MMKV 或 DataStore，例如：
 *   ```kotlin
 *   actual object TokenManager {
 *       private const val KEY_TOKEN = "kmp_token"
 *       actual fun saveToken(token: String) = MMKV.defaultMMKV().encode(KEY_TOKEN, token)
 *       actual fun getToken(): String = MMKV.defaultMMKV().decodeString(KEY_TOKEN) ?: ""
 *       actual fun clearToken() = MMKV.defaultMMKV().removeValueForKey(KEY_TOKEN)
 *   }
 *   ```
 * （注意：用 MMKV 需在 shared 的 androidMain 依赖里加 mmkv，并在 SharedAndroidContext.init 后初始化 MMKV）
 */
actual object TokenManager {

    @Volatile
    private var token: String = ""

    actual fun saveToken(token: String) {
        this.token = token
    }

    actual fun getToken(): String = token

    actual fun clearToken() {
        token = ""
    }
}
