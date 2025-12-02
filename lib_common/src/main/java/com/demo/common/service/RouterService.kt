package com.demo.common.service

import android.content.Context
import android.net.Uri
import android.util.Log
import com.alibaba.android.arouter.launcher.ARouter
import com.demo.common.constant.*
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * @Description: 路由服务中心，处理外部URL和路由跳转
 * @Date: 2024/11/24
 * @author: zhaoyudong
 * @version: 1.0
 */
object RouterService {

    private const val TAG = "RouterService"

    /**
     * 处理外部路由URL
     * @param context 上下文
     * @param url 外部URL，如: seedapp://web/activity?url=https://example.com&title=网页
     * @return 是否成功处理
     */
    fun handleExternalRoute(context: Context, url: String): Boolean {
        return try {
            Log.d(TAG, "处理外部路由URL: $url")

            if (!url.startsWith(APP_SCHEME)) {
                Log.w(TAG, "URL格式不支持，不是APP的Scheme: $url")
                return false
            }

            val uri = Uri.parse(url)

            // 正确解析路径：scheme://host/path?query
            // 对于 seedapp://web/activity，host="web", path="/activity"
            // 我们需要组合成完整路径 /web/activity
            val host = uri.host ?: ""
            val path = uri.path ?: ""
            val fullPath = if (host.isNotEmpty() && path.isNotEmpty()) {
                "/$host$path"
            } else if (host.isNotEmpty()) {
                "/$host"
            } else {
                path
            }

            val params = parseUriParams(uri)

            Log.d(TAG, "解析路由 - host: $host, path: $path, fullPath: $fullPath, params: $params")

            // 验证路由是否存在
            if (!isRouteExist(fullPath)) {
                Log.e(TAG, "路由不存在: $fullPath")
                return false
            }

            // 构建ARouter路由
            val postcard = ARouter.getInstance().build(fullPath)

            Log.d(TAG, "准备构建路由 - path: $fullPath, params: $params")

            // 添加参数
            params.forEach { (key, value) ->
                when {
                    key == PARAM_URL || key == PARAM_TITLE || key == PARAM_CONTENT || key == PARAM_HINT -> {
                        postcard.withString(key, value)
                        Log.d(TAG, "添加String参数: $key = $value")
                    }
                    key == PARAM_TAB_INDEX -> {
                        val intValue = value.toIntOrNull() ?: 0
                        postcard.withInt(key, intValue)
                        Log.d(TAG, "添加Int参数: $key = $intValue")
                    }
                    key == PARAM_SHOW_SHARE -> {
                        val boolValue = value.toBoolean()
                        postcard.withBoolean(key, boolValue)
                        Log.d(TAG, "添加Boolean参数: $key = $boolValue")
                    }
                    key == PARAM_USER_ID -> {
                        val longValue = value.toLongOrNull() ?: 0L
                        postcard.withLong(key, longValue)
                        Log.d(TAG, "添加Long参数: $key = $longValue")
                    }
                    else -> {
                        postcard.withString(key, value)
                        Log.d(TAG, "添加其他String参数: $key = $value")
                    }
                }
            }

            // 执行跳转
            postcard.navigation(context)
            Log.d(TAG, "路由跳转成功: $fullPath")
            true

        } catch (e: Exception) {
            Log.e(TAG, "处理外部路由失败", e)
            false
        }
    }

    /**
     * 解析Uri参数
     */
    private fun parseUriParams(uri: Uri): Map<String, String> {
        val params = mutableMapOf<String, String>()

        Log.d(TAG, "解析URI参数 - uri: $uri")
        Log.d(TAG, "解析URI参数 - query: ${uri.query}")
        Log.d(TAG, "解析URI参数 - queryParameterNames: ${uri.queryParameterNames}")

        // 解析查询参数
        uri.queryParameterNames.forEach { key ->
            val value = uri.getQueryParameter(key)
            Log.d(TAG, "解析URI参数 - key: $key, value: $value")
            if (value != null) {
                // URL解码
                val decodedValue = URLDecoder.decode(value, "UTF-8")
                Log.d(TAG, "解析URI参数 - key: $key, decodedValue: $decodedValue")
                params[key] = decodedValue
            }
        }

        Log.d(TAG, "解析URI参数 - 最终params: $params")
        return params
    }

    /**
     * 验证路由是否存在
     */
    private fun isRouteExist(path: String): Boolean {
        val knownRoutes = setOf(
            MAIN_ACTIVITY_HOME,
            WEB_ACTIVITY,
            EDIT_ACTIVITY,
            SPLASH_ACTIVITY,
            HOME_FRAGMENT,
            MINE_FRAGMENT
        )
        return knownRoutes.contains(path)
    }

    /**
     * 构建外部路由URL
     */
    fun buildRouteUrl(path: String, params: Map<String, String> = emptyMap()): String {
        val baseUrl = "$APP_SCHEME://$path"
        if (params.isEmpty()) return baseUrl

        val paramString = params.entries.joinToString("&") { (key, value) ->
            "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value, "UTF-8")}"
        }

        return "$baseUrl?$paramString"
    }

    /**
     * 快速跳转到WebView页面
     */
    fun navigateToWebView(context: Context, url: String, title: String = "网页") {
        val routeUrl = buildRouteUrl(WEB_ACTIVITY, mapOf(
            PARAM_URL to url,
            PARAM_TITLE to title
        ))
        handleExternalRoute(context, routeUrl)
    }

    /**
     * 快速跳转到编辑页面
     */
    fun navigateToEdit(context: Context, content: String = "", hint: String = "请输入内容") {
        val routeUrl = buildRouteUrl(EDIT_ACTIVITY, mapOf(
            PARAM_CONTENT to content,
            PARAM_HINT to hint
        ))
        handleExternalRoute(context, routeUrl)
    }

    /**
     * 快速跳转到主页
     */
    fun navigateToMain(context: Context, tabIndex: Int = 0) {
        val routeUrl = buildRouteUrl(MAIN_ACTIVITY_HOME, mapOf(
            PARAM_TAB_INDEX to tabIndex.toString()
        ))
        handleExternalRoute(context, routeUrl)
    }

    /**
     * 路由配置信息
     */
    fun getRouteConfig(): RouteConfigInfo {
        return RouteConfigInfo(
            scheme = APP_SCHEME,
            supportedRoutes = mapOf(
                "web" to WEB_ACTIVITY,
                "edit" to EDIT_ACTIVITY,
                "main" to MAIN_ACTIVITY_HOME,
                "home" to HOME_FRAGMENT,
                "mine" to MINE_FRAGMENT
            ),
            supportedParams = listOf(
                PARAM_URL,
                PARAM_TITLE,
                PARAM_TAB_INDEX,
                PARAM_CONTENT,
                PARAM_HINT,
                PARAM_SHOW_SHARE,
                PARAM_USER_ID
            )
        )
    }
}

/**
 * 路由配置信息
 */
data class RouteConfigInfo(
    val scheme: String,
    val supportedRoutes: Map<String, String>,
    val supportedParams: List<String>
)