package com.demo.main.ui.im

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.demo.common.constant.IM_USER_LIST_ACTIVITY
import com.demo.framework.base.BaseMvvmActivity
import com.demo.main.databinding.ActivityImUserListBinding
import com.demo.main.ui.im.adapter.ImUserAdapter
import com.demo.main.ui.im.viewmodel.ImUserListViewModel
import kotlinx.coroutines.launch

/**
 * IM 用户列表页（选择联系人发起聊天）
 *
 * @author zhaoyudong
 */
@Route(path = IM_USER_LIST_ACTIVITY)
class ImUserListActivity : BaseMvvmActivity<ActivityImUserListBinding, ImUserListViewModel>() {

    private val adapter = ImUserAdapter()

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        mBinding.recyclerView.adapter = adapter

        mBinding.ivBack.setOnClickListener { finish() }

        // 点击用户 → 进入聊天页
        adapter.onItemClickListener = { _, position ->
            val item = adapter.getItem(position)
            if (item != null) {
                val name = if (item.nickName.isNotEmpty()) item.nickName else item.userName
                ImChatActivity.start(this, 0L, item.userId, name)
            }
        }

        // 观察用户列表
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                mViewModel.users.collect { list ->
                    adapter.setData(list)
                }
            }
        }
    }

    override fun initData() {
        mViewModel.loadUsers()
    }
}
