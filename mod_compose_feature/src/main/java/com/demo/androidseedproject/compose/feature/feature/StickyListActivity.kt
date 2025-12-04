package com.demo.androidseedproject.compose.feature.feature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import com.alibaba.android.arouter.facade.annotation.Route
import com.demo.androidseedproject.compose.feature.components.*

@Route(path = "/compose/stickyList")
class StickyListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StickyListScreen(onClose = { finish() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun StickyListScreen(
    viewModel: ListViewModel = viewModel(),
    onClose: () -> Unit = {}
) {
    val state by remember { derivedStateOf { viewModel.state.value } }
    val lazyListState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshState()

    // 监听下拉刷新状态
    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.refresh()
            pullToRefreshState.endRefresh()
        }
    }

    // 监听ViewModel的刷新状态
    LaunchedEffect(state.isRefreshing) {
        if (state.isRefreshing) {
            pullToRefreshState.startRefresh()
        } else {
            pullToRefreshState.endRefresh()
        }
    }

    // 计算滚动偏移量，用于控制Tab栏的吸顶效果
    val scrollOffset = remember { derivedStateOf {
        lazyListState.firstVisibleItemScrollOffset
    } }

    val isTabSticky = remember { derivedStateOf {
        lazyListState.firstVisibleItemIndex >= 1 || scrollOffset.value > 0
    } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("吸顶列表") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "刷新")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isTabSticky.value) MaterialTheme.colorScheme.primary else Color.Transparent,
                    titleContentColor = if (isTabSticky.value) Color.White else MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = if (isTabSticky.value) Color.White else MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = if (isTabSticky.value) Color.White else MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            if (state.isLoading && state.items.isEmpty()) {
                LoadingContent()
            } else if (state.error?.isNotEmpty() == true) {
                ErrorContent(
                    error = state.error!!,
                    onRetry = { viewModel.retry() }
                )
            } else {
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
                            onTabSelected = { viewModel.switchTab(it) }
                        )
                    }

                    // 列表项
                    items(
                        items = state.items,
                        key = { item -> item.id }
                    ) { item ->
                        ListItem(
                            item = item,
                            onItemClicked = { viewModel.onItemClick(it) }
                        )
                    }

                    // 加载更多
                    item(key = "load_more") {
                        LoadMoreContent(
                            isLoadingMore = state.isLoadingMore,
                            hasMore = state.hasMore,
                            onLoadMore = { viewModel.loadMore() },
                            lazyListState = lazyListState
                        )
                    }
                }
            }

            // 下拉刷新指示器
            PullToRefreshContainer(
                state = pullToRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
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

