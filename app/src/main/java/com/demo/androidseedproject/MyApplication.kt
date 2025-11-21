package com.demo.androidseedproject

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
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
import com.demo.framework.manager.ActivityManager
import com.demo.framework.manager.AppFrontBack
import com.demo.framework.manager.AppFrontBackListener
import com.demo.framework.manager.AppManager
import com.demo.framework.utils.DeviceInfoUtils
import com.demo.html.html.HtmlTagCtrlFactory
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor

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
        AppManager.init(this)
        DeviceInfoUtils.init(this)
        //注册APP前后台切换监听
        appFrontBackRegister()
        // App启动立即注册监听
        registerActivityLifecycle()
        ARouter.init(AppHelper.getApplication())
        initRefreshLayoutTask()
        initHtmlText()
        Log.e("zzz", BuildConfig.MODEL)
        Log.e("zzz", "${BuildConfig.VA}")

        // 预加载并缓存Flutter引擎
        // 建议主线程初始化，商用场景可以考虑在合适的位置初始化，或者延时初始化
        preloadFlutterEngine()
    }

    private fun preloadFlutterEngine() {
        val engineId = "main_flutter_engine"
        if (!FlutterEngineCache.getInstance().contains(engineId)) {
            val flutterEngine = FlutterEngine(this)
            flutterEngine.dartExecutor.executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
            )
            FlutterEngineCache.getInstance().put(engineId, flutterEngine)
        }
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

    /**
     * 注册APP前后台切换监听
     */
    private fun appFrontBackRegister() {
        AppFrontBack.register(this, object : AppFrontBackListener {
            override fun onBack(activity: Activity?) {
                Log.d("", "onBack")
            }

            override fun onFront(activity: Activity?) {
                Log.d("", "onFront")
            }
        })
    }

    /**
     * 注册Activity生命周期监听
     */
    private fun registerActivityLifecycle() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                ActivityManager.pop(activity)
            }

            override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {
            }


            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivityCreated(activity: Activity, p1: Bundle?) {
                ActivityManager.push(activity)
            }

            override fun onActivityResumed(activity: Activity) {
            }
        })
    }

    private fun initHtmlText() {
        HtmlTagCtrlFactory.getInstance()
            .addHandlerCtrl(FontSizeTag.NAME, FontSizeHandler::class.java)
        HtmlTagCtrlFactory.getInstance()
            .addHandlerCtrl(MarginLeftTag.NAME, MarginLeftTagHandler::class.java)
        HtmlTagCtrlFactory.getInstance()
            .addHandlerCtrl(HYImageTag.NAME, HYImageTagHandler::class.java)
        HtmlTagCtrlFactory.getInstance().addHandlerCtrl(LabelTag.NAME, LabelTagHandler::class.java)
        HtmlTagCtrlFactory.getInstance()
            .addHandlerCtrl(HYSuffixTag.NAME, HYSuffixTagHandler::class.java)
        HtmlTagCtrlFactory.getInstance()
            .addHandlerCtrl(NoColorUrlTag.NAME, NoColorUrlHandler::class.java)
    }
}