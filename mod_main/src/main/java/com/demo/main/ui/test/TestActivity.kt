package com.demo.main.ui.test

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.demo.common.audio.AudioConstants
import com.demo.common.audio.exception.AudioException
import com.demo.common.audio.exception.AudioRecordCreateFileException
import com.demo.common.audio.exception.AudioRecordNoStorageSpaceException
import com.demo.common.audio.recorder.RecorderContract.RecorderCallback
import com.demo.common.audio.recorder.impl.AudioRecorder
import com.demo.common.audio.recorder.impl.FileRepositoryImpl
import com.demo.common.utils.RouteUtils
import com.demo.framework.base.BaseMvvmActivity
import com.demo.framework.helper.AppHelper
import com.demo.main.databinding.ActivityTestBinding
import com.demo.main.ui.test.viewmodel.TestViewModel
import com.demo.universaldialog.UniversalDialog
import com.demo.universaldialog.enums.ShowFrom
import com.demo.universaldialog.enums.XLocation
import com.demo.universaldialog.enums.YLocation
import com.demo.universaldialog.interfaces.ContentViewCreator
import com.demo.universaldialog.interfaces.DialogDataConfig
import com.demo.universaldialog.interfaces.UniversalDialogCallback
import com.demo.common.audio.AudioOutputFormat
import com.demo.framework.ext.RequestState
import com.demo.framework.ext.asFlow
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.permission.PermissionLists
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.io.File

/**
 * @Description: 测试页面Activity
 * @Date: 2025/12/02
 * @author: zhaoyudong
 * @version: 1.0
 */
@Route(path = "/test/activity")
class TestActivity : BaseMvvmActivity<ActivityTestBinding, TestViewModel>() {

    companion object {
        private const val TAG = "TestActivity"

        /**
         * 启动TestActivity
         * @param context 上下文
         */
        fun start(context: Context) {
            RouteUtils.navigate(context, "/test/activity")
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        initClickListeners()
        observeViewModel()
    }

    /**
     * 初始化点击事件
     */
    private fun initClickListeners() {
        // 权限测试
        mBinding.btnPermission.setOnClickListener {
            testPermission()
        }

        // WebView跳转测试
        mBinding.btnWebView.setOnClickListener {
            // RouteUtils.toWebView(
            //     this,
            //     "https://www.baidu.com",
            //     "百度搜索",
            //     true
            // )
            RouteUtils.toWebView(
                this,
                "http://192.168.213.9:3000",
                "百度搜索",
                true
            )
        }

        // 编辑页面跳转测试
        mBinding.btnEdit.setOnClickListener {
            RouteUtils.toEdit(
                this,
                "这是默认的编辑内容",
                "请输入您的文字..."
            )
        }

        // 主页跳转测试
        mBinding.btnMain.setOnClickListener {
            RouteUtils.toMain(this, 1) // 跳转到我的Tab
        }

        // 外部URL测试
        mBinding.btnExternalUrl.setOnClickListener {
            val externalUrl = "seedapp://web/activity?url=https://www.baidu.com&title=百度搜索&showShare=true"
            RouteUtils.handleExternalUrl(this, externalUrl)
        }

        // 测试所有路由
        mBinding.btnTestAll.setOnClickListener {
            RouteUtils.logRouteConfig()
            RouteUtils.testAllRoutes(this)
        }

        // Flow状态测试
        mBinding.btnFlowTest.setOnClickListener {
            testFlow()
        }

        mBinding.btnRxjavaFlowTest.setOnClickListener {
            // 测试RxJava转Flow
            testRxJavaToFlow()
        }

        // 录音测试
        mBinding.btnAudio.setOnClickListener {
            testAudio()
        }

        // 弹窗测试
        mBinding.btnDialog.setOnClickListener {
            showTestDialog()
        }
    }

    /**
     * 观察ViewModel数据变化
     */
    private fun observeViewModel() {
        /*// 方式1：直接使用 lifecycleScope
        lifecycleScope.launch {
            viewModel.data.collect { data ->
                binding.textView.text = data
            }
        }

        // 方式2：使用 repeatOnLifecycle（推荐）
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(STARTED) {
                viewModel.data.collect { data ->
                    binding.textView.text = data
                }
            }
        }

        // 方式3：使用 launchWhenStarted（已废弃，不推荐）
        lifecycleScope.launchWhenStarted {
            // 已废弃，使用 repeatOnLifecycle 代替
        }*/

        // 观察状态流
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.state.onEach { state ->
                    Log.e(TAG, "State变化: $state")
                }.collect()
            }
        }

        // 观察共享流
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.shared.onEach { shared ->
                    Log.e(TAG, "SharedFlow数据: $shared")
                }.collect()
            }
        }

        // 观察权限测试结果
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.permissionResult.onEach { result ->
                    Log.e(TAG, "权限测试结果观察: $result")
                }.collect()
            }
        }
    }

    /**
     * 权限测试
     */
    private fun testPermission() {
        XXPermissions.with(this)
            // 申请多个权限
            .permission(PermissionLists.getRecordAudioPermission())
            .permission(PermissionLists.getCameraPermission())
            .request { _, deniedList ->
                val allGranted = deniedList.isEmpty()
                mViewModel.updatePermissionResult(allGranted)

                if (!allGranted) {
                    // 判断请求失败的权限是否被用户勾选了不再询问的选项
                    val doNotAskAgain = XXPermissions.isDoNotAskAgainPermissions(this@TestActivity, deniedList)
                    Log.e(TAG, "权限被拒绝，是否不再询问: $doNotAskAgain")

                    if (doNotAskAgain) {
                        // 用户勾选了不再询问，可以跳转到设置页面
                        XXPermissions.startPermissionActivity(this@TestActivity, deniedList)
                    }
                } else {
                    Log.e(TAG, "所有权限已授予")
                }
            }
    }

    /**
     * Flow状态测试
     */
    private fun testFlow() {
        // 测试StateFlow
        mViewModel.changeState()

        // 测试SharedFlow
        mViewModel.changeShared()
    }

    /**
     * 测试RxJava转换为Flow
     */
    private fun testRxJavaToFlow() {
        lifecycleScope.launch {
            // 调用testCallBackFlow方法获取Flow
            testCallBackFlow().collect { state ->
                when (state) {
                    is RequestState.RequestStart<String> -> {
                        Log.e(TAG, "RxJava转Flow: 请求开始")
                    }
                    is RequestState.RequestSuccess<String> -> {
                        Log.e(TAG, "RxJava转Flow: 请求成功 - ${state.result}")
                    }
                    is RequestState.RequestError<String> -> {
                        Log.e(TAG, "RxJava转Flow: 请求错误 - ${state.message}")
                    }
                    is RequestState.RequestCompleted<String> -> {
                        Log.e(TAG, "RxJava转Flow: 请求完成")
                    }
                }
            }
            Log.e("zzz","TestActivity执行了")
        }
    }

    private fun testCallBackFlow(): Flow<RequestState<String>> {
        // 创建一个RxJava的Observable，模拟网络请求
        val observable = Observable.create<String> { emitter ->
            // 模拟网络请求延迟
            Thread.sleep(1000)

            // 发送成功数据
            emitter.onNext("RxJava转换为Flow成功！")

            // 完成请求
            emitter.onComplete()
        }.subscribeOn(Schedulers.io())

        // 使用asFlow扩展函数将RxJava的Observable转换为Kotlin的Flow
        return observable.asFlow()
    }

    /**
     * 录音测试
     */
    private fun testAudio() {
        AudioRecorder.getInstance().recorderCallback = object : RecorderCallback {
            override fun onStartRecord(output: File?) {
                Log.e(TAG, "录音开始: ${output?.absolutePath}")
            }

            override fun onPauseRecord() {
                Log.e(TAG, "录音暂停")
            }

            override fun onResumeRecord() {
                Log.e(TAG, "录音恢复")
            }

            override fun onRecordProgress(mills: Long, amp: Int) {
                Log.e(TAG, "录音进度: ${mills}ms, 振幅: $amp")
            }

            override fun onStopRecord(output: File?, duration: Long) {
                Log.e(TAG, "录音结束: ${output?.absolutePath}, 时长: ${duration}ms")
            }

            override fun onError(throwable: AudioException?) {
                Log.e(TAG, "录音错误: ${throwable?.message}")
            }
        }

        val fileRepository = FileRepositoryImpl.getInstance(AppHelper.getApplication())
        if (!fileRepository.hasAvailableSpace(AppHelper.getApplication())) {
            // 存储空间不足
            AudioRecorder.getInstance().recorderCallback.onError(
                AudioRecordNoStorageSpaceException()
            )
            return
        }

        // 创建录音文件
        val file = fileRepository.provideRecordFile(AudioOutputFormat.AudioOutputFormatM4a)
        if (file == null) {
            AudioRecorder.getInstance().recorderCallback.onError(
                AudioRecordCreateFileException()
            )
            return
        }

        // 开始录音
        val path = file.absolutePath
        AudioRecorder.getInstance().startRecording(
            path,
            AudioOutputFormat.AudioOutputFormatM4a,
            AudioConstants.CHANNEL_COUNT,
            AudioConstants.SAMPLE_RATE,
            AudioConstants.BITRATE,
            60 // 最大录音时间60秒
        )
    }

    /**
     * 显示测试弹窗
     */
    private fun showTestDialog() {
        var dialog: UniversalDialog? = null
        dialog = this.let {
            UniversalDialog.Builder(it)
                .setDialogDataConfig(object : DialogDataConfig {
                    override fun getShowX(): XLocation {
                        return XLocation.CENTER
                    }

                    override fun getShowY(): YLocation {
                        return YLocation.TOP
                    }

                    override fun getShowFrom(): ShowFrom {
                        return ShowFrom.TOP
                    }

                    override fun getShowTime(): Int {
                        return 10
                    }

                    override fun getOpacity(): Float {
                        return 0.5f
                    }

                    override fun isModal(): Boolean {
                        return false
                    }

                    override fun getMarginTop(): Int {
                        return 0
                    }

                    override fun getMarginBottom(): Int {
                        return 0
                    }

                    override fun getMarginLeft(): Int {
                        return 0
                    }

                    override fun getMarginRight(): Int {
                        return 0
                    }

                    override fun getContentWidth(): String {
                        return "100%"
                    }

                    override fun getContentHeight(): String {
                        return "100"
                    }

                    override fun isCardBackground(): Boolean {
                        return true
                    }

                    override fun isGlobal(): Boolean {
                        return true
                    }

                    override fun canSlideClose(): Boolean {
                        return false
                    }

                    override fun isUseSystemFloatingWindow(): Boolean {
                        return false
                    }

                }).setContentViewCreator(object : ContentViewCreator {
                    override fun createContentView(context: Context, group: ViewGroup): View {
                        val appContext = context.applicationContext
                        val textView = TextView(appContext)
                        textView.text = "wos弹窗"
                        textView.setTextColor(Color.BLACK)
                        val par = LinearLayout(appContext)
                        par.addView(textView, -2, -2)
                        par.setBackgroundColor(Color.parseColor("#ff552e"))
                        par.setOnClickListener {
                            dialog?.dialogClose()
                        }
                        return par
                    }

                }).setDialogCallBack(object : UniversalDialogCallback {
                    override fun onCreateContentView(view: View) {

                    }

                    override fun onDialogClose() {

                    }

                    override fun onDialogShow() {

                    }

                }).build()
        }
        this.let { dialog?.dialogShow(it) }
    }
}