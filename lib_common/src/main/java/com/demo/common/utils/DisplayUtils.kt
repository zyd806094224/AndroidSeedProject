package com.demo.common.utils

import android.content.Context
import android.util.TypedValue

/**
 * @Description:
 * @Date: 2024/9/24 10:31
 * @author:  zhaoyudong
 * @version: 1.0
 */
object DisplayUtils {
    /**
     * 将 dp 转换为 px
     * @param context 上下文，用于获取设备的屏幕密度
     * @param dpValue dp 值
     * @return 转换后的 px 值
     */
    fun dpToPx(context: Context, dpValue: Float): Int {
        val density = context.resources.displayMetrics.density
        return (dpValue * density + 0.5f).toInt()
    }

    /**
     * 将 px 转换为 dp
     * @param context 上下文，用于获取设备的屏幕密度
     * @param pxValue px 值
     * @return 转换后的 dp 值
     */
    fun pxToDp(context: Context, pxValue: Float): Int {
        val density = context.resources.displayMetrics.density
        return (pxValue / density + 0.5f).toInt()
    }

    /**
     * 将 sp 转换为 px
     * @param context 上下文，用于获取设备的屏幕缩放密度
     * @param spValue sp 值
     * @return 转换后的 px 值
     */
    fun spToPx(context: Context, spValue: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, spValue, context.resources.displayMetrics
        ).toInt()
    }

    /**
     * 将 px 转换为 sp
     * @param context 上下文，用于获取设备的屏幕缩放密度
     * @param pxValue px 值
     * @return 转换后的 sp 值
     */
    fun pxToSp(context: Context, pxValue: Float): Int {
        val scaledDensity = context.resources.displayMetrics.scaledDensity
        return (pxValue / scaledDensity + 0.5f).toInt()
    }
}