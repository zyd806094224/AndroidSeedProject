package com.demo.framework.ext

import android.util.Log
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.observers.DisposableObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

/**
 * @Description:
 * @Date: 2024/8/30 14:37
 * @author:  zhaoyudong
 * @version: 1.0
 */

/**
 * 倒计时
 */
fun countDownCoroutines(
    total: Int,
    scope: CoroutineScope,
    onTick: (Int) -> Unit,
    onStart: (() -> Unit)? = null,
    onFinish: (() -> Unit)? = null,
): Job {
    return flow {
        for (i in total downTo 0) {
            emit(i)
            delay(1000)
        }
    }
        .flowOn(Dispatchers.Main)
        .onStart { onStart?.invoke() }
        .onCompletion { onFinish?.invoke() }//like java finally
        .onEach { onTick.invoke(it) }
        .launchIn(scope)
}


@OptIn(ExperimentalCoroutinesApi::class)
fun <T : Any> Observable<T>.asFlow(): Flow<RequestState<T>> {
    return callbackFlow {
        val disposable: DisposableObserver<T> =
            object : DisposableObserver<T>() {
                override fun onStart() {
                    trySend(RequestState.RequestStart<T>())
                }

                override fun onNext(t: T) {
                    trySend(RequestState.RequestSuccess<T>(t))
                }

                override fun onError(e: Throwable) {
                    trySend(RequestState.RequestError<T>(-1, "网络错误，请重试", e))
                    // 可选择发送完主动关闭 ，也可以不加这个close等协程作用域结束后自动关闭  看具体场景
                    // close()
                }

                override fun onComplete() {
                    trySend(RequestState.RequestCompleted<T>())
                    // 可选择发送完主动关闭 ，也可以不加这个close等协程作用域结束后自动关闭  看具体场景
                    // close()
                }
            }.apply {
                this@asFlow.subscribe(this)
            }
        awaitClose { // 取消网络请求
            Log.e("zzz","awaitClose")
            if (!disposable.isDisposed) {
                disposable.dispose()
            }
        }
    }
}

