package com.demo.androidseedproject.task

import android.util.Log
import com.demo.framework.task.schedulingtask.Task

/**
 * 示例任务B
 * 依赖TaskA，只有当TaskA执行完成后才会执行
 */
class TaskB : Task {
    override val name: String
        get() = "TaskB"

    override val isAsync: Boolean
        get() = true

    override val dependencies: List<Class<out Task>>
        get() = listOf(TaskA::class.java)

    override suspend fun execute() {
        Log.e("SampleTask", "TaskB 开始执行")
        Thread.sleep(300)
        Log.e("SampleTask", "TaskB 执行完成")
    }
}