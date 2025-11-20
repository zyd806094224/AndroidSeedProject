package com.demo.main.flutter.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.plugin.common.MethodChannel

class FlutterDemoActivity : FlutterActivity() {

    companion object {
        private const val ENGINE_ID = "main_flutter_engine"

        fun start(context: Context) {
            val intent = Intent(context, FlutterDemoActivity::class.java)
            // 可以修改这里的路由和参数进行测试
            intent.putExtra("initial_route", "/custom_flutter_page?id=123&name=测试商品")
            context.startActivity(intent)
        }

        // 带参数启动的方法
        fun startWithParams(context: Context, id: String, name: String) {
            val intent = Intent(context, FlutterDemoActivity::class.java)
            intent.putExtra("initial_route", "/custom_flutter_page?id=$id&name=$name")
            context.startActivity(intent)
        }
    }

    private val CHANNEL = "com.example/custom_flutter_activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 当使用缓存引擎时，我们需要手动设置路由
        val route = intent.getStringExtra("initial_route")
        if (!route.isNullOrEmpty()) {
            flutterEngine?.navigationChannel?.pushRoute(route)
        }
    }

    override fun provideFlutterEngine(context: Context): FlutterEngine? {
        // 如果有缓存的Flutter引擎，则使用它来提高启动速度
        return FlutterEngineCache.getInstance().get(ENGINE_ID) ?: super.provideFlutterEngine(context)
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        // 注册 MethodChannel，实现原生与 Flutter 通信
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
            .setMethodCallHandler { call, result ->
                when (call.method) {
                    // 处理 Flutter 调用的原生方法（如显示 Toast）
                    "showNativeToast" -> {
                        val message = call.argument<String>("message") ?: "默认消息"
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        result.success(true)
                    }
                    // 处理返回原生页面的逻辑
                    "finishActivity" -> {
                        finish() // 关闭当前 Activity（Flutter 页面）
                        result.success(true)
                    }
                    // 获取原生参数
                    "getInitialRouteParams" -> {
                        val route = intent.getStringExtra("initial_route") ?: ""
                        result.success(route)
                    }

                    else -> result.notImplemented()
                }
            }
    }
}