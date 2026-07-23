package com.demo.main.ui.im

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.demo.common.constant.IM_CHAT_ACTIVITY
import com.demo.common.constant.PARAM_CONVERSATION_ID
import com.demo.common.constant.PARAM_TARGET_NAME
import com.demo.common.constant.PARAM_USER_ID
import com.demo.framework.base.BaseMvvmActivity
import com.demo.main.databinding.ActivityImChatBinding
import com.demo.main.ui.im.adapter.ImMessageAdapter
import com.demo.main.ui.im.viewmodel.ImChatViewModel
import com.demo.shared.repository.TokenManager
import com.demo.shared.usecase.ChatUseCase
import kotlinx.coroutines.launch

/**
 * IM 聊天页
 *
 * @author zhaoyudong
 */
@Route(path = IM_CHAT_ACTIVITY)
class ImChatActivity : BaseMvvmActivity<ActivityImChatBinding, ImChatViewModel>() {

    @Autowired(name = PARAM_CONVERSATION_ID)
    @JvmField
    var conversationId: Long = 0L

    @Autowired(name = PARAM_USER_ID)
    @JvmField
    var targetId: Long = 0L

    @Autowired(name = PARAM_TARGET_NAME)
    @JvmField
    var targetName: String = ""

    private val chatUseCase = ChatUseCase()

    private lateinit var adapter: ImMessageAdapter

    override fun initView(savedInstanceState: Bundle?) {
        ARouter.getInstance().inject(this)

        // 当前登录用户ID（登录时已通过 /getInfo 获取并存入 TokenManager）
        mViewModel.currentUserId = TokenManager.getUserId().toLongOrNull() ?: 0L

        // 标题
        mBinding.tvTitle.text = if (targetName.isNotEmpty()) targetName else "用户${targetId}"

        // RecyclerView
        adapter = ImMessageAdapter(mViewModel.currentUserId)
        mBinding.recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        mBinding.recyclerView.adapter = adapter

        // 返回
        mBinding.ivBack.setOnClickListener { finish() }

        // 发送
        mBinding.btnSend.setOnClickListener {
            val text = mBinding.etInput.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener
            mBinding.etInput.text.clear()
            mViewModel.sendMessage(targetId, text) { success, _ ->
                if (success) scrollToBottom()
            }
        }

        // 观察消息列表变化
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                mViewModel.messages.collect { list ->
                    // ViewModel 推断出 currentUserId 后同步给 adapter（getInfo 可能失败导致初始为 0）
                    if (adapter.currentUserId != mViewModel.currentUserId) {
                        adapter.currentUserId = mViewModel.currentUserId
                    }
                    adapter.setData(list)
                    scrollToBottom()
                }
            }
        }

        // 订阅实时消息（对方发来的）
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                chatUseCase.observeMessages().collect { msg ->
                    // 只接收当前会话相关的消息
                    if (msg.senderId == targetId || msg.receiverId == targetId) {
                        mViewModel.onReceiveMessage(msg)
                    }
                }
            }
        }
    }

    override fun initData() {
        mViewModel.init(conversationId, targetId)
    }

    private fun scrollToBottom() {
        mBinding.recyclerView.post {
            val count = adapter.itemCount
            if (count > 0) {
                mBinding.recyclerView.smoothScrollToPosition(count - 1)
            }
        }
    }

    companion object {
        fun start(context: Context, conversationId: Long, targetId: Long, targetName: String) {
            ARouter.getInstance()
                .build(IM_CHAT_ACTIVITY)
                .withLong(PARAM_CONVERSATION_ID, conversationId)
                .withLong(PARAM_USER_ID, targetId)
                .withString(PARAM_TARGET_NAME, targetName)
                .navigation(context)
        }
    }
}
