package com.demo.main.ui.home

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.demo.framework.base.BaseMvvmFragment
import com.demo.main.databinding.FragmentHome1Binding
import com.demo.main.ui.home.adapter.HomeItemAdapter
import com.demo.main.ui.home.viewmodel.HomeViewModel
import com.demo.room.entity.DemoDataInfo

/**
 * @Description:
 * @Date: 2024/9/5 14:25
 * @author:  zhaoyudong
 * @version: 1.0
 */
class HomeTabFragment1 : BaseMvvmFragment<FragmentHome1Binding,HomeViewModel>(){

    private lateinit var homeItemAdapter: HomeItemAdapter

    override fun initView(view: View, savedInstanceState: Bundle?) {
        val spanCount = 1
        val manager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
        homeItemAdapter = HomeItemAdapter(requireContext())
        mBinding?.recyclerView?.apply {
            layoutManager = manager
            adapter = homeItemAdapter
        }
    }

    override fun initData() {
        super.initData()
        val dataList = mutableListOf<DemoDataInfo>()
        repeat(30){
            dataList.add(DemoDataInfo(1,title = "测试","",""))
        }
        homeItemAdapter.setData(dataList)
    }

}