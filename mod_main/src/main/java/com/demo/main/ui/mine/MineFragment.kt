package com.demo.main.ui.mine

import android.os.Bundle
import android.view.View
import com.demo.common.utils.UIKitUtil
import com.demo.framework.base.BaseMvvmFragment
import com.demo.main.databinding.FragmentMineBinding
import com.demo.main.ui.mine.viewmodel.MineViewModel

/**
 * @Description:
 * @Date: 2024/9/3 17:29
 * @author:  zhaoyudong
 * @version: 1.0
 */
class MineFragment : BaseMvvmFragment<FragmentMineBinding, MineViewModel>() {

    override fun initView(view: View, savedInstanceState: Bundle?) {
        val icon = "https://wos.58cdn.com.cn/cDazYxWcDHJ/picasso/dohan5dj__w328_h80.png"
        UIKitUtil.setPostTitle("测试测试测试测试测试测试测试测试测试", mBinding?.tv, icon, icon)
    }
}