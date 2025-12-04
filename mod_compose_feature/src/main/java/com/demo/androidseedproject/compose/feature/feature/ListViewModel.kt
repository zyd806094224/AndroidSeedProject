package com.demo.androidseedproject.compose.feature.feature

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.androidseedproject.compose.feature.data.DataGenerator
import com.demo.androidseedproject.compose.feature.model.ListItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ListViewModel : ViewModel() {
    // 使用StateFlow来管理状态
    private val _state = mutableStateOf(ListState())
    val state = _state

    private val pageSize = 20

    // 更新状态的辅助函数
    private fun updateState(transform: ListState.() -> ListState) {
        _state.value = _state.value.transform()
    }

    init {
        loadInitialData()
    }

    // 加载初始数据
    private fun loadInitialData() {
        updateState { copy(isLoading = true) }
        viewModelScope.launch {
            delay(1000) // 模拟网络延迟

            val initialData = DataGenerator.generateFakeData(0, pageSize)
            updateState {
                ListState(
                    items = initialData,
                    isLoading = false,
                    isRefreshing = false,
                    isLoadingMore = false,
                    hasMore = true,
                    error = null
                )
            }
        }
    }

    // 下拉刷新
    fun refresh() {
        if (state.value.isRefreshing) return

        updateState { copy(isRefreshing = true, error = null) }
        viewModelScope.launch {
            delay(1500) // 模拟刷新延迟

            val refreshedData = DataGenerator.generateFakeData(0, pageSize)
            updateState {
                copy(
                    items = refreshedData,
                    isRefreshing = false,
                    isLoadingMore = false,
                    hasMore = true,
                    error = null
                )
            }
        }
    }

    // 加载更多
    fun loadMore() {
        if (state.value.isLoadingMore || !state.value.hasMore) return

        updateState { copy(isLoadingMore = true) }
        viewModelScope.launch {
            delay(1000) // 模拟加载延迟

            val currentSize = state.value.items.size
            val moreData = DataGenerator.generateFakeData(currentSize, pageSize)

            // 模拟最多加载5页数据
            val hasMoreData = currentSize / pageSize < 5

            val currentState = state.value
            updateState {
                copy(
                    items = currentState.items + moreData,
                    isLoadingMore = false,
                    hasMore = hasMoreData
                )
            }
        }
    }

    // 切换Tab
    fun switchTab(tabIndex: Int) {
        if (state.value.selectedTabIndex == tabIndex) return

        updateState {
            copy(
                selectedTabIndex = tabIndex,
                isLoading = true,
                items = emptyList()
            )
        }

        // 模拟根据Tab加载不同数据
        viewModelScope.launch {
            delay(800)

            // 根据Tab索引生成不同范围的数据，让不同Tab的内容看起来不同
            val offset = tabIndex * 100
            val tabData = DataGenerator.generateFakeData(offset, pageSize)

            updateState {
                copy(
                    items = tabData,
                    isLoading = false,
                    isRefreshing = false,
                    isLoadingMore = false,
                    hasMore = true,
                    error = null
                )
            }
        }
    }

    // 项目点击事件
    fun onItemClick(item: ListItem) {
        // 这里可以处理点击事件，比如跳转到详情页
        // 暂时打印日志
        println("Item clicked: ${item.title}")
    }

    // 重试加载
    fun retry() {
        if (state.value.items.isEmpty()) {
            loadInitialData()
        } else {
            refresh()
        }
    }
}

// 状态类
data class ListState(
    val items: List<ListItem> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val error: String? = null,
    val selectedTabIndex: Int = 0
)