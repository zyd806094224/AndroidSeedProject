package com.demo.universaldialog.interfaces

import android.content.Context
import android.view.View
import android.view.ViewGroup

/**
 * 弹窗的显示内容创建者
 */
interface ContentViewCreator {
    /**
     * 创建显示内容并返回
     * [context] 上下文
     * [group] 父View
     * @return 创建的View
     */
    fun createContentView(context: Context, group: ViewGroup): View
}