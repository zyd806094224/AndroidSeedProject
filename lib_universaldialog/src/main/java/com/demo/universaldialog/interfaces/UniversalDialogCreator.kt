package com.demo.universaldialog.interfaces

import android.app.Activity
import com.demo.universaldialog.DialogConfig
import com.demo.universaldialog.UniversalDialog
import com.demo.universaldialog.floatdialog.FloatDialog
import com.demo.universaldialog.generaldialog.GeneralDialog

/**
 * 通用弹窗生成器，一般情况下不用自定义，除非想继承和扩展弹窗
 * 悬浮弹窗使用[FloatDialog]
 * 普通弹窗使用[GeneralDialog]
 */
interface UniversalDialogCreator {
    /**
     * 创建悬浮弹窗
     * [activity] 页面的activity
     * [dialogConfig] 通用弹窗的配置
     * @return [UniversalDialog]
     */
    fun createFloatDialog(activity: Activity, dialogConfig: DialogConfig) : UniversalDialog

    /**
     * 创建普通背景不能点击的弹窗
     * [activity] 页面的activity
     * [dialogConfig] 通用弹窗的配置
     * @return [UniversalDialog]
     */
    fun createGeneralDialog(activity: Activity, dialogConfig: DialogConfig) : UniversalDialog
}