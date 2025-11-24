package com.demo.common.interceptor

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.demo.common.constant.EDIT_ACTIVITY
import com.demo.common.constant.MAIN_ACTIVITY_HOME
import com.demo.common.constant.PARAM_CONTENT
import com.demo.common.constant.PARAM_HINT
import com.demo.common.constant.PARAM_TITLE
import com.demo.common.constant.PARAM_URL
import com.demo.common.constant.WEB_ACTIVITY

/**
 * @Description: 路由拦截器，用于参数验证和处理
 * @Date: 2024/11/24
 * @author: zhaoyudong
 * @version: 1.0
 */
@Interceptor(priority = 8, name = "RouteInterceptor")
class RouteInterceptor : com.alibaba.android.arouter.facade.template.IInterceptor {

    companion object {
        private const val TAG = "RouteInterceptor"
    }

    override fun process(postcard: Postcard, callback: InterceptorCallback) {
        Log.d(TAG, "开始处理路由: ${postcard.path}")

        when (postcard.path) {
            WEB_ACTIVITY -> {
                // 验证WebView页面参数
                val url = postcard.extras.getString(PARAM_URL)
                val title = postcard.extras.getString(PARAM_TITLE)

                Log.d(TAG, "WebView路由拦截 - 收到参数: url=$url, title=$title")
                Log.d(TAG, "WebView路由拦截 - 完整参数: ${postcard.extras}")

                if (url.isNullOrEmpty()) {
                    // URL为空，使用默认URL
                    postcard.withString(PARAM_URL, "http://106.15.7.132:3000/")
                    Log.w(TAG, "WebView URL为空，使用默认URL")
                } else if (!isValidUrl(url)) {
                    // URL格式不正确
                    Log.e(TAG, "WebView URL格式不正确: $url")
                    callback.onInterrupt(RuntimeException("URL格式不正确"))
                    return
                }

                if (title.isNullOrEmpty()) {
                    postcard.withString(PARAM_TITLE, "网页")
                    Log.w(TAG, "WebView title为空，使用默认title")
                }

                Log.d(TAG, "WebView路由验证通过: url=${postcard.extras.getString(PARAM_URL)}, title=${postcard.extras.getString(PARAM_TITLE)}")
            }

            EDIT_ACTIVITY -> {
                // 验证编辑页面参数
                val content = postcard.extras.getString(PARAM_CONTENT)
                val hint = postcard.extras.getString(PARAM_HINT)

                if (hint.isNullOrEmpty()) {
                    postcard.withString(PARAM_HINT, "请输入内容")
                }

                // 内容长度验证
                if (!content.isNullOrEmpty() && content.length > 10000) {
                    Log.e(TAG, "内容过长: ${content.length}")
                    callback.onInterrupt(RuntimeException("内容过长"))
                    return
                }

                Log.d(TAG, "编辑页面路由验证通过: content=${content?.length ?: 0}字符, hint=$hint")
                Log.d(TAG, "编辑页面 - 调用callback.onContinue，即将跳转到Activity")
            }

            MAIN_ACTIVITY_HOME -> {
                // 主页路由处理
                val tabIndex = postcard.extras.getInt("tab", 0)
                if (tabIndex < 0 || tabIndex > 3) {
                    Log.w(TAG, "tab索引异常，重置为0: $tabIndex")
                    postcard.withInt("tab", 0)
                }
                Log.d(TAG, "主页路由验证通过: tab=$tabIndex")
            }
        }

        // 继续路由
        callback.onContinue(postcard)
    }

    override fun init(context: Context?) {
        Log.d(TAG, "RouteInterceptor初始化完成")
    }

    /**
     * 验证URL格式是否正确
     */
    private fun isValidUrl(url: String): Boolean {
        return try {
            // 基本URL格式验证
            val urlPattern = Regex("^(https?|ftp):\\/\\/[^\\s/$.?#].[^\\s]*\$")
            urlPattern.matches(url)
        } catch (e: Exception) {
            Log.e(TAG, "URL验证异常", e)
            false
        }
    }
}