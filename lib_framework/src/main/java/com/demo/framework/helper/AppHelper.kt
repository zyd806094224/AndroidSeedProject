package com.demo.framework.helper

import android.app.Application

/**
 * @Description:
 * @Date: 2024/8/29 17:48
 * @author:  zhaoyudong
 * @version: 1.0
 */
object AppHelper {

    private lateinit var app: Application
    private var isDebug = false

    fun init(application: Application, isDebug: Boolean) {
        this.app = application
        this.isDebug = isDebug
    }

    /**
     * 获取全局应用
     */
    fun getApplication() = app

    /**
     * 是否为debug环境
     */
    fun isDebug() = isDebug
}