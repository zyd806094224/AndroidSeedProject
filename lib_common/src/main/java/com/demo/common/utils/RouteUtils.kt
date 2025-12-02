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
     * 跳转到测试页面
     * @param context 上下文
     */
    fun toTest(context: Context) {
        navigate(context, TEST_ACTIVITY)
    }

    /**
     * 跳转到Flutter页面
     * @param context 上下文
     * @param initialRoute 初始路由，格式如 "/custom_flutter_page?id=123&name=测试商品"
     */
    fun toFlutter(context: Context, initialRoute: String? = "/custom_flutter_page?id=123&name=测试商品") {
        val postcard = ARouter.getInstance().build(FLUTTER_ACTIVITY)

        // 传递初始路由参数给 FlutterDemoActivity
        initialRoute?.let {
            postcard.withString("initial_route", it)
        }

        postcard.navigation(context)
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
     * 通过外部URL跳转到Flutter页面
     * @param context 上下文
     * @param url 外部URL，格式如 "seedapp://flutter/activity?route=/custom_flutter_page?id=123&name=测试商品"
     * @return 是否成功处理
     */
    fun handleFlutterUrl(context: Context, url: String): Boolean {
        return try {
            if (url.startsWith("seedapp://flutter/activity")) {
                android.util.Log.d("RouteUtils", "原始Flutter URL: $url")

                // 手动解析URL，因为route参数值包含&符号会被getQueryParameter截断
                val routeParam = extractRouteParam(url)

                // 处理URL编码，确保中文等特殊字符正确传递
                val initialRoute = if (!routeParam.isNullOrEmpty()) {
                    java.net.URLDecoder.decode(routeParam, "UTF-8")
                } else {
                    "/custom_flutter_page?id=123&name=测试商品"
                }

                android.util.Log.d("RouteUtils", "Flutter URL解析结果: $initialRoute")
                toFlutter(context, initialRoute)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            android.util.Log.e("RouteUtils", "Flutter URL解析失败: ${e.message}")
            false
        }
    }

    /**
     * 从URL中提取route参数，处理包含&符号的情况
     */
    private fun extractRouteParam(url: String): String? {
        return try {
            // 移除协议和主机部分，只保留查询参数
            val queryStart = url.indexOf('?')
            if (queryStart == -1) return null

            val queryString = url.substring(queryStart + 1)
            android.util.Log.d("RouteUtils", "查询字符串: $queryString")

            // 查找 route= 参数
            val routePrefix = "route="
            val routeIndex = queryString.indexOf(routePrefix)
            if (routeIndex == -1) return null

            // 获取route=后面的所有内容（包括&符号）
            val routeValue = queryString.substring(routeIndex + routePrefix.length)
            android.util.Log.d("RouteUtils", "Route参数值: $routeValue")

            routeValue
        } catch (e: Exception) {
            android.util.Log.e("RouteUtils", "提取route参数失败: ${e.message}")
            null
        }
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

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            // 测试Flutter页面
            android.util.Log.d("RouteUtils", "测试Flutter页面")
            toFlutter(context, "/custom_flutter_page?id=777&name=自动测试&source=testAllRoutes")
        }, 8000)

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            // 测试Flutter外部URL（简单参数）
            android.util.Log.d("RouteUtils", "测试Flutter外部URL（简单参数）")
            val simpleFlutterUrl = "seedapp://flutter/activity?route=/custom_flutter_page?id=888&name=TestName&source=AutoTest"
            android.util.Log.d("RouteUtils", "简单参数Flutter URL: $simpleFlutterUrl")
            handleFlutterUrl(context, simpleFlutterUrl)
        }, 10000)

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            // 测试Flutter外部URL（中文参数）
            android.util.Log.d("RouteUtils", "测试Flutter外部URL（中文参数）")

            // 对中文参数进行URL编码
            val encodedName = java.net.URLEncoder.encode("URL测试", "UTF-8")
            val encodedSource = java.net.URLEncoder.encode("自动测试", "UTF-8")
            val chineseFlutterUrl = "seedapp://flutter/activity?route=/custom_flutter_page?id=999&name=$encodedName&source=$encodedSource"
            android.util.Log.d("RouteUtils", "中文参数Flutter URL: $chineseFlutterUrl")
            handleFlutterUrl(context, chineseFlutterUrl)
        }, 12000)
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