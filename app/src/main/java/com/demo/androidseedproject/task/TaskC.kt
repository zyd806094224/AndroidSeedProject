package com.demo.androidseedproject.task

import android.util.Log
import com.demo.framework.task.schedulingtask.Task
import kotlinx.coroutines.delay


/**
 * 没有依赖任何任务、最先启动
 */
class TaskC : Task {
    override val name: String
        get() = "TaskC"

    override val isAsync: Boolean
        get() = true

    override suspend fun execute() {
        Log.e("SampleTask", "TaskC 开始执行")
        delay(2000) // 模拟耗时操作
        Log.e("SampleTask", "TaskC 执行完成")
    }
}