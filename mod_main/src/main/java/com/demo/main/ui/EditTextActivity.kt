package com.demo.main.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.demo.framework.base.BaseMvvmActivity
import com.demo.main.databinding.ActivityEdittextInputBinding
import com.demo.main.ui.mine.viewmodel.EditTextInputViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class EditTextActivity : BaseMvvmActivity<ActivityEdittextInputBinding,EditTextInputViewModel>(){


    companion object {
        fun start(context: Context) {
            val intent = Intent(context, EditTextActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
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