package com.demo.universaldialog.generaldialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.demo.universaldialog.DialogConfig
import com.demo.universaldialog.R
import com.demo.universaldialog.UniversalDialog
import com.demo.universaldialog.enums.ShowFrom
import com.demo.universaldialog.enums.XLocation
import com.demo.universaldialog.enums.YLocation
import com.demo.universaldialog.utils.StatusBarUtil
import com.demo.universaldialog.utils.Utils

/**
 * 普通弹窗的实现
 */
open class GeneralDialog(context: Activity, val dialogConfig: DialogConfig) : Dialog(context, R.style.universal_general_style),
    UniversalDialog {
    /**
     * 延时隐藏弹窗
     */
    private val handler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            if(msg.what == 1){
                dismiss()
            }
        }
    }

    /**
     * 是否已经设置过View了
     */
    private var isSet = false

    /**
     * 弹窗view生成展示隐藏控制类
     */
    private var generalViewOperator = GeneralViewOperator(context, dialogConfig)

    /**
     * 创建View并设置到Dialog上
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(isSet)return
        generalViewOperator.onAttach(this, false)
    }

    /**
     * 展示弹窗
     */
    override fun show() {
        try {
            if(!isShowing) {
                super.show()
                if(dialogConfig.dialogDataConfig.getShowTime() > 0){
                    handler.sendEmptyMessageDelayed(1,
                    dialogConfig.dialogDataConfig.getShowTime() * 1000L)
                }
                dialogConfig.dialogCallback?.onDialogShow()
            }
        }catch (e:Exception){
            if(Utils.isDebug(context))e.printStackTrace()
        }
    }

    /**
     * 隐藏弹窗
     */
    override fun dismiss() {
        try {
            if(isShowing) {
                super.dismiss()
                handler.removeMessages(1)
                dialogConfig.dialogCallback?.onDialogClose()
            }
        }catch (e:Exception){
            if(Utils.isDebug(context))e.printStackTrace()
        }
    }

    override fun dialogShow(activity: Activity) {
        show()
    }

    override fun dialogClose() {
        dismiss()
    }

    override fun isInShow() : Boolean {
        return isShowing
    }
}