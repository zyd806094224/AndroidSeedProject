package com.demo.universaldialog.generaldialog

import android.app.Activity
import com.demo.universaldialog.DialogConfig
import com.demo.universaldialog.R
import com.demo.universaldialog.enums.ShowFrom
import com.demo.universaldialog.utils.StatusBarUtil
import com.demo.universaldialog.view.CommonViewOperator

open class GeneralViewOperator(context: Activity, dialogConfig: DialogConfig) :
    CommonViewOperator<GeneralDialog>(context, dialogConfig, null) {

    /**
     * 设置普通Dialog的视图
     */
    override fun onAttach(generalDialog: GeneralDialog, anim: Boolean) {
        if(isAttach)return
        if(mRootContentView == null)createContentView()
        if(mRootContentView == null)return
        val window = generalDialog.window
        window ?: return
        isAttach = true
        generalDialog.setContentView(mRootContentView!!)
        val lp = window.attributes
        lp.dimAmount = dialogConfig.dialogDataConfig.getOpacity()
        lp.gravity = getLocation()

        //设置通顶，在设置顶部边距
        StatusBarUtil.fullScreenTransparent(window, false)
        setCreateViewToMargin(window.decorView)

        val lpGroup = getLayoutParams()
        lp.width = lpGroup.width
        lp.height = lpGroup.height

        generalDialog.window!!.attributes = lp

        //使用xml配置的动画，不使用代码方式
        val style = when(dialogConfig.dialogDataConfig.getShowFrom()){
            ShowFrom.BOTTOM -> R.style.universal_anim_bottom
            ShowFrom.TOP -> R.style.universal_anim_top
            ShowFrom.LEFT -> R.style.universal_anim_left
            ShowFrom.RIGHT -> R.style.universal_anim_right
            else -> 0
        }
        if(style != 0)window.setWindowAnimations(style)

        generalDialog.setCancelable(false)
        generalDialog.setCanceledOnTouchOutside(false)
        mRootContentView?.fitsSystemWindows = false
    }

    /**
     * 该种弹窗无需detach
     */
    override fun onDetach(anim: Boolean) {
    }

    /**
     * 该种弹窗无需拖动
     */
    override fun moveXY(x: Float, y: Float) {
    }
    /**
     * 该种弹窗无需拖动
     */
    override fun canMove(): Boolean {
        return false
    }
}