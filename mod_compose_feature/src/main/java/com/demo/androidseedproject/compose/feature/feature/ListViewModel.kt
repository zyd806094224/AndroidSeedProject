package com.demo.androidseedproject.compose.feature.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.androidseedproject.compose.feature.mvi.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * MVI 模式下的 ListViewModel
 * 负责协调 Intent 处理和状态管理
 */
class ListViewModel : ViewModel() {

    // MVI 核心组件
    private val processor = ListProcessor()

    // 使用 StateFlow 管理状态，符合 MVI 模式
    private val _state = MutableStateFlow(ListState())
    val state: StateFlow<ListState> = _state.asStateFlow()

    init {
        // 初始化时发送加载意图
        handleIntent(ListIntent.LoadInitial)
    }

    /**
     * 处理用户 Intent
     * 这是 MVI 模式的核心入口点
     */
    fun handleIntent(intent: ListIntent) {
        viewModelScope.launch {
            // 首先生成开始的 Action，立即更新 UI 状态
            val startAction = processor.generateStartAction(intent)
            val startState = ListReducer.reduce(_state.value, startAction)
            _state.value = startState

            // 然后处理 Intent 并生成结果 Action
            val resultAction = processor.processIntent(intent, startState)

            // 通过 Reducer 更新状态
            val finalState = ListReducer.reduce(startState, resultAction)
            _state.value = finalState
        }
    }

    /**
     * 清除错误状态
     */
    fun clearError() {
        viewModelScope.launch {
            val action = ListAction.ClearError
            val newState = ListReducer.reduce(_state.value, action)
            _state.value = newState
        }
    }
}