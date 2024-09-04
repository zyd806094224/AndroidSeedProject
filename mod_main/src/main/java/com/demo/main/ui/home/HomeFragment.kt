package com.demo.main.ui.home

import android.os.Bundle
import android.view.View
import com.demo.framework.base.BaseMvvmFragment
import com.demo.main.databinding.FragmentHomeBinding
import com.demo.main.ui.home.viewmodel.HomeViewModel

/**
 * @Description:
 * @Date: 2024/9/3 17:20
 * @author:  zhaoyudong
 * @version: 1.0
 */
class HomeFragment : BaseMvvmFragment<FragmentHomeBinding,HomeViewModel>() {

    override fun initView(view: View, savedInstanceState: Bundle?) {

    }

    override fun initData() {
        super.initData()
        mViewModel.getDataList().observe(this){

        }

        mViewModel.getDataList2().observe(this){

        }
    }

}