package com.demo.universaldialog.windowdialog

import android.app.Activity
import android.app.Application
import android.content.Context.WINDOW_SERVICE
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.WindowManager
import com.demo.universaldialog.DialogConfig
import com.demo.universaldialog.UniversalDialog
import com.demo.universaldialog.UniversalDialogSDK
import com.demo.universaldialog.utils.Utils
import com.demo.universaldialog.view.CommonViewOperator

/**
 * 有权限情况下，使用全局悬浮窗
 */
open class WindowDialog(context: Activity, private val dialogConfig: DialogConfig)  : UniversalDialog,
    CommonViewOperator.OnDismissFromTouch, UniversalDialogSDK.OnFontChangeListener {

    /**
     * 使用applicationContext，不让Activity内存泄漏
     */
    private val application = context.applicationContext

    /**
     * 弹窗view生成展示隐藏控制类
     */
    private val windowViewOperator = WindowViewOperator(context, dialogConfig, this)

    /**
     * 是否显示当中
     */
    private var isShow = false

    /**
     * 通过windowManager添加view
     */
    private var windowManager : WindowManager?=null

    /**
     * 延时隐藏弹窗
     */
    private val mHandler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            dialogClose()
        }
    }

    /**
     * 弹窗展示处理，注册前后台切换，将windowManager交给[windowViewOperator]处理
     */
    override fun dialogShow(topAct: Activity) {
        if(isShow)return
        if(windowManager == null){
            try {
                windowManager = application?.getSystemService(WINDOW_SERVICE) as WindowManager
            }catch (e : Exception){
                if(Utils.isDebug(application))e.printStackTrace()
            }
        }
        windowManager?.let {
            windowViewOperator.onAttach(it, true)
            dialogConfig.dialogCallback?.onDialogShow()

            if(dialogConfig.dialogDataConfig.getShowTime() > 0){
                mHandler.sendEmptyMessageDelayed(1,
                    (dialogConfig.dialogDataConfig.getShowTime() * 1000).toLong()
                )
            }
        }
        UniversalDialogSDK.registerOnFountChange(this)
        isShow = true
    }

    /**
     * 弹窗消失处理，反注册前后台切换
     */
    override fun dialogClose() {
        if(!isShow)return
        isShow = false
        UniversalDialogSDK.unregisterOnFountChange(this)
        windowViewOperator.onDetach(true)
        dialogConfig.dialogCallback?.onDialogClose()
    }

    override fun isInShow(): Boolean {
        return isShow
    }

    override fun onDismiss() {
        dialogClose()
    }

    /**
     * 前后端切换回调，后台的时候无需展示
     */
    override fun onFountChange(font: Boolean) {
        if(!isShow)return
        if(font){
            windowViewOperator.addView()
        }else{
            windowViewOperator.removeView()
        }
    }
}