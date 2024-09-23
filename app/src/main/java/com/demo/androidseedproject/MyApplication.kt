package com.demo.androidseedproject

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter
import com.demo.common.text.html.ctrl.FontSizeHandler
import com.demo.common.text.html.ctrl.HYImageTagHandler
import com.demo.common.text.html.ctrl.HYSuffixTagHandler
import com.demo.common.text.html.ctrl.LabelTagHandler
import com.demo.common.text.html.ctrl.MarginLeftTagHandler
import com.demo.common.text.html.ctrl.NoColorUrlHandler
import com.demo.common.text.html.tag.FontSizeTag
import com.demo.common.text.html.tag.HYImageTag
import com.demo.common.text.html.tag.HYSuffixTag
import com.demo.common.text.html.tag.LabelTag
import com.demo.common.text.html.tag.MarginLeftTag
import com.demo.common.text.html.tag.NoColorUrlTag
import com.demo.framework.helper.AppHelper
import com.demo.html.html.HtmlTagCtrlFactory
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
        initHtmlText()
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

    private fun initHtmlText() {
        HtmlTagCtrlFactory.getInstance().addHandlerCtrl(FontSizeTag.NAME,FontSizeHandler::class.java)
        HtmlTagCtrlFactory.getInstance().addHandlerCtrl(MarginLeftTag.NAME, MarginLeftTagHandler::class.java)
        HtmlTagCtrlFactory.getInstance().addHandlerCtrl(HYImageTag.NAME, HYImageTagHandler::class.java)
        HtmlTagCtrlFactory.getInstance().addHandlerCtrl(LabelTag.NAME, LabelTagHandler::class.java)
        HtmlTagCtrlFactory.getInstance().addHandlerCtrl(HYSuffixTag.NAME, HYSuffixTagHandler::class.java)
        HtmlTagCtrlFactory.getInstance().addHandlerCtrl(NoColorUrlTag.NAME, NoColorUrlHandler::class.java)
    }
}