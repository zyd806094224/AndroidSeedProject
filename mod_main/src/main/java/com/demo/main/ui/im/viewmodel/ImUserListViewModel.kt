package com.demo.main.ui.im.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.shared.model.SimpleUser
import com.demo.shared.usecase.ChatUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 用户列表 ViewModel
 *
 * @author zhaoyudong
 */
class ImUserListViewModel : ViewModel() {

    private val chatUseCase = ChatUseCase()

    private val _users = MutableStateFlow<List<SimpleUser>>(emptyList())
    val users: StateFlow<List<SimpleUser>> = _users.asStateFlow()

    fun loadUsers() {
        viewModelScope.launch {
            val result = chatUseCase.getChatUsers()
            when (result) {
                is ChatUseCase.ListResult.Success -> _users.value = result.data
                is ChatUseCase.ListResult.Fail -> _users.value = emptyList()
            }
        }
    }
}
