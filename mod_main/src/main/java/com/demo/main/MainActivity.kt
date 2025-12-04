package com.demo.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.alibaba.android.arouter.facade.annotation.Route
import com.demo.common.constant.MAIN_ACTIVITY_HOME
import com.demo.common.service.RouterService
import com.demo.framework.base.BaseDataBindActivity
import com.demo.framework.utils.StatusBarSettingHelper
import com.demo.main.databinding.ActivityMainBinding

/**
 * @Description: 主页
 * @Date: 2024/8/30 14:19
 * @author:  zhaoyudong
 * @version: 1.0
 */
@Route(path = MAIN_ACTIVITY_HOME)
class MainActivity : BaseDataBindActivity<ActivityMainBinding>() {

    private lateinit var navController: NavController

    companion object {
        fun start(context: Context, index: Int = 0) {
            val intent = Intent(context, MainActivity::class.java)
//            intent.putExtra(KEY_INDEX, index)
            context.startActivity(intent)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        val navView = mBinding.navView

        //1.寻找出路由控制器对象，它是路由跳转的唯一入口，找到宿主NavHostFragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment

        //2.获取NavController，使用标准Navigation组件
        navController = navHostFragment.navController

        //3.将NavController和BottomNavigationView绑定，形成联动效果
        try {
            navView.setupWithNavController(navController)
        } catch (e: Exception) {
            e.printStackTrace()
            // 备用导航方案
            navView.setOnNavigationItemSelectedListener { item ->
                when(item.itemId) {
                    R.id.navi_home -> {
                        navController.navigate(R.id.navi_home)
                        true
                    }
                    R.id.navi_mine -> {
                        navController.navigate(R.id.navi_mine)
                        true
                    }
                    else -> false
                }
            }
        }
        StatusBarSettingHelper.setStatusBarTranslucent(this)
        StatusBarSettingHelper.statusBarLightMode(this@MainActivity, true)

        //6.处理外部Intent
        handleExternalIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleExternalIntent(intent)
    }

    /**
     * 处理外部Intent
     */
    private fun handleExternalIntent(intent: Intent?) {
        intent?.let {
            val uri = it.data
            if (uri != null) {
                Log.d("MainActivity", "收到外部Intent: $uri")
                Log.d("MainActivity", "Intent完整信息: action=${it.action}, dataString=${it.dataString}, extras=${it.extras}")
                Log.d("MainActivity", "Uri详细信息 - scheme=${uri.scheme}, host=${uri.host}, path=${uri.path}, query=${uri.query}")

                val handled = when {
                    uri.scheme == "seedapp" -> {
                        // 处理seedapp://协议
                        RouterService.handleExternalRoute(this, uri.toString())
                    }
                    uri.scheme in listOf("http", "https") && uri.host in listOf("seedapp.com", "www.seedapp.com") -> {
                        // 处理HTTP/HTTPS协议
                        val path = uri.path?.substring(1) // 移除开头的'/'
                        if (!path.isNullOrEmpty()) {
                            val routeUrl = "seedapp://$path${uri.query?.let { "?$it" } ?: ""}"
                            RouterService.handleExternalRoute(this, routeUrl)
                        } else {
                            false
                        }
                    }
                    else -> {
                        Log.w("MainActivity", "不支持的URI协议: ${uri.scheme}")
                        false
                    }
                }

                if (handled) {
                    Log.d("MainActivity", "外部路由处理成功")
                } else {
                    Log.w("MainActivity", "外部路由处理失败，使用默认逻辑")
                    handleFallbackRoute(uri)
                }
            }
        }
    }

    /**
     * 处理回退路由
     */
    private fun handleFallbackRoute(uri: Uri) {
        // 如果有URL参数，打开WebView
        val url = uri.getQueryParameter("url")
        if (!url.isNullOrEmpty()) {
            val title = uri.getQueryParameter("title") ?: "网页"
            RouterService.navigateToWebView(this, url, title)
        } else {
            // 默认跳转到主页
            val tabIndex = uri.getQueryParameter("tab")?.toIntOrNull() ?: 0
            RouterService.navigateToMain(this, tabIndex)
        }
    }

}