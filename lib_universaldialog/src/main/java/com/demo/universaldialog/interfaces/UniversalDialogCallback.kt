package com.demo.universaldialog.interfaces

import android.view.View

/**
 * 通用弹窗相关节点回调，用于埋点等
 */
interface UniversalDialogCallback {
    /**
     * 展示弹窗回调
     */
    fun onDialogShow()

    /**
     * 关闭弹窗回调
     */
    fun onDialogClose()
    /**
     * 创建显示内容过后的回调
     * [view]创建的内容
     */
    fun onCreateContentView(view : View)
}