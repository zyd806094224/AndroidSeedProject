package com.demo.main.ui.im

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.alibaba.android.arouter.facade.annotation.Route
import com.demo.common.constant.IM_CONVERSATION_ACTIVITY
import com.demo.common.constant.IM_LOGIN_ACTIVITY
import com.demo.framework.base.BaseMvvmActivity
import com.demo.main.databinding.ActivityImLoginBinding
import com.demo.main.ui.im.viewmodel.ImLoginViewModel
import kotlinx.coroutines.launch

/**
 * IM 登录页
 *
 * @author zhaoyudong
 */
@Route(path = IM_LOGIN_ACTIVITY)
class ImLoginActivity : BaseMvvmActivity<ActivityImLoginBinding, ImLoginViewModel>() {

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.btnLogin.setOnClickListener {
            val username = mBinding.etUsername.text.toString().trim()
            val password = mBinding.etPassword.text.toString().trim()
            if (username.isEmpty() || password.isEmpty()) {
                mBinding.tvTip.text = "用户名和密码不能为空"
                return@setOnClickListener
            }
            mBinding.btnLogin.isEnabled = false
            mBinding.tvTip.text = "登录中..."
            mViewModel.login(username, password)
        }

        // 观察登录结果
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                mViewModel.loginResult.collect { result ->
                    mBinding.btnLogin.isEnabled = true
                    when (result) {
                        is ImLoginViewModel.LoginUiResult.Success -> {
                            mBinding.tvTip.text = "登录成功"
                            ImConversationActivity.start(this@ImLoginActivity)
                            finish()
                        }
                        is ImLoginViewModel.LoginUiResult.Fail -> {
                            mBinding.tvTip.text = result.msg
                        }
                    }
                }
            }
        }
    }
}
