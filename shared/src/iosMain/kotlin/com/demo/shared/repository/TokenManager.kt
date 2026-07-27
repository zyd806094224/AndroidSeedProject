package com.demo.shared.repository

import platform.Foundation.NSUserDefaults

/**
 * iOS actual：Token 仅保存在进程内存中，由宿主 App 的 Keychain 在启动/登录时注入；
 * UserId 继续使用 NSUserDefaults 保存。
 *
 * 避免 KMP 再把宿主已安全保存到 Keychain 的 token 明文复制到 NSUserDefaults。
 */
actual object TokenManager {

    private const val KEY_USER_ID = "kmp_user_id"

    private var token: String = ""

    private val defaults: NSUserDefaults
        get() = NSUserDefaults.standardUserDefaults()

    actual fun saveToken(token: String) {
        this.token = token
    }

    actual fun getToken(): String {
        return token
    }

    actual fun saveUserId(userId: String) {
        defaults.setObject(userId, forKey = KEY_USER_ID)
    }

    actual fun getUserId(): String {
        return defaults.stringForKey(KEY_USER_ID) ?: ""
    }

    actual fun clearToken() {
        token = ""
        defaults.removeObjectForKey(KEY_USER_ID)
    }
}
