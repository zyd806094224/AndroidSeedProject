package com.demo.framework.adapter

import android.util.SparseArray
import androidx.core.util.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * @Description: FragmentStateAdapter
 * @Date: 2024/9/5 13:55
 * @author:  zhaoyudong
 * @version: 1.0
 */
class ViewPage2FragmentAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private var fragments: SparseArray<Fragment>
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    /**class ViewPage2FragmentAdapter(activity: FragmentActivity, var fragments: SparseArray<Fragment>) :
    FragmentStateAdapter(activity) {*/

    private var isDestroyed = false

    //FragmentStateAdapter内部自己会管理已实例化的fragment对象，所以不需要考虑复用的问题。
    override fun createFragment(i: Int): Fragment {
        // 防止在Adapter被销毁后创建Fragment
        if (isDestroyed) {
            throw IllegalStateException("Adapter has been destroyed")
        }
        return fragments[i]
    }

    override fun getItemCount(): Int {
        return if (isDestroyed) 0 else fragments.size
    }

    fun setData(fragments: SparseArray<Fragment>) {
        this.fragments = fragments
    }

    /**
     * 清理Fragment引用，防止内存泄漏
     */
    fun destroy() {
        isDestroyed = true
        fragments.clear()
        fragments = SparseArray()
    }

    /**
     * 获取指定位置的Fragment（如果有内存泄漏风险，可以考虑返回null）
     */
    fun getFragmentAt(position: Int): Fragment? {
        return if (position < fragments.size()) fragments[position] else null
    }
}