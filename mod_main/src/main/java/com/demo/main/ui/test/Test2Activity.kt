package com.demo.main.ui.test

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Route
import com.demo.framework.ext.activityScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Route(path = "/test2/activity")
class Test2Activity : Activity() {

    companion object {
        private const val TAG = "Test2Activity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: 页面创建")

        // 测试 activityScope 是否可用
        activityScope.launch {
            Log.d(TAG, "协程开始执行: Thread=${Thread.currentThread().name}")

            try {
                // 模拟耗时任务
                for (i in 1..10) {
                    Log.d(TAG, "协程执行中: 进度=$i/10, Thread=${Thread.currentThread().name}")
                    delay(1000) // 每次延迟1秒
                }

                Log.d(TAG, "协程正常完成: 任务执行完毕")
            } catch (e: CancellationException) {
                Log.d(TAG, "协程被取消: ${e.message}, Thread=${Thread.currentThread().name}")
                throw e // 重新抛出CancellationException
            } catch (e: Exception) {
                Log.e(TAG, "协程执行异常: ${e.message}", e)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: 页面销毁，activityScope绑定的协程应该被自动取消")
    }
}
