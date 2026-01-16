package com.demo.androidseedproject.callback

import android.content.Context
import android.content.Intent
import android.util.Log
import com.demo.androidseedproject.BuildConfig
import com.qihoo360.replugin.RePluginCallbacks

/**
 * 宿主针对RePlugin的自定义行为
 */
class HostCallbacks(context: Context) : RePluginCallbacks(context) {
    override fun onPluginNotExistsForActivity(context: Context, plugin: String, intent: Intent, process: Int): Boolean {
        // FIXME 当插件"没有安装"时触发此逻辑，可打开您的"下载对话框"并开始下载。
        // FIXME 其中"intent"需传递到"对话框"内，这样可在下载完成后，打开这个插件的Activity
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onPluginNotExistsForActivity: Start download... p=$plugin; i=$intent")
        }
        return super.onPluginNotExistsForActivity(context, plugin, intent, process)
    }

    companion object {
        private const val TAG = "HostCallbacks"
    }
}