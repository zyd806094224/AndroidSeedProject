package com.demo.androidseedproject

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter
import com.demo.framework.helper.AppHelper

/**
 * @Description:
 * @Date: 2024/8/30 15:06
 * @author:  zhaoyudong
 * @version: 1.0
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppHelper.init(this, BuildConfig.DEBUG)
        ARouter.init(AppHelper.getApplication())
    }
}