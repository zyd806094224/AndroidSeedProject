package com.demo.universaldialog

import com.demo.universaldialog.interfaces.ContentViewCreator
import com.demo.universaldialog.interfaces.DialogDataConfig
import com.demo.universaldialog.interfaces.UniversalDialogCallback

/**
 * 通用弹窗相关配置
 */
class DialogConfig(
    /**
     *  弹窗配置
     */
    var dialogDataConfig: DialogDataConfig,
    /**
     * 内容View生成器
     */
    var contentViewCreator: ContentViewCreator
) {
    /**
     * 弹窗相关节点回调器
     */
    var dialogCallback: UniversalDialogCallback? = null

}