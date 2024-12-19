package com.demo.universaldialog.utils

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import android.provider.Settings

object Utils {
    private var density = 0f
    private var height = 0
    private var width: Int = 0
    private var debug = 0

    fun isDebug(context: Context): Boolean {
        if (debug == 0) {
            val isDebug = context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
            debug = if (isDebug) 1 else 2
        }
        return debug == 1
    }

    /**
     * dp2px
     */
    fun dip2px(ctx: Context, dpValue: Float): Int {
        if (density == 0f) density =
            ctx.resources.displayMetrics.density
        return (dpValue * density + 0.5f).toInt()
    }

    fun getScreenHeight(context: Context): Int {
        if (height == 0) {
            height =
                context.resources.displayMetrics.heightPixels
        }
        return height
    }

    fun getScreenWidth(context: Context): Int {
        if (width == 0) {
            width =
                context.resources.displayMetrics.widthPixels
        }
        return width
    }

    /**
     * 是否有悬浮窗权限
     */
    fun hasPermission(context: Activity):Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else true
    }
}