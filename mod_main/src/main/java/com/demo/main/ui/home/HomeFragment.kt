package com.demo.main.ui.home

import android.graphics.Typeface
import android.os.Bundle
import android.util.SparseArray
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.demo.common.model.ProjectTabItem
import com.demo.framework.adapter.ViewPage2FragmentAdapter
import com.demo.framework.base.BaseMvvmFragment
import com.demo.main.R
import com.demo.main.databinding.FragmentHomeBinding
import com.demo.main.ui.home.viewmodel.HomeViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener

/**
 * @Description:
 * @Date: 2024/9/3 17:20
 * @author:  zhaoyudong
 * @version: 1.0
 */
class HomeFragment : BaseMvvmFragment<FragmentHomeBinding, HomeViewModel>(), OnRefreshListener {

    private val mArrayTabFragments = SparseArray<Fragment>()
    private var mFragmentAdapter: ViewPage2FragmentAdapter? = null
    private var mProjectTabs: MutableList<ProjectTabItem> = mutableListOf()
    private var mTabLayoutMediator: TabLayoutMediator? = null

    override fun initView(view: View, savedInstanceState: Bundle?) {
        mBinding?.refreshLayout?.apply {
            autoRefresh()
            setEnableRefresh(true)
            setEnableLoadMore(false)
            setOnRefreshListener(this@HomeFragment)
        }

        initTab()
    }

    private fun initTab() {
        mArrayTabFragments.append(0, HomeTabFragment1())
        mProjectTabs.add(0, ProjectTabItem(id = 0, "条目一"))
        activity?.let {
            mFragmentAdapter =
                ViewPage2FragmentAdapter(childFragmentManager, lifecycle, mArrayTabFragments)
//            mFragmentAdapter = ViewPage2FragmentAdapter(it, mArrayTabFragments)
        }
        mBinding?.let {
            it.viewPager.adapter = mFragmentAdapter
            //可左右滑动
            it.viewPager.isUserInputEnabled = true
            //禁用预加载
            //需要注意是FragmentStateAdapter不会一直保持Fragment实例，在被destroy后，需要做好Fragment重建后回复数据的准备，这点可以结合ViewModel来进行配合使用。
            it.viewPager.offscreenPageLimit = mArrayTabFragments.size()

            mTabLayoutMediator =
                TabLayoutMediator(it.tabHome, it.viewPager) { tab: TabLayout.Tab, position: Int ->
                    tab.text = mProjectTabs[position].name
                }
            //tabLayout和viewPager2关联起来
            mTabLayoutMediator?.attach()

            //增加tab选择监听
            it.tabHome.addOnTabSelectedListener(tabSelectedCall)
            //设置第一个tab效果
            val tabFirst = it.tabHome.getTabAt(0)
            setTabTextSize(tabFirst)
        }
    }

    /**
     * tab选择回调
     */
    private val tabSelectedCall = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            setTabTextSize(tab)
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
            //非选中效果在xml中设置
            tab?.customView = null
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
        }
    }

    /**
     * 设置tab大小加粗效果
     */
    private fun setTabTextSize(tabFirst: TabLayout.Tab?) {
        TextView(requireContext()).apply {
            typeface = Typeface.DEFAULT_BOLD
            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }.also {
            it.text = tabFirst?.text
            tabFirst?.customView = it
        }
    }

    override fun initData() {
        super.initData()

        mViewModel.getDataList().observe(this) {
            mBinding?.refreshLayout?.finishRefresh()
        }

        mViewModel.getDataList2().observe(this) {

            mProjectTabs.add(ProjectTabItem(id = 1, "条目二"))
            mArrayTabFragments.append(1, HomeTabFragment2())

            mFragmentAdapter?.setData(mArrayTabFragments)
            mFragmentAdapter?.notifyItemRangeChanged(1, mArrayTabFragments.size())

            // 解决 TabLayout 刷新数据后滚动到错误位置
            mBinding?.tabHome?.let {
                it.post { it.getTabAt(0)?.select() }
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        refresh()
    }

    private fun refresh() {
//        mViewModel.launchUI(errorBlock = {code,err ->
//
//        }){
//
//        }

        mBinding?.refreshLayout?.finishRefresh()
    }

    override fun onDestroy() {
        super.onDestroy()
        mTabLayoutMediator?.detach()
    }

}