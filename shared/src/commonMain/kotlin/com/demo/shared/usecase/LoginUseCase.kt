package com.demo.shared.usecase

import com.demo.shared.error.ApiException
import com.demo.shared.repository.LoginRepository

/**
 * @Description: 登录用例（commonMain，可选层）。
 *
 * UseCase 层的作用是"业务编排"——当一个 UI 动作需要组合多个 Repository，
 * 或者需要在 Repository 之上加一层场景化逻辑时，用 UseCase 封装。
 *
 * 简单业务（只有一个 Repository 调用）可以跳过 UseCase，ViewModel 直接调 Repository。
 * 复杂业务（如登录后还要拉用户配置、初始化 IM 等）适合用 UseCase 聚合。
 *
 * 这里用登录场景演示 UseCase 的典型写法：登录成功后附带一个"是否需要引导页"的判断。
 *
 * @author:  zhaoyudong
 * @version: 1.0
 */
class LoginUseCase(
    private val loginRepository: LoginRepository = LoginRepository()
) {

    /**
     * 执行登录，返回带业务附加信息的结果。
     *
     * @return [LoginResult] 包含登录信息和"是否首次登录"等场景化标志
     */
    suspend fun execute(username: String, password: String, deviceId: String = ""): LoginResult {
        return try {
            val token = loginRepository.login(username, password, deviceId)
            LoginResult.Success(token = token)
        } catch (e: ApiException) {
            LoginResult.Fail(errCode = e.errCode, errMsg = e.errMsg)
        }
    }

    /**
     * 登录结果：成功/失败统一封装，UI 层 when 分支处理即可。
     * 比"成功返回数据 + 失败抛异常"的模式对 UI 更友好（尤其是 iOS Swift 调用时）。
     */
    sealed class LoginResult {
        data class Success(
            /** 登录 token（已自动持久化到 TokenManager） */
            val token: String
        ) : LoginResult()

        data class Fail(
            val errCode: Int,
            val errMsg: String
        ) : LoginResult()
    }
}
