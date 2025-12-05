package com.demo.androidseedproject.compose.feature.mvi

import com.demo.androidseedproject.compose.feature.model.ListItem

/**
 * MVI 模式下的状态定义
 * 状态代表 UI 的完整状态，应该是不可变的
 */
data class ListState(
    val items: List<ListItem> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val error: String? = null,
    val selectedTabIndex: Int = 0,
    val isInitialized: Boolean = false,
    val scrollToIndex: Int? = null  // 滚动到指定位置的索引
) {

    /**
     * 检查是否处于加载状态
     */
    val isInLoadingState: Boolean
        get() = isLoading && !isRefreshing && !isLoadingMore

    /**
     * 检查是否显示内容
     */
    val shouldShowContent: Boolean
        get() = isInitialized || items.isNotEmpty()

    /**
     * 检查是否显示错误页面
     */
    val shouldShowError: Boolean
        get() = error != null && items.isEmpty()

    /**
     * 检查是否可以刷新
     */
    val canRefresh: Boolean
        get() = !isRefreshing && !isLoading

    /**
     * 检查是否可以加载更多
     */
    val canLoadMore: Boolean
        get() = hasMore && !isLoadingMore && !isRefreshing && !isLoading
}