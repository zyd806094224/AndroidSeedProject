package com.demo.androidseedproject.compose.feature.feature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.alibaba.android.arouter.facade.annotation.Route
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import com.demo.androidseedproject.compose.feature.components.*
import com.demo.androidseedproject.compose.feature.mvi.ListIntent
import com.demo.androidseedproject.compose.feature.mvi.TabAnchors
import kotlinx.coroutines.delay

@Route(path = "/compose/stickyList")
class StickyListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StickyListScreen(onClose = { finish() })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StickyListScreen(
    viewModel: ListViewModel = viewModel(),
    onClose: () -> Unit = {}
) {
    // MVI 模式：使用 StateFlow 收集状态
    val state by viewModel.state.collectAsState()
    val lazyListState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshState()

    // 记录是否是用户主动点击Tab，防止滚动监听器覆盖Tab状态
    var isUserTabClick by remember { mutableStateOf(false) }

    // 监听下拉刷新状态 - MVI 模式：发送 Intent
    LaunchedEffect(pullToRefreshState.isRefreshing) {
        if (pullToRefreshState.isRefreshing && !state.isRefreshing) {
            viewModel.handleIntent(ListIntent.Refresh)
        }
    }

    // 监听ViewModel的刷新状态
    LaunchedEffect(state.isRefreshing) {
        if (!state.isRefreshing && pullToRefreshState.isRefreshing) {
            // ViewModel 刷新已完成，结束下拉刷新状态
            delay(500) // 延迟一点时间让用户看到刷新完成状态
            pullToRefreshState.endRefresh()
        }
    }

    // 监听滚动到指定位置的请求
    LaunchedEffect(state.scrollToIndex) {
        state.scrollToIndex?.let { index ->
            if (index >= 0 && index < state.items.size) {
                lazyListState.animateScrollToItem(index)
                // 滚动完成后重置用户点击标志
                delay(300) // 等待滚动动画完成
                isUserTabClick = false
            }
        }
    }

    // 监听列表滚动位置，自动切换Tab
    LaunchedEffect(lazyListState.firstVisibleItemIndex, lazyListState.firstVisibleItemScrollOffset) {
        // 只有在不是用户主动点击Tab时才进行Tab自动切换
        if (!isUserTabClick) {
            // 获取当前可见的第一个item的索引（考虑头部和Tab栏）
            val firstVisibleIndex = lazyListState.firstVisibleItemIndex
            val firstVisibleScrollOffset = lazyListState.firstVisibleItemScrollOffset

            // 只有在列表内容区域滚动时才进行Tab切换（排除头部和Tab栏）
            if (firstVisibleIndex >= 2) { // 0: header, 1: stickyHeader (tab), 2+: list items
                // 计算实际的列表item索引（减去header和tab栏）
                val actualItemIndex = firstVisibleIndex - 2

                // 查找当前滚动位置应该对应的Tab
                val currentTabAnchor = TabAnchors.getAllAnchors().findLast { anchor ->
                    actualItemIndex >= anchor.anchorIndex
                }

                currentTabAnchor?.let { anchor ->
                    // 如果当前选中的Tab与计算出的Tab不同，则切换Tab
                    if (state.selectedTabIndex != anchor.tabIndex) {
                        // 使用 UpdateSelectedTab 而不是 ScrollToTab，避免触发重新滚动
                        viewModel.handleIntent(ListIntent.UpdateSelectedTab(anchor.tabIndex))
                    }
                }
            }
        }
    }

    // 计算滚动偏移量，用于控制Tab栏的吸顶效果
    val scrollOffset = remember { derivedStateOf {
        lazyListState.firstVisibleItemScrollOffset
    } }

    val isTabSticky = remember { derivedStateOf {
        lazyListState.firstVisibleItemIndex >= 1 || scrollOffset.value > 0
    } }

    // 使用标准的下拉刷新布局
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (state.shouldShowError) {
            ErrorContent(
                error = state.error ?: "未知错误",
                onRetry = {
                    viewModel.handleIntent(ListIntent.Retry)
                }
            )
        } else if (state.isLoading && state.items.isEmpty()) {
            LoadingContent()
        } else {
            // 使用标准的下拉刷新容器
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(pullToRefreshState.nestedScrollConnection)
            ) {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    // 头部区域 (index 0)
                    item(key = "header") {
                        ListHeader()
                    }

                    // Tab栏 (index 1)
                    stickyHeader(key = "tabs") {
                        TabBar(
                            tabs = defaultTabs,
                            selectedTabIndex = state.selectedTabIndex,
                            onTabSelected = { tabIndex ->
                                // 标记为用户主动点击Tab
                                isUserTabClick = true
                                viewModel.handleIntent(ListIntent.ScrollToTab(tabIndex))
                            }
                        )
                    }

                    // 列表项
                    items(
                        items = state.items,
                        key = { item -> item.id }
                    ) { item ->
                        ListItem(
                            item = item,
                            onItemClicked = { clickedItem ->
                                viewModel.handleIntent(ListIntent.ItemClick(clickedItem))
                            }
                        )
                    }

                    // 加载更多
                    item(key = "load_more") {
                        LoadMoreContent(
                            isLoadingMore = state.isLoadingMore,
                            hasMore = state.hasMore,
                            onLoadMore = {
                                viewModel.handleIntent(ListIntent.LoadMore)
                            },
                            lazyListState = lazyListState
                        )
                    }
                }

                // 标准的下拉刷新指示器
                PullToRefreshContainer(
                    state = pullToRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "加载中...",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "出错了",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("重试")
            }
        }
    }
}

@Composable
private fun LoadMoreContent(
    isLoadingMore: Boolean,
    hasMore: Boolean,
    onLoadMore: () -> Unit,
    lazyListState: LazyListState
) {
    // 监听滚动状态，在接近底部时触发加载更多
    LaunchedEffect(lazyListState) {
        snapshotFlow {
            val lastVisibleItemIndex = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItemsCount = lazyListState.layoutInfo.totalItemsCount

            // 当滚动到倒数第3个item时，触发加载更多
            lastVisibleItemIndex >= totalItemsCount - 3
        }.collect { shouldLoadMore ->
            if (shouldLoadMore && !isLoadingMore && hasMore) {
                onLoadMore()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoadingMore -> {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "加载中...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            !hasMore -> {
                Text(
                    text = "没有更多数据了",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}