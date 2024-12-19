package com.demo.universaldialog.impl

import android.app.Activity
import com.demo.universaldialog.DialogConfig
import com.demo.universaldialog.UniversalDialog
import com.demo.universaldialog.floatdialog.FloatDialog
import com.demo.universaldialog.generaldialog.GeneralDialog
import com.demo.universaldialog.interfaces.UniversalDialogCreator
import com.demo.universaldialog.utils.Utils
import com.demo.universaldialog.windowdialog.WindowDialog

/**
 * 内部通用弹窗生成器
 */
object DialogCreatorImpl : UniversalDialogCreator {

    /**
     * 创建浮层类型的弹窗，区分有没有浮层权限
     */
    override fun createFloatDialog(context: Activity, dialogConfig: DialogConfig) : UniversalDialog {
        return if (dialogConfig.dialogDataConfig.isGlobal()
            && dialogConfig.dialogDataConfig.isUseSystemFloatingWindow()
            && Utils.hasPermission(context)
        ) {
            WindowDialog(context, dialogConfig)
        } else {
            FloatDialog(context, dialogConfig)
        }
    }

    /**
     * 创建普通类型的弹窗
     */
    override fun createGeneralDialog(context: Activity, dialogConfig: DialogConfig) : UniversalDialog {
        return GeneralDialog(context, dialogConfig)
    }


}