package com.demo.main.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.demo.framework.adapter.BaseBindViewHolder
import com.demo.framework.adapter.BaseRecyclerViewAdapter
import com.demo.main.databinding.LayoutHomeItemBinding
import com.demo.room.entity.DemoDataInfo

/**
 * @Description:
 * @Date: 2024/9/5 15:24
 * @author:  zhaoyudong
 * @version: 1.0
 */
class HomeItemAdapter(val context: Context) : BaseRecyclerViewAdapter<DemoDataInfo, LayoutHomeItemBinding>(){

    override fun onBindDefViewHolder(
        holder: BaseBindViewHolder<LayoutHomeItemBinding>,
        item: DemoDataInfo?,
        position: Int
    ) {
        item?.apply {
            holder.binding.tv.text = title
        }
    }

    override fun getViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): LayoutHomeItemBinding {
        return LayoutHomeItemBinding.inflate(layoutInflater, parent, false)
    }

}