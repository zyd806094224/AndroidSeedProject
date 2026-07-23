package com.demo.main.ui.im.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.demo.framework.adapter.BaseRecyclerViewAdapter
import com.demo.framework.adapter.BaseBindViewHolder
import com.demo.main.databinding.ItemImMessageBinding
import com.demo.shared.model.ImMessage
import com.demo.shared.model.MsgType

/**
 * 聊天消息列表 Adapter（区分自己/对方）
 *
 * @author zhaoyudong
 */
class ImMessageAdapter(
    var currentUserId: Long
) : BaseRecyclerViewAdapter<ImMessage, ItemImMessageBinding>() {

    override fun getViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemImMessageBinding {
        return ItemImMessageBinding.inflate(layoutInflater, parent, false)
    }

    override fun onBindDefViewHolder(
        holder: BaseBindViewHolder<ItemImMessageBinding>,
        item: ImMessage?,
        position: Int
    ) {
        item ?: return
        val isMe = item.senderId == currentUserId
        holder.binding.apply {
            if (isMe) {
                layoutLeft.visibility = View.GONE
                layoutRight.visibility = View.VISIBLE
                tvMsgRight.text = formatContent(item)
            } else {
                layoutRight.visibility = View.GONE
                layoutLeft.visibility = View.VISIBLE
                tvMsgLeft.text = formatContent(item)
            }
        }
    }

    private fun formatContent(item: ImMessage): String {
        return when (MsgType.of(item.msgType)) {
            MsgType.IMAGE -> "[图片]"
            MsgType.TEXT -> item.content
        }
    }
}
