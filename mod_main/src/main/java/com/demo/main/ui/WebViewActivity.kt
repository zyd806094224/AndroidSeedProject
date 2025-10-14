package com.demo.main.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.demo.framework.base.BaseMvvmActivity
import com.demo.main.databinding.ActivityWebviewBinding
import com.demo.main.ui.mine.viewmodel.WebViewViewModel

class WebViewActivity : BaseMvvmActivity<ActivityWebviewBinding, WebViewViewModel>(){

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, WebViewActivity::class.java)
            context.startActivity(intent)
        }
    }
    @SuppressLint("SetJavaScriptEnabled")
    override fun initView(savedInstanceState: Bundle?) {
        mBinding.webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
        }
        mBinding.webView.loadUrl("http://106.15.7.132:3000/")
    }


}