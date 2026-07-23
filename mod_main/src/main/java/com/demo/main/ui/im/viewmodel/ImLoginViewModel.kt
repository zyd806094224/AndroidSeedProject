package com.demo.main.ui.im.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.shared.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * IM 登录 ViewModel
 *
 * @author zhaoyudong
 */
class ImLoginViewModel : ViewModel() {

    private val loginUseCase = LoginUseCase()

    /** 登录结果事件（一次性） */
    private val _loginResult = MutableSharedFlow<LoginUiResult>()
    val loginResult: SharedFlow<LoginUiResult> = _loginResult.asSharedFlow()

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val result = loginUseCase.execute(
                username = username,
                password = password,
                deviceId = android.os.Build.DEVICE
            )
            when (result) {
                is LoginUseCase.LoginResult.Success ->
                    _loginResult.emit(LoginUiResult.Success(result.token))
                is LoginUseCase.LoginResult.Fail ->
                    _loginResult.emit(LoginUiResult.Fail(result.errMsg))
            }
        }
    }

    sealed class LoginUiResult {
        data class Success(val token: String) : LoginUiResult()
        data class Fail(val msg: String) : LoginUiResult()
    }
}
