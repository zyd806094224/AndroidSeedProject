package com.demo.main.ui.mine.viewmodel

import androidx.lifecycle.viewModelScope
import com.demo.network.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * @Description:
 * @Date: 2024/9/3 17:29
 * @author:  zhaoyudong
 * @version: 1.0
 */
class MineViewModel : BaseViewModel() {

    private val _state = MutableStateFlow(1) // 必须初始化
    val state = _state.asStateFlow()

    private val _shared = MutableSharedFlow<Int>(replay = 2)
    val shared = _shared.asSharedFlow()


    fun changeState() {
        repeat(10){
            _state.value = it
        }
    }

    fun changeShared() {
        viewModelScope.launch {
            repeat(10){
                _shared.emit(it)
            }
        }
    }

}