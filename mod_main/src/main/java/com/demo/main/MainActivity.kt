package com.demo.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.demo.common.constant.MAIN_ACTIVITY_HOME
import com.demo.framework.base.BaseDataBindActivity
import com.demo.main.databinding.ActivityMainBinding

/**
 * @Description: 主页
 * @Date: 2024/8/30 14:19
 * @author:  zhaoyudong
 * @version: 1.0
 */
@Route(path = MAIN_ACTIVITY_HOME)
class MainActivity : BaseDataBindActivity<ActivityMainBinding>(){

    companion object {
        fun start(context: Context, index: Int = 0) {
            val intent = Intent(context, MainActivity::class.java)
//            intent.putExtra(KEY_INDEX, index)
            context.startActivity(intent)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

}