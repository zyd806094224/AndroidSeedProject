package com.demo.shared.repository

/**
 * @Description: 跨平台 Token 存储抽象（expect/actual）。
 *
 * 业务逻辑（LoginRepository）需要"保存 token / 读取 token"，
 * 但 commonMain 不能直接访问 Android 的 MMKV 或 iOS 的 UserDefaults，
 * 所以用 expect 声明接口，各平台 actual 实现对接到原生存储。
 *
 * 这就是 KMP 处理"需要平台能力"的标准套路：
 *   业务逻辑在 commonMain 调 expect → 编译时分派到 androidMain/iosMain 的 actual。
 *
 * @author:  zhaoyudong
 * @version: 1.0
 */
expect object TokenManager {

    /**
     * 保存登录 token
     */
    fun saveToken(token: String)

    /**
     * 读取登录 token，未登录返回空串
     */
    fun getToken(): String

    /**
     * 保存当前登录用户ID
     */
    fun saveUserId(userId: String)

    /**
     * 读取当前登录用户ID，未登录返回空串
     */
    fun getUserId(): String

    /**
     * 清除 token（退出登录时调用）
     */
    fun clearToken()
}
