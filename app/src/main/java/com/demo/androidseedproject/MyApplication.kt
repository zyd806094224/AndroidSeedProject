package com.demo.androidseedproject

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter
import com.demo.framework.helper.AppHelper
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout

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
        initRefreshLayoutTask()
    }

    private fun initRefreshLayoutTask() {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(android.R.color.white)
            //            CustomRefreshHeader(context)
            ClassicsHeader(context)
        }
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            //指定为经典Footer，默认是 BallPulseFooter
            ClassicsFooter(context)
        }
    }
}