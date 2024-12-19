package com.demo.universaldialog.floatdialog

import android.app.Activity
import android.view.View
import android.widget.FrameLayout
import com.demo.universaldialog.DialogConfig
import com.demo.universaldialog.view.CommonViewOperator

/**
 * 悬浮弹窗的展示，根据[onAttach]时候的Activity进行悬浮展示
 */
open class FloatViewOperator(
    act: Activity,
    dialogConfig: DialogConfig,
    onDismissFromTouch: OnDismissFromTouch
) : CommonViewOperator<Activity>(act, dialogConfig, onDismissFromTouch) {

    /**
     * 设置RootView的对齐方式
     */
    private fun setLocation() {
        (mRootContentView?.layoutParams as? FrameLayout.LayoutParams)?.gravity = getLocation()
    }

    /**
     * 从Activity上面移除View
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
     * 在Activity展示View，需要设置layout，以及相关Margin、动画等
     */
    override fun onAttach(activity: Activity, anim: Boolean) {
        if (isAttach) return
        if (mRootContentView == null) createContentView()
        val contentView = activity.window?.decorView?.findViewById<FrameLayout>(android.R.id.content)
        if (mRootContentView == null || contentView == null) {
            return
        }
        isAttach = true
        setCreateViewToMargin(activity.window?.decorView)
        removeView()
        contentView.addView(mRootContentView, getLayoutParams())
        setLocation()
        if (anim){
            executeAnim(true)
        }else{
            handler.postDelayed({
                mRootContentView?.requestLayout()
            }, 10)
        }

    }

    /**
     * 展示的Activity切换，需要从上一个Activity移除，在当前Activity上面显示
     */
    fun onResume(activity: Activity) {
        if (!isAttach) return
        //activity不同才更换rootView的附着Activity
        if(activity != (mRootContentView?.parent as? View)?.context) {
            setCreateViewToMargin(activity.window?.decorView)
            removeView()
            val contentView =
                activity.window?.decorView?.findViewById<FrameLayout>(android.R.id.content)
            contentView?.addView(mRootContentView)

            mRootContentView?.bringToFront()
        }
    }

    /**
     * Activity销毁，如果是当前的，则需要移除
     */
    fun onDestroy(activity: Activity){
        if (!isAttach) return
        if(activity == (mRootContentView?.parent as? View)?.context) {
            removeView()
        }
    }

    /**
     * 悬浮层拖动修改对应位置
     */
    override fun moveXY(x: Float, y: Float) {
        mRootContentView!!.translationY = y
        mRootContentView!!.translationX = x
    }

    override fun canMove(): Boolean {
        return dialogConfig.dialogDataConfig.canSlideClose()
    }

}