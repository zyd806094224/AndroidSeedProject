package com.demo.main.ui.test.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.demo.network.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * @Description: 测试页面ViewModel
 * @Date: 2025/12/02
 * @author: zhaoyudong
 * @version: 1.0
 */
class TestViewModel : BaseViewModel() {

    private val _state = MutableStateFlow(1) // 必须初始化
    val state = _state.asStateFlow()

    private val _shared = MutableSharedFlow<Int>(replay = 2)
    val shared = _shared.asSharedFlow()

    /**
     * 改变状态
     */
    fun changeState() {
        repeat(10) {
            _state.value = it
        }
    }

    /**
     * 改变共享流
     */
    fun changeShared() {
        viewModelScope.launch {
            repeat(10) {
                _shared.emit(it)
            }
        }
    }

    /**
     * 权限测试结果
     */
    private val _permissionResult = MutableStateFlow<Boolean>(false)
    val permissionResult = _permissionResult.asStateFlow()

    /**
     * 更新权限测试结果
     */
    fun updatePermissionResult(granted: Boolean) {
        _permissionResult.value = granted
        Log.e("TestViewModel", "权限测试结果: $granted")
    }
}