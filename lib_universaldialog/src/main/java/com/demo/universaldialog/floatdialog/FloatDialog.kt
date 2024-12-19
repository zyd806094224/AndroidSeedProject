package com.demo.universaldialog.floatdialog

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.demo.universaldialog.DialogConfig
import com.demo.universaldialog.UniversalDialog
import com.demo.universaldialog.UniversalDialogSDK
import com.demo.universaldialog.view.CommonViewOperator

/**
 * 悬浮弹窗的实现，多Activity都会显示，方案就是在每个Activity父View添加显示的View；
 * [FloatDialog]需要管理这个view的显示，隐藏，动画的统一管理
 */
open class FloatDialog(context: Activity,private val dialogConfig: DialogConfig)  : UniversalDialog,
    CommonViewOperator.OnDismissFromTouch {
    /**
     * 在显示过后注册Activity生命周期监听，每个Activity展示的时候把悬浮弹窗显示在Activity上面
     */
    private var lifeCycleCallBack : LifeCycleCallBack?=null

    /**
     * 使用application引用，避免造成Activity不能回收
     */
    private val application = context.applicationContext as? Application

    /**
     * 是否显示当中
     */
    private var isShow = false

    /**
     * 延时隐藏弹窗
     */
    private val mHandler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            dialogClose()
        }
    }
    /**
     * View生成与展示类
     */
    private var floatViewOperator : FloatViewOperator?=null

    /**
     * 显示浮层弹窗，延时消失、注册Activity监听等
     */
    override fun dialogShow(activity: Activity) {
        if(isShow)return
        isShow = true
        if(dialogConfig.dialogDataConfig.isGlobal()) {
            lifeCycleCallBack ?: kotlin.run {
                lifeCycleCallBack = LifeCycleCallBack()
                application?.registerActivityLifecycleCallbacks(lifeCycleCallBack)
            }
        }
        createNewOperator(activity, true)
        dialogConfig.dialogCallback?.onDialogShow()
        if(dialogConfig.dialogDataConfig.getShowTime() > 0){
            mHandler.sendEmptyMessageDelayed(1,
                (dialogConfig.dialogDataConfig.getShowTime() * 1000).toLong()
            )
        }
    }

    /**
     * 隐藏浮层弹窗，反注册延时消失、Activity监听等
     */
    override fun dialogClose() {
        if(!isShow)return
        isShow = false
        mHandler.removeMessages(1)
        application?.unregisterActivityLifecycleCallbacks(lifeCycleCallBack)
        floatViewOperator?.onDetach(true)
        dialogConfig.dialogCallback?.onDialogClose()
    }

    /**
     * 创建并且在Activity真正显示
     */
    private fun createNewOperator(activity: Activity, anim:Boolean){
        if(!isShow)return
        if(floatViewOperator == null){
            floatViewOperator = FloatViewOperator(activity, dialogConfig, this)
        }
        floatViewOperator?.onAttach(activity, anim)
    }

    /**
     * Activity重新展示
     */
    private fun onResume(activity: Activity){
        if(!isShow) return
        floatViewOperator?.onResume(activity)
    }
    /**
     * Activity销毁
     */
    private fun onDestroy(activity: Activity){
        if(!isShow) return
        floatViewOperator?.onDestroy(activity)
    }

    inner class LifeCycleCallBack : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            if (UniversalDialogSDK.getFlotDialogActivity().isNotEmpty()) {
                if (UniversalDialogSDK.getFlotDialogActivity()
                        .contains(activity::class.java.name)
                ) {
                    mHandler.post { createNewOperator(activity, false) }
                }
            } else {
                if (!UniversalDialogSDK.getFlotDialogExcludeActivity()
                        .contains(activity::class.java.name)
                ) {
                    mHandler.post { createNewOperator(activity, false) }
                }
            }
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityResumed(activity: Activity) {
            if (UniversalDialogSDK.getFlotDialogActivity().isNotEmpty()) {
                if (UniversalDialogSDK.getFlotDialogActivity()
                        .contains(activity::class.java.name)
                ) {
                    mHandler.post { onResume(activity) }
                }
            } else {
                if (!UniversalDialogSDK.getFlotDialogExcludeActivity()
                        .contains(activity::class.java.name)
                ) {
                    mHandler.post { onResume(activity) }
                }
            }
        }

        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityDestroyed(activity: Activity) {
            if (UniversalDialogSDK.getFlotDialogActivity().isNotEmpty()) {
                if (UniversalDialogSDK.getFlotDialogActivity()
                        .contains(activity::class.java.name)
                ) {
                    mHandler.post {
                        onDestroy(activity)
                    }
                }
            } else {
                if (!UniversalDialogSDK.getFlotDialogExcludeActivity()
                        .contains(activity::class.java.name)
                ) {
                    mHandler.post {
                        onDestroy(activity)
                    }
                }
            }
        }
    }

    override fun isInShow(): Boolean {
        return isShow
    }

    /**
     * 滑动消失回调
     */
    override fun onDismiss() {
        dialogClose()
    }
}