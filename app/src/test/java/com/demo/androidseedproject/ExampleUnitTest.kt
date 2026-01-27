package com.demo.androidseedproject

import android.database.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    data class A(var x: Int, var y: Int)

    private suspend fun deepDiveJob() {
        try {
            coroutineScope {
                val job1 = launch {
                    throw RuntimeException("失败1")
                    // try {
                    //     throw RuntimeException("失败1")
                    // } catch (e: Exception) {
                    //     println("job1 捕获: $e")
                    //     // 即使捕获，协程仍会被取消
                    // }
                }

                val job2 = launch {
                    delay(200)
                    println("job2 完成")
                }
            }
        } catch (e: Exception) {
            println("外层捕获: $e")
        }
    }

    private suspend fun supervisorScopeTest() {
        println("=== supervisorScope 测试 ===")

        try {
            supervisorScope {
                val job1 = launch {
                    try {
                        throw RuntimeException("失败1")
                    } catch (e: Exception) {
                        println("  job1 内部捕获: $e")
                    }
                }

                val job2 = launch {
                    //delay(100)
                    println("  job2 完成")
                }

                val job3 = launch {
                    throw RuntimeException("失败3")
                    // 不捕获异常
                }

                val job4 = launch {
                    delay(200)
                    println("  job4 完成")
                }
                throw RuntimeException("11111")
            }
            println("  supervisorScope 正常完成")
        } catch (e: Exception) {
            println("  外层捕获: $e")
        }
    }


    @Test
    fun testtt() {
        runBlocking {
            val sharedFlow = MutableSharedFlow<Int>(
                replay = 2,          // 新订阅者接收最近2个值
                extraBufferCapacity = 10 // 额外缓冲区
            )

// 生产者
            launch {
                (1..15).forEach {
                    sharedFlow.emit(it)
                    delay(50)
                }
            }

// 消费者1
            launch {
                sharedFlow.collect {
                    println("C1: $it")
                }
            }

// 延迟消费者
            launch {
                delay(300)
                sharedFlow.collect {
                    println("C2: $it") // 收到13,14（replay=2）
                }
            }
        }
    }

    @Test
    fun testAsync() = runBlocking {
        println("开始-------")
        // 使用当前协程作用域，而不是创建新的
        val deferred1 = async { fetchData1() }
        val deferred2 = async { fetchData2() }

        println("async执行完-------")

        // val res1 = deferred1.await()
        // val res2 = deferred2.await()
        //println("结果: $res1, $res2")
        println("结束-------")
    }

    private suspend fun fetchData2() : Int{
        delay(5000)
        println("fetchData2执行了")
        return 22
    }

    private suspend fun fetchData1() : Int{
        delay(2000)
        println("fetchData1执行了")
        return 11
    }

    @Test
    fun addition_isCorrect() {
        testtt()
        println("11111111111111111111111")

        runBlocking {
            // deepDiveJob()
            supervisorScopeTest()
        }

        val a = A(1, 1)
        a.apply {

        }
        println(
            a.run {
                3
            }
        )
        with(a) {
            3
        }
        a.let {
            3
        }
        a.also {

        }

        val arr: IntArray = intArrayOf(1, 3, 6, 8, 23, 56, 99, 30, 22, 7, 2)
        println(findPeek(arr))


    }

    private fun findPeek(arr: IntArray): Int {
        var left = 0
        var right = arr.size - 1
        while (left <= right) {
            val mid = left + (right - left) / 2
            if (mid == 0 && arr[mid] > arr[mid + 1]) {
                return mid;
            }
            if (mid == arr.size - 1 && arr[mid] > arr[mid - 1]) {
                return mid;
            }
            if (arr[mid] < arr[mid - 1]) {
                right = mid - 1;
            } else if (arr[mid] < arr[mid + 1]) {
                left = mid + 1;
            } else {
                return mid;
            }
        }
        return -1
    }


}