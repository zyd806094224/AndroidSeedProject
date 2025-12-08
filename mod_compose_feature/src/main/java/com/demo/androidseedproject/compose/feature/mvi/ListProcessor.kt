package com.demo.androidseedproject.compose.feature.mvi

import com.demo.androidseedproject.compose.feature.data.DataGenerator
import com.demo.androidseedproject.compose.feature.model.ListItem
import kotlinx.coroutines.delay

/**
 * MVI 模式下的 Processor
 * Processor 负责处理副作用（网络请求、数据库操作等）并生成 Action
 */
class ListProcessor {

    private val pageSize = 50  // 增加到50，确保包含所有锚定位置（最大锚定索引为45）

    /**
     * 处理 Intent 并生成对应的 Action
     * @param intent 用户意图
     * @param currentState 当前状态
     * @return 生成的 Action
     */
    suspend fun processIntent(intent: ListIntent, currentState: ListState): ListAction {
        return when (intent) {
            is ListIntent.LoadInitial -> {
                try {
                    delay(1000) // 模拟网络延迟
                    val initialData = DataGenerator.generateFakeData(0, pageSize)
                    ListAction.LoadInitialSuccess(initialData)
                } catch (e: Exception) {
                    ListAction.LoadInitialFailure(e.message ?: "加载失败")
                }
            }

            is ListIntent.Refresh -> {
                try {
                    delay(1500) // 模拟刷新延迟
                    val refreshedData = DataGenerator.generateFakeData(0, pageSize)
                    ListAction.RefreshSuccess(refreshedData)
                } catch (e: Exception) {
                    ListAction.RefreshFailure(e.message ?: "刷新失败")
                }
            }

            is ListIntent.LoadMore -> {
                try {
                    delay(1000) // 模拟加载延迟

                    val currentSize = currentState.items.size
                    val moreData = DataGenerator.generateFakeData(currentSize, pageSize)

                    // 模拟最多加载5页数据
                    val hasMoreData = currentSize / pageSize < 5

                    ListAction.LoadMoreSuccess(moreData, hasMoreData)
                } catch (e: Exception) {
                    ListAction.LoadMoreFailure(e.message ?: "加载更多失败")
                }
            }

            is ListIntent.ScrollToTab -> {
                // 查找对应的锚定位置
                val anchor = TabAnchors.getAnchorByIndex(intent.tabIndex)
                if (anchor != null) {
                    ListAction.ScrollToTabPosition(anchor.anchorIndex, intent.tabIndex)
                } else {
                    ListAction.UpdateSelectedTab(intent.tabIndex)
                }
            }

            is ListIntent.UpdateSelectedTab -> {
                // 只更新Tab状态，不触发滚动
                ListAction.UpdateSelectedTab(intent.tabIndex)
            }

            is ListIntent.ItemClick -> {
                // 处理项目点击，这里可以添加导航逻辑
                println("Item clicked: ${intent.item.title}")
                ListAction.ItemClicked(intent.item)
            }

            is ListIntent.Retry -> {
                // Retry 直接调用对应的处理逻辑，避免递归
                if (currentState.items.isEmpty()) {
                    try {
                        delay(1000) // 模拟网络延迟
                        val initialData = DataGenerator.generateFakeData(0, pageSize)
                        ListAction.LoadInitialSuccess(initialData)
                    } catch (e: Exception) {
                        ListAction.LoadInitialFailure(e.message ?: "加载失败")
                    }
                } else {
                    if (!currentState.canRefresh) {
                        return ListAction.ClearError
                    }

                    try {
                        delay(1500) // 模拟刷新延迟
                        val refreshedData = DataGenerator.generateFakeData(0, pageSize)
                        ListAction.RefreshSuccess(refreshedData)
                    } catch (e: Exception) {
                        ListAction.RefreshFailure(e.message ?: "刷新失败")
                    }
                }
            }
        }
    }

    /**
     * 生成开始动作的 Action
     * 这个方法用于生成开始各种操作的 Action
     */
    suspend fun generateStartAction(intent: ListIntent): ListAction {
        return when (intent) {
            is ListIntent.LoadInitial -> ListAction.LoadInitialStarted
            is ListIntent.Refresh -> ListAction.RefreshStarted
            is ListIntent.LoadMore -> ListAction.LoadMoreStarted
            is ListIntent.ScrollToTab -> ListAction.UpdateSelectedTab(intent.tabIndex)
            else -> ListAction.ClearError
        }
    }
}