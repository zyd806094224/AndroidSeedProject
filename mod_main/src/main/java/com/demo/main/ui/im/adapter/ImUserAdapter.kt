package com.demo.main.ui.im.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.demo.framework.adapter.BaseRecyclerViewAdapter
import com.demo.framework.adapter.BaseBindViewHolder
import com.demo.main.databinding.ItemImUserBinding
import com.demo.shared.model.SimpleUser

/**
 * 用户列表 Adapter
 *
 * @author zhaoyudong
 */
class ImUserAdapter : BaseRecyclerViewAdapter<SimpleUser, ItemImUserBinding>() {

    override fun getViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemImUserBinding {
        return ItemImUserBinding.inflate(layoutInflater, parent, false)
    }

    override fun onBindDefViewHolder(
        holder: BaseBindViewHolder<ItemImUserBinding>,
        item: SimpleUser?,
        position: Int
    ) {
        item ?: return
        holder.binding.tvName.text =
            if (item.nickName.isNotEmpty()) item.nickName else item.userName
    }
}
