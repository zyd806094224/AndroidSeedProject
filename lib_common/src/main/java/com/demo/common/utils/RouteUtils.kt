package com.demo.common.utils

import android.content.Context
import com.demo.common.constant.*
import com.demo.common.service.RouterService
import com.alibaba.android.arouter.launcher.ARouter
import com.demo.common.config.RouteConfig

/**
 * @Description: 路由工具类，提供便捷的路由跳转方法
 * @Date: 2024/11/24
 * @author: zhaoyudong
 * @version: 1.0
 */
object RouteUtils {

    /**
     * 跳转到WebView页面
     * @param context 上下文
     * @param url 网页URL
     * @param title 页面标题
     * @param showShare 是否显示分享按钮
     */
    fun toWebView(
        context: Context,
        url: String = "http://106.15.7.132:3000/",
        title: String = "网页",
        showShare: Boolean = false
    ) {
        RouterService.navigateToWebView(context, url, title)
    }

    /**
     * 跳转到编辑页面
     * @param context 上下文
     * @param content 默认内容
     * @param hint 输入提示
     */
    fun toEdit(
        context: Context,
        content: String = "",
        hint: String = "请输入内容"
    ) {
        RouterService.navigateToEdit(context, content, hint)
    }

    /**
     * 跳转到主页
     * @param context 上下文
     * @param tabIndex 底部导航索引 (0=首页, 1=我的)
     */
    fun toMain(context: Context, tabIndex: Int = 0) {
        RouterService.navigateToMain(context, tabIndex)
    }

    /**
     * 通过ARouter直接跳转
     * @param path 路由路径
     * @param params 参数
     * @param context 上下文
     */
    fun navigate(context: Context, path: String, params: Map<String, Any> = emptyMap()) {
        val postcard = ARouter.getInstance().build(path)
        params.forEach { (key, value) ->
            when (value) {
                is String -> postcard.withString(key, value)
                is Int -> postcard.withInt(key, value)
                is Boolean -> postcard.withBoolean(key, value)
                is Long -> postcard.withLong(key, value)
                is Float -> postcard.withFloat(key, value)
                is Double -> postcard.withDouble(key, value)
                else -> postcard.withString(key, value.toString())
            }
        }
        postcard.navigation(context)
    }

    /**
     * 处理外部URL
     * @param context 上下文
     * @param url 外部URL
     * @return 是否成功处理
     */
    fun handleExternalUrl(context: Context, url: String): Boolean {
        return RouterService.handleExternalRoute(context, url)
    }

    /**
     * 测试所有路由跳转
     */
    fun testAllRoutes(context: Context) {
        android.util.Log.d("RouteUtils", "开始测试所有路由跳转")

        // 测试WebView页面
        android.util.Log.d("RouteUtils", "测试WebView页面")
        toWebView(context, "https://www.baidu.com", "百度搜索")

        // 延迟测试其他路由
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            // 测试编辑页面
            android.util.Log.d("RouteUtils", "测试编辑页面")
            toEdit(context, "测试内容", "请输入文字")
        }, 2000)

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            // 测试主页
            android.util.Log.d("RouteUtils", "测试主页")
            toMain(context, 1)
        }, 4000)

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            // 测试外部URL
            android.util.Log.d("RouteUtils", "测试外部URL")
            val externalUrl = "seedapp://web/activity?url=https://github.com&title=GitHub"
            handleExternalUrl(context, externalUrl)
        }, 6000)
    }

    /**
     * 打印路由配置信息
     */
    fun logRouteConfig() {
        val config = RouterService.getRouteConfig()
        android.util.Log.d("RouteUtils", """
            路由配置信息:
            - Scheme: ${config.scheme}
            - 支持的路由: ${config.supportedRoutes.entries.joinToString { "${it.key}=${it.value}" }}
            - 支持的参数: ${config.supportedParams.joinToString(", ")}

            示例路由URL:
            ${RouteConfig.getExampleUrls().joinToString("\n            ")}
        """.trimIndent())
    }
}