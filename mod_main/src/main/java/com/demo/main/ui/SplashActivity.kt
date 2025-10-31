package com.demo.main.ui

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.demo.common.provider.MainServiceProvider
import com.demo.framework.base.BaseDataBindActivity
import com.demo.framework.ext.countDownCoroutines
import com.demo.framework.ext.onClick
import com.demo.framework.utils.StatusBarSettingHelper
import com.demo.main.R
import com.demo.main.databinding.ActivitySplashBinding

/**
 * @Description:
 * @Date: 2024/8/30 14:20
 * @author:  zhaoyudong
 * @version: 1.0
 */
class SplashActivity : BaseDataBindActivity<ActivitySplashBinding>() {

    override fun initView(savedInstanceState: Bundle?) {
        //处理每次进入应用都会创建启动页的问题
        if (!isTaskRoot) {
            finish()
            return
        }
        StatusBarSettingHelper.setStatusBarTranslucent(this)
        //倒计时
        val timer = countDownCoroutines(2, lifecycleScope, onTick = {
            mBinding.tvSkip.text = getString(R.string.splash_time, it.plus(1).toString())
        }) {
            MainServiceProvider.toMain(this)
            finish()
        }
        mBinding.tvSkip.onClick {
            timer.cancel()
            MainServiceProvider.toMain(this)
        }
    }

}