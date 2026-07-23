package com.demo.main.ui.im.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.demo.framework.adapter.BaseRecyclerViewAdapter
import com.demo.framework.adapter.BaseBindViewHolder
import com.demo.main.databinding.ItemImConversationBinding
import com.demo.shared.model.ImConversation

/**
 * 会话列表 Adapter
 *
 * @author zhaoyudong
 */
class ImConversationAdapter : BaseRecyclerViewAdapter<ImConversation, ItemImConversationBinding>() {

    override fun getViewBinding(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ItemImConversationBinding {
        return ItemImConversationBinding.inflate(layoutInflater, parent, false)
    }

    override fun onBindDefViewHolder(
        holder: BaseBindViewHolder<ItemImConversationBinding>,
        item: ImConversation?,
        position: Int
    ) {
        item ?: return
        holder.binding.apply {
            tvName.text = if (item.targetName.isNotEmpty()) item.targetName else "用户${item.targetId}"
            tvLastMsg.text = item.lastMsgContent
            tvTime.text = formatTime(item.lastMsgTime)

            if (item.unreadCount > 0) {
                tvUnread.visibility = android.view.View.VISIBLE
                tvUnread.text = if (item.unreadCount > 99) "99+" else item.unreadCount.toString()
            } else {
                tvUnread.visibility = android.view.View.GONE
            }
        }
    }

    private fun formatTime(timeStr: String): String {
        // 服务端返回 "yyyy-MM-dd HH:mm:ss"，只显示 HH:mm
        return try {
            if (timeStr.length >= 19) timeStr.substring(11, 16) else timeStr
        } catch (e: Exception) {
            timeStr
        }
    }
}
