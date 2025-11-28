package com.demo.main.ui.mine

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.viewModelScope
import com.demo.common.audio.AudioConstants
import com.demo.common.audio.exception.AudioException
import com.demo.common.audio.exception.AudioRecordCreateFileException
import com.demo.common.audio.exception.AudioRecordNoStorageSpaceException
import com.demo.common.audio.recorder.RecorderContract.RecorderCallback
import com.demo.common.audio.recorder.impl.AudioRecorder
import com.demo.common.audio.recorder.impl.FileRepositoryImpl
import com.demo.common.utils.RouteUtils
import com.demo.common.utils.UIKitUtil
import com.demo.framework.base.BaseMvvmFragment
import com.demo.framework.helper.AppHelper
import com.demo.main.databinding.FragmentMineBinding
import com.demo.main.ui.EditTextActivity
import com.demo.main.ui.mine.viewmodel.MineViewModel
import com.demo.universaldialog.UniversalDialog
import com.demo.universaldialog.enums.ShowFrom
import com.demo.universaldialog.enums.XLocation
import com.demo.universaldialog.enums.YLocation
import com.demo.universaldialog.interfaces.ContentViewCreator
import com.demo.universaldialog.interfaces.DialogDataConfig
import com.demo.universaldialog.interfaces.UniversalDialogCallback
import com.demo.common.audio.AudioOutputFormat
import com.demo.main.ui.WebViewActivity
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.permission.PermissionLists
import com.hjq.permissions.permission.base.IPermission
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import java.io.File

/**
 * @Description:
 * @Date: 2024/9/3 17:29
 * @author:  zhaoyudong
 * @version: 1.0
 */
class MineFragment : BaseMvvmFragment<FragmentMineBinding, MineViewModel>() {

    override fun initView(view: View, savedInstanceState: Bundle?) {
        val icon = "https://wos.58cdn.com.cn/cDazYxWcDHJ/picasso/dohan5dj__w328_h80.png"
        val text =
            "[1004040-安心投] <font color=#ff552e>收拾卫生</font>|<font color=#ff552e>打扫卫生</font>|兵哥佳正军人家政新房开荒高端家政商业保洁商铺保洁兼职司机搬家陪诊"
        UIKitUtil.setPostTitle(text, mBinding?.tv, icon, icon)
        UIKitUtil.setTextWidthSuffixIcon("q121212", mBinding?.tv2, icon, 2f)
//        showDialog()
        //toAudio()

        mBinding?.btn?.setOnClickListener {
            // test()
            // EditTextActivity.start(requireContext())
            // testInLine()
            // WebViewActivity.start(requireContext())
            testPermission()
        }

        // WebView跳转测试
        mBinding?.btnWebView?.setOnClickListener {
            RouteUtils.toWebView(
                requireContext(),
                "https://www.baidu.com",
                "百度搜索",
                true
            )
        }

        // 编辑页面跳转测试
        mBinding?.btnEdit?.setOnClickListener {
            RouteUtils.toEdit(
                requireContext(),
                "这是默认的编辑内容",
                "请输入您的文字..."
            )
        }

        // 主页跳转测试
        mBinding?.btnMain?.setOnClickListener {
            RouteUtils.toMain(requireContext(), 1) // 跳转到我的Tab
        }

        // 外部URL测试
        mBinding?.btnExternalUrl?.setOnClickListener {
            val externalUrl = "seedapp://web/activity?url=https://www.baidu.com&title=百度搜索&showShare=true"
            RouteUtils.handleExternalUrl(requireContext(), externalUrl)
        }

        // 测试所有路由
        mBinding?.btnTestAll?.setOnClickListener {
            RouteUtils.logRouteConfig()
            RouteUtils.testAllRoutes(requireContext())
        }

        initTest()
    }

    /**
     * 权限测试
     */
    private fun testPermission(){

        XXPermissions.with(this)
            // 申请多个权限
            .permission(PermissionLists.getRecordAudioPermission())
            .permission(PermissionLists.getCameraPermission())
            // 设置不触发错误检测机制（局部设置）
            //.unchecked()
            .request(object : OnPermissionCallback {

                override fun onResult(grantedList: MutableList<IPermission>, deniedList: MutableList<IPermission>) {
                    val allGranted = deniedList.isEmpty()
                    if (!allGranted) {
                        // 判断请求失败的权限是否被用户勾选了不再询问的选项
                        val doNotAskAgain = XXPermissions.isDoNotAskAgainPermissions(activity!!, deniedList)
                        Log.e("zzz","doNotAskAgain----$doNotAskAgain")
                        // 在这里处理权限请求失败的逻辑
                        // ......
                        return
                    }
                    // 在这里处理权限请求成功的逻辑
                    // ......
                }
            })
    }


    private fun testInLine(){
        val nums = arrayOf(1, 2, 3, 4, 5)
        nums.forEach {
            if(it == 3){
                return@forEach
            }
            Log.e("zzz","nums----$it")
        }
        Log.e("zzz","end")
    }

    private fun initTest() {

//        lifecycleScope.launch {
//            mViewModel.state.onEach {
//                Log.e("zzz","state----$it")
//            }.collect()
//        }
//        GlobalScope.launch {
//            mViewModel.state.onEach {
//                Log.e("zzz","state----$it")
//            }.collect()
//        }
        mViewModel.viewModelScope.launch {
            mViewModel.state.onEach {
                Log.e("zzz","state----$it")
            }.collect {
                Log.e("zzz","collect--state----$it")
            }
        }

        mViewModel.viewModelScope.launch {
            mViewModel.shared.onEach {
                Log.e("zzz","shared----$it")
            }.collect {
                Log.e("zzz","collect--shared----$it")
            }
        }
    }


    private fun test(){
        mViewModel.changeState()
        mViewModel.changeShared()

    }

    private fun toAudio() {
        AudioRecorder.getInstance().recorderCallback = object : RecorderCallback {
            override fun onStartRecord(output: File?) {

            }

            override fun onPauseRecord() {

            }

            override fun onResumeRecord() {

            }

            override fun onRecordProgress(mills: Long, amp: Int) {

            }

            override fun onStopRecord(output: File?, duration: Long) {

            }

            override fun onError(throwable: AudioException?) {

            }

        }
        val fileRepository =
            FileRepositoryImpl.getInstance(AppHelper.getApplication())
        if (!fileRepository.hasAvailableSpace(AppHelper.getApplication())) { //存储空间不足
            if (AudioRecorder.getInstance().recorderCallback != null) {
                AudioRecorder.getInstance().recorderCallback.onError(
                    AudioRecordNoStorageSpaceException()
                )
            }
            return
        }
        if (true) {//有文件存储权限 有麦克风权限 再开始录音
            val file = fileRepository.provideRecordFile(AudioOutputFormat.AudioOutputFormatM4a)
            if (file == null) {
                if (AudioRecorder.getInstance().recorderCallback != null) {
                    AudioRecorder.getInstance().recorderCallback.onError(
                        AudioRecordCreateFileException()
                    )
                }
                return
            }
            val path = file.absolutePath
            AudioRecorder.getInstance().startRecording(
                path,
                AudioOutputFormat.AudioOutputFormatM4a,
                AudioConstants.CHANNEL_COUNT,
                AudioConstants.SAMPLE_RATE,
                AudioConstants.BITRATE,
                60
            )

        }
    }

    private fun showDialog() {
        var dialog: UniversalDialog? = null
        dialog = this@MineFragment.activity?.let {
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

                    override fun getContentWidth(): String? {
                        return "100%"
                    }

                    override fun getContentHeight(): String? {
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
                        var appContext = context.applicationContext
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
        };
        this@MineFragment.activity?.let { dialog?.dialogShow(it) }


    }

}