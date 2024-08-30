package com.demo.main.service

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.demo.common.constant.MAIN_SERVICE_HOME
import com.demo.common.service.IMainService
import com.demo.main.MainActivity

/**
 * @Description:
 * @Date: 2024/8/30 14:56
 * @author:  zhaoyudong
 * @version: 1.0
 */
@Route(path = MAIN_SERVICE_HOME)
class MainService : IMainService{

    /**
     * 主页跳转
     */
    override fun toMain(context: Context, index: Int) {
        MainActivity.start(context, index)
    }

    override fun init(context: Context?) {

    }

}