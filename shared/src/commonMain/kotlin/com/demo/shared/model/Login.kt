package com.demo.shared.model

import kotlinx.serialization.Serializable

/**
 * @Description: 登录业务相关数据模型（commonMain，Android/iOS 共用）
 *
 * 这些是跨平台的纯 Kotlin 数据类，用 kotlinx.serialization 的 [@Serializable] 注解。
 * 注意与 lib_network 的 Gson 模型不互通——shared 内部一律用这套。
 *
 * @author:  zhaoyudong
 * @version: 1.0
 */

/**
 * 登录请求参数
 *
 * @param username 用户名
 * @param password 密码（明文，由 HTTPS 保证传输安全；如需额外加密在 [com.demo.shared.repository.LoginRepository] 内处理）
 * @param deviceId 设备标识，用于服务端做设备管理
 */
@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
    val deviceId: String = ""
)

/**
 * 登录成功后服务端返回的业务数据
 *
 * @param token 登录凭证，后续请求放在 Header 里
 * @param userId 用户 ID
 * @param nickname 昵称
 * @param expireTime token 过期时间戳（毫秒）
 */
@Serializable
data class LoginInfo(
    val token: String,
    val userId: String,
    val nickname: String = "",
    val expireTime: Long = 0L
)
