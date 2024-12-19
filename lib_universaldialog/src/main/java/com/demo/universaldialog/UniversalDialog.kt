package com.demo.universaldialog

import android.app.Activity
import com.demo.universaldialog.impl.DialogCreatorImpl
import com.demo.universaldialog.interfaces.ContentViewCreator
import com.demo.universaldialog.interfaces.DialogDataConfig
import com.demo.universaldialog.interfaces.UniversalDialogCallback
import com.demo.universaldialog.interfaces.UniversalDialogCreator
import com.demo.universaldialog.utils.Utils

/**
 * 通用弹窗父接口，控制弹窗显示或者消失
 */
interface UniversalDialog {

    /**
     * 显示弹窗，传入当前的Activity
     * [topAct] 当前顶部Activity
     */
    fun dialogShow(topAct: Activity)

    /**
     * 隐藏弹窗
     */
    fun dialogClose()
    /**
     * 弹窗是否在显示
     */
    fun isInShow() : Boolean

    /**
     * 通用弹窗构造器
     * [context]当前的Activity
     */
    class Builder(private val context: Activity) {

        private var dialogConfig : DialogConfig?=null
        private var dialogDataConfig: DialogDataConfig? = null
        private var dialogCallback: UniversalDialogCallback? = null
        private var contentViewCreator: ContentViewCreator? = null
        private var dialogCreator : UniversalDialogCreator = DialogCreatorImpl

        /**
         * [dialogDataConfig] 设置弹窗配置数据
         */
        fun setDialogDataConfig(dialogDataConfig: DialogDataConfig) = apply {
            this.dialogDataConfig = dialogDataConfig
            if(!dialogDataConfig.isModal() && !Utils.hasPermission(context)){
                UniversalDialogSDK.requestAlertWindowPermission?.requestPermission(context){}
            }
        }
        /**
         * [contentViewCreator] 设置内容生成器
         */
        fun setContentViewCreator(contentViewCreator: ContentViewCreator) = apply {
            this.contentViewCreator = contentViewCreator
        }
        /**
         * [dialogCallback] 设置弹窗显示隐藏的回调
         */
        fun setDialogCallBack(dialogCallback: UniversalDialogCallback) = apply {
            this.dialogCallback = dialogCallback
        }
        /**
         * [contentViewCreator] 设置弹窗生成器，一般不需要设置
         */
        fun setDialogCreator(dialogCreator: UniversalDialogCreator) = apply {
            this.dialogCreator = dialogCreator
        }

        /**
         * 创建[UniversalDialog]
         * @return 如果配置的数据不正确，则返回为空
         */
        fun build(): UniversalDialog? {
            dialogDataConfig ?: kotlin.run {
                if (Utils.isDebug(context)) {
                    throw java.lang.IllegalArgumentException("dialogDataConfig can not null")
                }else{
                    return null
                }
            }
            contentViewCreator ?: kotlin.run {
                if (Utils.isDebug(context)) {
                    throw java.lang.IllegalArgumentException("contentViewCreator can not null")
                }else{
                    return null
                }
            }
            dialogConfig = DialogConfig(dialogDataConfig!!, contentViewCreator!!)
            dialogConfig!!.dialogCallback = dialogCallback

            val dialog = if (dialogConfig!!.dialogDataConfig.isModal()){
                dialogCreator.createGeneralDialog(context ,dialogConfig!!)
            }else{
                dialogCreator.createFloatDialog(context, dialogConfig!!)
            }
            return dialog
        }
    }

}