package com.demo.androidseedproject.callback

import android.content.Context
import android.util.Log
import com.demo.androidseedproject.BuildConfig
import com.qihoo360.replugin.RePluginEventCallbacks

class HostEventCallbacks(context: Context?) : RePluginEventCallbacks(context) {
    override fun onInstallPluginFailed(path: String, code: InstallResult) {
        // FIXME 当插件安装失败时触发此逻辑。您可以在此处做“打点统计”，也可以针对安装失败情况做“特殊处理”
        // 大部分可以通过RePlugin.install的返回值来判断是否成功
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onInstallPluginFailed: Failed! path=$path; r=$code")
        }
        super.onInstallPluginFailed(path, code)
    }

    override fun onStartActivityCompleted(plugin: String, activity: String, result: Boolean) {
        // FIXME 当打开Activity成功时触发此逻辑，可在这里做一些APM、打点统计等相关工作
        super.onStartActivityCompleted(plugin, activity, result)
    }

    companion object {
        private const val TAG = "HostEventCallbacks"
    }
}