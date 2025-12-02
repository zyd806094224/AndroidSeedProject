package com.demo.common.config

import com.demo.common.constant.*

/**
 * @Description: 路由配置管理
 * @Date: 2024/11/24
 * @author: zhaoyudong
 * @version: 1.0
 */
object RouteConfig {

    /**
     * 支持的路由页面映射
     */
    val ROUTE_MAP = mapOf(
        "web" to WEB_ACTIVITY,
        "edit" to EDIT_ACTIVITY,
        "main" to MAIN_ACTIVITY_HOME,
        "home" to HOME_FRAGMENT,
        "mine" to MINE_FRAGMENT,
        "splash" to SPLASH_ACTIVITY
    )

    /**
     * 支持的参数类型
     */
    enum class ParamType {
        STRING,
        INT,
        BOOLEAN,
        LONG,
        FLOAT
    }

    /**
     * 参数配置
     */
    data class ParamConfig(
        val key: String,
        val type: ParamType,
        val required: Boolean = false,
        val defaultValue: Any? = null,
        val description: String = ""
    )

    /**
     * 各路由页面的参数配置
     */
    val ROUTE_PARAM_CONFIG = mapOf(
        WEB_ACTIVITY to listOf(
            ParamConfig(PARAM_URL, ParamType.STRING, required = false, defaultValue = "http://106.15.7.132:3000/", description = "网页URL"),
            ParamConfig(PARAM_TITLE, ParamType.STRING, required = false, defaultValue = "网页", description = "页面标题"),
            ParamConfig(PARAM_SHOW_SHARE, ParamType.BOOLEAN, required = false, defaultValue = false, description = "是否显示分享按钮")
        ),
        EDIT_ACTIVITY to listOf(
            ParamConfig(PARAM_CONTENT, ParamType.STRING, required = false, defaultValue = "", description = "编辑内容"),
            ParamConfig(PARAM_HINT, ParamType.STRING, required = false, defaultValue = "请输入内容", description = "输入提示")
        ),
        MAIN_ACTIVITY_HOME to listOf(
            ParamConfig(PARAM_TAB_INDEX, ParamType.INT, required = false, defaultValue = 0, description = "底部导航索引")
        )
    )

    /**
     * 获取路由页面的参数配置
     */
    fun getParamConfig(routePath: String): List<ParamConfig> {
        return ROUTE_PARAM_CONFIG[routePath] ?: emptyList()
    }

    /**
     * 获取默认参数
     */
    fun getDefaultParams(routePath: String): Map<String, Any> {
        return getParamConfig(routePath)
            .filter { it.defaultValue != null }
            .associate { it.key to it.defaultValue!! }
    }

    /**
     * 验证必需参数
     */
    fun validateRequiredParams(routePath: String, params: Map<String, Any>): Boolean {
        val requiredParams = getParamConfig(routePath).filter { it.required }
        return requiredParams.all { param ->
            params.containsKey(param.key) && params[param.key] != null
        }
    }

    /**
     * 构建完整路由URL（用于服务端下发）
     */
    fun buildRouteUrl(routeKey: String, params: Map<String, String> = emptyMap()): String {
        val routePath = ROUTE_MAP[routeKey] ?: return ""
        return if (params.isEmpty()) {
            "$APP_SCHEME://$routePath"
        } else {
            val paramString = params.entries.joinToString("&") { (key, value) ->
                "$key=${java.net.URLEncoder.encode(value, "UTF-8")}"
            }
            "$APP_SCHEME://$routePath?$paramString"
        }
    }

    /**
     * 示例路由URL
     */
    fun getExampleUrls(): List<String> {
        return listOf(
            // WebView页面
            buildRouteUrl("web", mapOf(
                PARAM_URL to "https://www.baidu.com",
                PARAM_TITLE to "百度一下",
                PARAM_SHOW_SHARE to "true"
            )),

            // 编辑页面
            buildRouteUrl("edit", mapOf(
                PARAM_CONTENT to "默认编辑内容",
                PARAM_HINT to "请输入您的评论"
            )),

            // 主页
            buildRouteUrl("main", mapOf(
                PARAM_TAB_INDEX to "2"
            )),

            // 简单路由
            buildRouteUrl("web"),
            buildRouteUrl("edit"),
            buildRouteUrl("main")
        )
    }
}