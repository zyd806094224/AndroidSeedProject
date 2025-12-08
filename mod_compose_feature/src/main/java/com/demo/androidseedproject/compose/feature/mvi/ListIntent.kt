package com.demo.androidseedproject.compose.feature.mvi

import com.demo.androidseedproject.compose.feature.model.ListItem

/**
 * MVI 模式下的 Intent 定义
 * Intent 代表用户的操作意图，不包含具体的业务逻辑
 */
sealed interface ListIntent {

    /**
     * 初始化加载数据
     */
    data object LoadInitial : ListIntent

    /**
     * 下拉刷新
     */
    data object Refresh : ListIntent

    /**
     * 加载更多数据
     */
    data object LoadMore : ListIntent

    /**
     * 滚动到指定 Tab 对应的位置（用户主动点击Tab时使用）
     */
    data class ScrollToTab(val tabIndex: Int) : ListIntent

    /**
     * 更新选中的Tab（滚动列表时使用，只更新Tab状态，不触发滚动）
     */
    data class UpdateSelectedTab(val tabIndex: Int) : ListIntent

    /**
     * 点击列表项
     */
    data class ItemClick(val item: ListItem) : ListIntent

    /**
     * 重试加载
     */
    data object Retry : ListIntent
}