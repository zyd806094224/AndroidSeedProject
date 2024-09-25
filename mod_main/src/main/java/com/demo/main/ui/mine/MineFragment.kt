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
        val text = "[1004040-安心投] <font color=#ff552e>收拾卫生</font>|<font color=#ff552e>打扫卫生</font>|兵哥佳正军人家政新房开荒高端家政商业保洁商铺保洁兼职司机搬家陪诊"
        UIKitUtil.setPostTitle(text, mBinding?.tv, icon, icon)
        UIKitUtil.setTextWidthSuffixIcon("q121212",mBinding?.tv2,icon,2f)

    }
}