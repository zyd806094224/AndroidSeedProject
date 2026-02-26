package com.demo.main.ui.test.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.demo.network.flow.requestFlow
import com.demo.network.manager.ApiManager
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

    fun testNetWorkRequest(){
        viewModelScope.launch {
            val data = requestFlow<String?>(requestCall = {
                ApiManager.api.testRequest()
            }, errorBlock = { code, error ->
                null
            })
            Log.e("zzz",data.toString())
        }
    }

    fun testNetWorkRequest2(){
        viewModelScope.launch {
            val data = requestFlow<String?>(requestCall = {
                ApiManager.api.test2Request()
            }, errorBlock = { code, error ->
                null
            })
            Log.e("zzz",data.toString())
        }
    }

    /**
     * launchUI 使用示例
     * 运行在主线程中，不需要处理返回值，适合简单的异步操作
     */
    fun launchUIExample() {
        launchUI(errorBlock = { code, error ->
            // 错误回调
            Log.e("TestViewModel", "launchUI 错误: code=$code, error=$error")
        }) {
            // 在这里执行需要异步处理的任务
            val data = requestFlow<String?>(requestCall = {
                ApiManager.api.testRequest()
            }, errorBlock = { code, error ->
                null
            })
            Log.e("TestViewModel", "launchUI 结果: ${data.toString()}")
        }
    }

    /**
     * launchUIWithResult 使用示例
     * 运行在主线程中，可以处理返回值，适合需要获取API响应数据的场景
     */
    fun launchUIWithResultExample() {
        launchUIWithResult(
            responseBlock = {
                // API请求
                ApiManager.api.testRequest()
            },
            errorCall = object : com.demo.network.callback.IApiErrorCallback {
                override fun onError(code: Int?, error: String?) {
                    // 普通错误回调
                    Log.e("TestViewModel", "launchUIWithResult 错误: code=$code, error=$error")
                }

                override fun onLoginFail(code: Int?, error: String?) {
                    // 登录失效回调
                    Log.e("TestViewModel", "launchUIWithResult 登录失效: code=$code, error=$error")
                }
            }
        ) { result ->
            // 成功回调，处理返回的数据
            Log.e("TestViewModel", "launchUIWithResult 结果: ${result.toString()}")
        }
    }

    /**
     * launchFlow 使用示例
     * 运行在主线程中，基于Flow实现，支持Loading状态管理
     */
    fun launchFlowExample() {
        launchFlow(
            errorCall = object : com.demo.network.callback.IApiErrorCallback {
                override fun onError(code: Int?, error: String?) {
                    // 普通错误回调
                    Log.e("TestViewModel", "launchFlow 错误: code=$code, error=$error")
                }

                override fun onLoginFail(code: Int?, error: String?) {
                    // 登录失效回调
                    Log.e("TestViewModel", "launchFlow 登录失效: code=$code, error=$error")
                }
            },
            requestCall = {
                // API请求
                ApiManager.api.testRequest()
            },
            showLoading = { isLoading ->
                // Loading状态回调
                Log.e("TestViewModel", "launchFlow Loading: $isLoading")
            }
        ) { result ->
            // 成功回调，处理返回的数据
            Log.e("TestViewModel", "launchFlow 结果: ${result.toString()}")
        }
    }

    /**
     * launchFlow 简化版使用示例
     * 不传errorCall和showLoading时，可以使用更简洁的写法
     */
    fun launchFlowSimpleExample() {
        launchFlow(
            requestCall = {
                ApiManager.api.test2Request()
            }
        ) { result ->
            Log.e("TestViewModel", "launchFlow简化版 结果: ${result.toString()}")
        }
    }
}