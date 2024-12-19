package com.demo.universaldialog.windowdialog

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.demo.universaldialog.DialogConfig
import com.demo.universaldialog.utils.StatusBarUtil
import com.demo.universaldialog.view.CommonViewOperator

/**
 * 有权限情况下，处理弹窗的View展示、隐藏、动画、拖动等
 */
open class WindowViewOperator(
    act: Context,
    dialogConfig: DialogConfig,
    onDismissFromTouch: OnDismissFromTouch
) : CommonViewOperator<WindowManager>(act, dialogConfig, onDismissFromTouch) {
    /**
     * 保存LayoutParams，下次展示不用创建
     */
    private var windowLayout : WindowManager.LayoutParams?=null
    /**
     * 保存WindowManager
     */
    private var windowManager: WindowManager?=null

    /**
     * 浮层初始位置记录，后续拖动、和动画改变[windowLayout]的X,Y会影响位置，需要在该基础上面修改
     */
    private var location:IntArray?=null

    /**
     * 显示浮层内容，并且附着到windowManager上面
     */
    override fun onAttach(windowManager: WindowManager, anim: Boolean) {
        if (isAttach) return
        if (mRootContentView == null) createContentView()
        if (mRootContentView == null) {
            return
        }
        isAttach = true
        removeView()
        windowLayout = getWindowLayoutParams()
        windowManager.addView(mRootContentView, windowLayout)
        this.windowManager = windowManager
        if (anim){
            mRootContentView?.visibility = View.INVISIBLE
            executeAnim(true)
        }else{
            handler.postDelayed({
                mRootContentView?.requestLayout()
            }, 10)
        }

    }

    /**
     * 获取浮层的LayoutParams
     */
    private fun getWindowLayoutParams(): WindowManager.LayoutParams? {
        //根据配置获取长宽及对齐方式
        val groupLo = getLayoutParams()
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.width = groupLo.width
        layoutParams.height = groupLo.height
        layoutParams.gravity = getLocation()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        //配置透明及能点击其他地方，以及能移出屏幕
        layoutParams.flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        layoutParams.flags
        layoutParams.format = PixelFormat.TRANSLUCENT
        return layoutParams
    }

    /**
     * 浮层消失时候相关处理
     */
    override fun onDetach(anim: Boolean) {
        if (!isAttach) return
        isAttach = false
        if (anim) {
            executeAnim(false)
        } else {
            removeView()
        }
    }

    /**
     * 前后台切换使用，或者消失时候从[windowManager]移除
     */
    override fun removeView() {
        windowManager?.removeView(mRootContentView)
    }

    /**
     * 前后台切换使用
     */
    fun addView(){
        if(mRootContentView != null && mRootContentView!!.parent == null){
            windowManager?.addView(mRootContentView, windowLayout)
        }
    }

    /**
     * 拖动实现，需要修改[windowLayout]的X,Y
     */
    override fun moveXY(x: Float, y: Float) {
        if(mRootContentView == null)return
        if(mRootContentView!!.width == 0){
            mRootContentView?.visibility = View.INVISIBLE
            return
        }
        mRootContentView?.visibility = View.VISIBLE
        if(location == null){
            //获取初始位置
            location = intArrayOf(0, 0)
            mRootContentView?.getLocationOnScreen(location)
            location!![1] -= StatusBarUtil.getStatusBarHeight(context)
            /**
             * 获取[location]过后，设置X,Y，对齐方式会影响真实位置，需要重置成左上对齐
             */
            windowLayout?.gravity = Gravity.TOP or Gravity.LEFT
        }
        windowLayout?.x = location!![0] + x.toInt()
        windowLayout?.y = location!![1] + y.toInt()
        windowManager?.updateViewLayout(mRootContentView, windowLayout)
    }

    override fun canMove(): Boolean {
        return dialogConfig.dialogDataConfig.canSlideClose()
    }

}