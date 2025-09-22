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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
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
            EditTextActivity.start(requireContext())
        }

        initTest()
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


    fun rxJava3() {
//        Observable.just("").

    }


}