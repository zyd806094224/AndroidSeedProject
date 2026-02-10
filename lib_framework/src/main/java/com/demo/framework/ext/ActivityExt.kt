package com.demo.framework.ext

import android.app.Activity
import android.app.Application
import android.os.Bundle
import kotlinx.coroutines.*
import java.util.*

/**
 * 缓存 Activity 对应的协程作用域
 * 使用 WeakHashMap 以避免内存泄漏
 */
private val activityScopes = WeakHashMap<Activity, CoroutineScope>()

/**
 * 生命周期回调监听器，负责在 Activity 销毁时清理并取消协程作用域
 */
private val scopeLifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        synchronized(activityScopes) {
            activityScopes.remove(activity)?.cancel()
        }
    }
}

private var isLifecycleRegistered = false

/**
 * 为 Activity 提供协程作用域支持。
 * 1. 即使不继承 ComponentActivity 也能使用（类似 lifecycleScope）。
 * 2. 自动随 Activity 的 onDestroy 生命周期释放。
 * 3. 默认运行在 Dispatchers.Main.immediate（与 lifecycleScope 一致）。
 */
val Activity.activityScope: CoroutineScope
    get() {
        // 自动注册全局生命周期监听，只需执行一次
        if (!isLifecycleRegistered) {
            synchronized(scopeLifecycleCallbacks) {
                if (!isLifecycleRegistered) {
                    (applicationContext as? Application)?.registerActivityLifecycleCallbacks(scopeLifecycleCallbacks)
                    isLifecycleRegistered = true
                }
            }
        }

        return synchronized(activityScopes) {
            // 如果已存在则返回，否则创建新的
            activityScopes[this] ?: CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).also {
                // 如果 Activity 已经销毁，直接取消
                if (isDestroyed || isFinishing) {
                    it.cancel()
                } else {
                    activityScopes[this] = it
                }
            }
        }
    }
