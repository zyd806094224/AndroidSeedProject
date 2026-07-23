package com.demo.shared.repository

/**
 * Android actual：Token + UserId 存储。
 *
 * 当前用内存 volatile 变量做最小实现（便于演示和单元测试）。
 * 生产环境建议替换为 MMKV 或 DataStore。
 */
actual object TokenManager {

    @Volatile
    private var token: String = ""

    @Volatile
    private var userId: String = ""

    actual fun saveToken(token: String) {
        this.token = token
    }

    actual fun getToken(): String = token

    actual fun saveUserId(userId: String) {
        this.userId = userId
    }

    actual fun getUserId(): String = userId

    actual fun clearToken() {
        token = ""
        userId = ""
    }
}
