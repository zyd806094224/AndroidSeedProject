package com.demo.main.ui.im

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.demo.common.constant.IM_CONVERSATION_ACTIVITY
import com.demo.common.constant.IM_USER_LIST_ACTIVITY
import com.demo.framework.base.BaseMvvmActivity
import com.demo.main.databinding.ActivityImConversationBinding
import com.demo.main.ui.im.adapter.ImConversationAdapter
import com.demo.main.ui.im.viewmodel.ImConversationViewModel
import kotlinx.coroutines.launch

/**
 * IM 会话列表页
 *
 * @author zhaoyudong
 */
@Route(path = IM_CONVERSATION_ACTIVITY)
class ImConversationActivity : BaseMvvmActivity<ActivityImConversationBinding, ImConversationViewModel>() {

    private val adapter = ImConversationAdapter()

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerView.adapter = adapter

        // 点击会话进入聊天页
        adapter.onItemClickListener = { _, position ->
            val item = adapter.getItem(position)
            if (item != null) {
                ImChatActivity.start(this, item.conversationId, item.targetId, item.targetName)
            }
        }

        // 发起新聊天 → 跳转到用户列表页选择联系人
        mBinding.tvNewChat.setOnClickListener {
            ImUserListActivity::class.java
            com.alibaba.android.arouter.launcher.ARouter.getInstance()
                .build(IM_USER_LIST_ACTIVITY)
                .navigation(this)
        }

        // 观察会话列表
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                mViewModel.conversations.collect { list ->
                    adapter.setData(list)
                }
            }
        }
    }

    override fun initData() {
        mViewModel.init()
    }

    override fun onResume() {
        super.onResume()
        // 从聊天页返回时刷新会话列表
        mViewModel.loadConversations()
    }

    companion object {
        fun start(context: Context) {
            com.alibaba.android.arouter.launcher.ARouter.getInstance()
                .build(IM_CONVERSATION_ACTIVITY)
                .navigation(context)
        }
    }
}
