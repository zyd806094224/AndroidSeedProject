package com.demo.androidseedproject.compose.feature.mvi

/**
 * MVI 模式下的 Reducer
 * Reducer 是纯函数，负责根据当前状态和 Action 生成新状态
 */
object ListReducer {

    /**
     * 根据当前状态和 Action 生成新状态
     * @param currentState 当前状态
     * @param action 要执行的 Action
     * @return 新的状态
     */
    fun reduce(currentState: ListState, action: ListAction): ListState {
        return when (action) {
            // 初始加载相关
            is ListAction.LoadInitialStarted -> currentState.copy(
                isLoading = true,
                error = null
            )

            is ListAction.LoadInitialSuccess -> currentState.copy(
                items = action.items,
                isLoading = false,
                isRefreshing = false,
                isLoadingMore = false,
                hasMore = true,
                error = null,
                isInitialized = true
            )

            is ListAction.LoadInitialFailure -> currentState.copy(
                isLoading = false,
                error = action.error
            )

            // 刷新相关
            is ListAction.RefreshStarted -> currentState.copy(
                isRefreshing = true,
                error = null
            )

            is ListAction.RefreshSuccess -> currentState.copy(
                items = action.items,
                isRefreshing = false,
                isLoadingMore = false,
                hasMore = true,
                error = null
            )

            is ListAction.RefreshFailure -> currentState.copy(
                isRefreshing = false,
                error = action.error
            )

            // 加载更多相关
            is ListAction.LoadMoreStarted -> currentState.copy(
                isLoadingMore = true
            )

            is ListAction.LoadMoreSuccess -> currentState.copy(
                items = currentState.items + action.items,
                isLoadingMore = false,
                hasMore = action.hasMore
            )

            is ListAction.LoadMoreFailure -> currentState.copy(
                isLoadingMore = false,
                error = action.error
            )

            // 滚动相关
            is ListAction.ScrollToPosition -> currentState.copy(
                scrollToIndex = action.index
            )

            is ListAction.ScrollToTabPosition -> currentState.copy(
                selectedTabIndex = action.tabIndex,
                scrollToIndex = action.index
            )

            is ListAction.UpdateSelectedTab -> currentState.copy(
                selectedTabIndex = action.tabIndex,
                scrollToIndex = null  // 重置滚动索引
            )

            // 项目点击
            is ListAction.ItemClicked -> {
                // 项目点击通常不改变状态，只是触发副作用
                currentState
            }

            // 清除错误
            is ListAction.ClearError -> currentState.copy(
                error = null
            )
        }
    }
}