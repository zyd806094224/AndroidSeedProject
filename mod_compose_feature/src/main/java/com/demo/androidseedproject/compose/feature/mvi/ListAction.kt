package com.demo.androidseedproject.compose.feature.mvi

import com.demo.androidseedproject.compose.feature.model.ListItem

/**
 * MVI 模式下的 Action 定义
 * Action 代表 ViewModel 内部执行的具体动作，用于 Reducer 处理
 */
sealed interface ListAction {

    /**
     * 开始加载初始数据
     */
    data object LoadInitialStarted : ListAction

    /**
     * 初始数据加载成功
     */
    data class LoadInitialSuccess(val items: List<ListItem>) : ListAction

    /**
     * 初始数据加载失败
     */
    data class LoadInitialFailure(val error: String) : ListAction

    /**
     * 开始刷新
     */
    data object RefreshStarted : ListAction

    /**
     * 刷新成功
     */
    data class RefreshSuccess(val items: List<ListItem>) : ListAction

    /**
     * 刷新失败
     */
    data class RefreshFailure(val error: String) : ListAction

    /**
     * 开始加载更多
     */
    data object LoadMoreStarted : ListAction

    /**
     * 加载更多成功
     */
    data class LoadMoreSuccess(val items: List<ListItem>, val hasMore: Boolean) : ListAction

    /**
     * 加载更多失败
     */
    data class LoadMoreFailure(val error: String) : ListAction

    /**
     * 滚动到指定位置
     */
    data class ScrollToPosition(val index: Int) : ListAction

    /**
     * 更新选中的 Tab 索引
     */
    data class UpdateSelectedTab(val tabIndex: Int) : ListAction

    /**
     * 处理列表项点击
     */
    data class ItemClicked(val item: ListItem) : ListAction

    /**
     * 清除错误状态
     */
    data object ClearError : ListAction
}