package com.demo.shared.repository

import com.demo.shared.error.ApiException
import com.demo.shared.error.ERROR
import com.demo.shared.model.LoginInfo
import com.demo.shared.model.LoginRequest
import com.demo.shared.network.Api

/**
 * @Description: 登录业务仓库（commonMain 业务逻辑示例）。
 *
 * 这里演示 KMP 业务逻辑的典型写法——把"一个业务的完整逻辑"封装在 Repository 里，
 * 跨端共用。Repository 的职责：
 *   1. 入参校验（平台无关的纯逻辑）
 *   2. 调用网络接口（[Api]）
 *   3. 处理响应（业务状态码 + token 持久化）
 *   4. 把异常转换为业务可读的 [ApiException]
 *
 * 继承 [BaseRepository] 复用统一的超时 + 状态码校验。
 * ViewModel / iOS ViewController 直接调 [login]，无需关心内部细节。
 *
 * @author:  zhaoyudong
 * @version: 1.0
 */
class LoginRepository : BaseRepository() {

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @param deviceId 设备 ID
     * @return 登录成功后的用户信息（token 已自动持久化到 [TokenManager]）
     * @throws ApiException 入参校验失败 / 网络失败 / 业务状态码非 0
     */
    suspend fun login(username: String, password: String, deviceId: String = ""): LoginInfo {
        // —— 业务逻辑 1：入参校验（平台无关，两端共用同一套规则）——
        validateCredentials(username, password)

        // —— 业务逻辑 2：组装请求 + 调接口（requestResponse 自动处理超时和 errorCode 校验）——
        val result = requestResponse {
            Api.login(LoginRequest(username = username, password = password, deviceId = deviceId))
        }

        // —— 业务逻辑 3：响应处理（理论上有数据，防御性判空）——
        val loginInfo = result ?: throw ApiException(ERROR.PARSE_ERROR)

        // —— 业务逻辑 4：副作用——持久化 token（调 expect/actual 的 TokenManager）——
        TokenManager.saveToken(loginInfo.token)

        return loginInfo
    }

    /**
     * 退出登录：清除本地 token。
     * 不调服务端接口（如需通知服务端，在此处加 Api 调用）。
     */
    fun logout() {
        TokenManager.clearToken()
    }

    /**
     * 判断当前是否已登录（根据本地是否有 token）。
     * UI 层（Android Activity / iOS ViewController）用来决定显示登录页还是主页。
     */
    fun isLoggedIn(): Boolean {
        return TokenManager.getToken().isNotEmpty()
    }

    /**
     * 入参校验：两端共用同一套规则。
     *
     * 把校验逻辑写在 commonMain 的好处：
     *   - 改一处，Android/iOS 同步生效，不会出现两端校验不一致
     *   - 单元测试可以覆盖（见 SharedModuleTest 的 login 校验用例）
     *
     * @throws ApiException 校验不通过
     */
    @Throws(ApiException::class)
    private fun validateCredentials(username: String, password: String) {
        if (username.isBlank()) {
            throw ApiException(ERROR.UNKNOWN.code, "用户名不能为空")
        }
        if (password.isBlank()) {
            throw ApiException(ERROR.UNKNOWN.code, "密码不能为空")
        }
        if (username.length < 3) {
            throw ApiException(ERROR.UNKNOWN.code, "用户名至少 3 个字符")
        }
        if (password.length < 6) {
            throw ApiException(ERROR.UNKNOWN.code, "密码至少 6 个字符")
        }
    }
}
