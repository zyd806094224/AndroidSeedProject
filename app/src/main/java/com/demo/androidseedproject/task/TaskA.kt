package com.demo.androidseedproject.task

import android.util.Log
import com.demo.framework.task.schedulingtask.Task


/**
 * 示例任务A  依赖任务C
 */
class TaskA : Task {

    override val name: String
        get() = "TaskA"

    override val isAsync: Boolean
        get() = true

    override val dependencies: List<Class<out Task>>
        get() = listOf(TaskC::class.java)

    override suspend fun execute() {
        Log.e("SampleTask", "TaskA 开始执行")
        fibonacci(40)
        Log.e("SampleTask", "TaskA 执行完成")
    }


    fun fibonacci(n: Int): Int {
        if (n == 0 || n == 1) {
            return 1
        }
        return fibonacci(n - 1) + fibonacci(n - 2)
    }
}