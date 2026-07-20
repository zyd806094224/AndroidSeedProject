package com.demo.shared.repository

import platform.Foundation.NSUserDefaults

/**
 * iOS actual：Token 存储用 NSUserDefaults。
 * 简单轻量，适合存字符串 token。敏感数据生产环境建议用 Keychain。
 */
actual object TokenManager {

    private const val KEY_TOKEN = "kmp_token"

    private val defaults: NSUserDefaults
        get() = NSUserDefaults.standardUserDefaults()

    actual fun saveToken(token: String) {
        defaults.setObject(token, forKey = KEY_TOKEN)
    }

    actual fun getToken(): String {
        return defaults.stringForKey(KEY_TOKEN) ?: ""
    }

    actual fun clearToken() {
        defaults.removeObjectForKey(KEY_TOKEN)
    }
}
