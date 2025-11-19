package com.demo.main.flutter.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class FlutterDemoActivity : FlutterActivity() {

    companion object {
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
    }

    override fun getInitialRoute(): String? {
        return intent.getStringExtra("initial_route")
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