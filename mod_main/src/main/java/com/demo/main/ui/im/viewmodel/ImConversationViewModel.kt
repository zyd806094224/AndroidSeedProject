package com.demo.main.ui.im.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.shared.model.ImConversation
import com.demo.shared.model.MsgType
import com.demo.shared.repository.TokenManager
import com.demo.shared.usecase.ChatUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 会话列表 ViewModel
 *
 * @author zhaoyudong
 */
class ImConversationViewModel : ViewModel() {

    private val chatUseCase = ChatUseCase()

    private val _conversations = MutableStateFlow<List<ImConversation>>(emptyList())
    val conversations: StateFlow<List<ImConversation>> = _conversations.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    /** WebSocket 是否已连接 */
    private var wsStarted = false

    /** 是否已订阅消息流 */
    private var observingMessages = false

    /**
     * 初始化：连接 WebSocket + 订阅消息流 + 拉取会话列表
     */
    fun init() {
        if (!wsStarted) {
            wsStarted = true
            viewModelScope.launch {
                val token = TokenManager.getToken()
                if (token.isNotEmpty()) {
                    chatUseCase.start(token)
                }
            }
        }
        observeMessages()
        loadConversations()
    }

    /**
     * 订阅 WebSocket 消息流，收到消息时实时更新会话列表
     */
    private fun observeMessages() {
        if (observingMessages) return
        observingMessages = true
        viewModelScope.launch {
            chatUseCase.observeMessages().collect { msg ->
                updateConversationForMessage(msg)
            }
        }
    }

    /**
     * 收到实时消息后，更新对应会话的最后消息摘要和未读数。
     *
     * 判断"是不是发给自己的"：消息的 senderId 或 receiverId 有一个出现在
     * 已有会话列表的 targetId 里，就说明是相关消息。
     * 如果 receiverId 匹配某个会话的 targetId → 自己是发送方（对方收到）。
     * 如果 senderId 匹配某个会话的 targetId → 对方发来的，未读数 +1。
     */
    private fun updateConversationForMessage(msg: com.demo.shared.model.ImMessage) {
        val summary = if (MsgType.of(msg.msgType) == MsgType.IMAGE) "[图片]" else msg.content.take(100)
        val list = _conversations.value.toMutableList()
        var changed = false

        for (i in list.indices) {
            val conv = list[i]
            // 这条消息属于当前会话（对方是 senderId 或 receiverId 之一 == conv.targetId）
            if (conv.targetId == msg.senderId || conv.targetId == msg.receiverId) {
                // senderId == targetId → 对方发来的 → 未读 +1
                val isReceived = msg.senderId == conv.targetId
                list[i] = conv.copy(
                    lastMsgContent = summary,
                    lastMsgTime = msg.sendTime,
                    unreadCount = if (isReceived) conv.unreadCount + 1 else conv.unreadCount
                )
                changed = true
                break
            }
        }

        // 会话不在列表里（对方首次发消息），尝试新建
        if (!changed) {
            val currentUserId = TokenManager.getUserId().toLongOrNull()
            if (currentUserId != null) {
                val isReceiver = msg.receiverId == currentUserId
                val targetId = if (msg.senderId == currentUserId) msg.receiverId else msg.senderId
                list.add(ImConversation(
                    conversationId = msg.conversationId,
                    targetId = targetId,
                    targetName = "用户${targetId}",
                    lastMsgContent = summary,
                    lastMsgTime = msg.sendTime,
                    unreadCount = if (isReceiver) 1 else 0
                ))
                changed = true
            }
        }

        if (changed) {
            list.sortByDescending { it.lastMsgTime }
            _conversations.value = list
        }
    }

    fun loadConversations() {
        viewModelScope.launch {
            _loading.value = true
            val token = TokenManager.getToken()
            android.util.Log.d("ImConvVM", "loadConversations: token=${token.take(10)}..., userId=${TokenManager.getUserId()}")
            val result = chatUseCase.getConversations()
            _loading.value = false
            android.util.Log.d("ImConvVM", "loadConversations result: $result")
            when (result) {
                is ChatUseCase.ListResult.Success -> _conversations.value = result.data
                is ChatUseCase.ListResult.Fail -> _conversations.value = emptyList()
            }
        }
    }
}
