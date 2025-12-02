package com.demo.main.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.demo.common.constant.PARAM_TITLE
import com.demo.common.constant.PARAM_URL
import com.demo.common.constant.WEB_ACTIVITY
import com.demo.framework.base.BaseMvvmActivity
import com.demo.main.databinding.ActivityWebviewBinding
import com.demo.main.ui.mine.viewmodel.WebViewViewModel

@Route(path = WEB_ACTIVITY)
class WebViewActivity : BaseMvvmActivity<ActivityWebviewBinding, WebViewViewModel>() {

    @Autowired
    @JvmField
    var url: String? = null

    @Autowired
    @JvmField
    var title: String? = null

    companion object {
        fun start(context: Context, url: String = "", title: String = "") {
            ARouter.getInstance()
                .build(WEB_ACTIVITY)
                .withString(PARAM_URL, url)
                .withString(PARAM_TITLE, title)
                .navigation(context)
        }

        // 传统跳转方式保持兼容
        fun startByIntent(context: Context) {
            val intent = Intent(context, WebViewActivity::class.java)
            context.startActivity(intent)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView(savedInstanceState: Bundle?) {
        // 注入ARouter参数
        ARouter.getInstance().inject(this)
        // 调试日志：打印接收到的参数
        android.util.Log.d("WebViewActivity", "收到的参数 - url: $url, title: $title")
        android.util.Log.d("WebViewActivity", "Intent参数 - ${intent?.extras}")

        // 优先获取参数，顺序：ARouter注入 > Intent传递 > 默认值
        val finalUrl = when {
            !url.isNullOrEmpty() -> url
            intent?.getStringExtra(PARAM_URL) != null -> intent.getStringExtra(PARAM_URL)
            else -> "http://106.15.7.132:3000/"
        }

        val finalTitle = when {
            !title.isNullOrEmpty() -> {
                android.util.Log.d("WebViewActivity", "使用ARouter注入的title: $title")
                title
            }

            intent?.getStringExtra(PARAM_TITLE) != null -> {
                val intentTitle = intent.getStringExtra(PARAM_TITLE)
                android.util.Log.d("WebViewActivity", "使用Intent的title: $intentTitle")
                intentTitle
            }

            else -> {
                android.util.Log.d("WebViewActivity", "使用默认title")
                "网页"
            }
        }

        android.util.Log.d("WebViewActivity", "最终使用的参数 - url: $finalUrl, title: $finalTitle")

        if (!finalTitle.isNullOrEmpty()) {
            try {
                supportActionBar?.title = finalTitle
                android.util.Log.d("WebViewActivity", "设置ActionBar标题成功: $finalTitle")
            } catch (e: Exception) {
                android.util.Log.e("WebViewActivity", "设置ActionBar标题失败", e)
            }

        }

        mBinding.webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
        }

        // 加载URL
        finalUrl?.let { mBinding.webView.loadUrl(it) }
    }


}