package com.demo.main.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.demo.common.constant.EDIT_ACTIVITY
import com.demo.common.constant.PARAM_CONTENT
import com.demo.common.constant.PARAM_HINT
import com.demo.framework.base.BaseMvvmActivity
import com.demo.main.databinding.ActivityEdittextInputBinding
import com.demo.main.ui.mine.viewmodel.EditTextInputViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Route(path = EDIT_ACTIVITY)
class EditTextActivity : BaseMvvmActivity<ActivityEdittextInputBinding, EditTextInputViewModel>() {

    @JvmField
    @Autowired
    var content: String? = null

    @JvmField
    @Autowired
    var hint: String? = null

    companion object {
        fun start(context: Context, content: String = "", hint: String = "") {
            ARouter.getInstance()
                .build(EDIT_ACTIVITY)
                .withString(PARAM_CONTENT, content)
                .withString(PARAM_HINT, hint)
                .navigation(context)
        }

        // 传统跳转方式保持兼容
        fun startByIntent(context: Context) {
            val intent = Intent(context, EditTextActivity::class.java)
            context.startActivity(intent)
        }

        // 测试Intent方式传递参数
        fun startWithIntent(context: Context, content: String = "", hint: String = "") {
            val intent = Intent(context, EditTextActivity::class.java)
            intent.putExtra(PARAM_CONTENT, content)
            intent.putExtra(PARAM_HINT, hint)
            context.startActivity(intent)
        }

        // 测试ARouter传递，但使用不同的参数名
        fun startWithDifferentParamNames(context: Context, content: String = "", hint: String = "") {
            ARouter.getInstance()
                .build(EDIT_ACTIVITY)
                .withString("content", content)  // 直接使用字符串字面量
                .withString("hint", hint)      // 直接使用字符串字面量
                .navigation(context)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d("EditTextActivity", "onNewIntent called: ${intent?.extras}")

        // 更新Intent并重新注入
        setIntent(intent)
        ARouter.getInstance().inject(this)

        Log.d("EditTextActivity", "onNewIntent重新注入后 - content: $content, hint: $hint")
    }

    override fun initView(savedInstanceState: Bundle?) {
        //路由注入
        ARouter.getInstance().inject(this)
        // 设置提示文本
        if (!hint.isNullOrEmpty()) {
            mBinding.et.hint = hint
        }

        // 设置初始内容
        if (!content.isNullOrEmpty()) {
            mBinding.et.setText(content)
            // 将光标移动到末尾
            mBinding.et.setSelection(content!!.length)
        } else if (intent?.getStringExtra(PARAM_CONTENT) != null) {
            val intentContent = intent.getStringExtra(PARAM_CONTENT)
            if (!intentContent.isNullOrEmpty()) {
                mBinding.et.setText(intentContent)
                mBinding.et.setSelection(intentContent.length)
            }
        }

        // 设置提示文本
        if (hint.isNullOrEmpty() && intent?.getStringExtra(PARAM_HINT) != null) {
            val intentHint = intent.getStringExtra(PARAM_HINT)
            if (!intentHint.isNullOrEmpty()) {
                mBinding.et.hint = intentHint
            }
        }

        setupSearchWithViewModel()
    }

    private fun setupSearchWithViewModel() {
        // 监听 EditText 输入并更新 ViewModel
        mBinding.et.addTextChangedListener { editable ->
            mViewModel.searchWithQuery(editable.toString())
        }

        // 观察搜索结果
        lifecycleScope.launch {
            mViewModel.searchResults.collect {
                Log.e("zzz", it.toString())
            }
        }
    }
}