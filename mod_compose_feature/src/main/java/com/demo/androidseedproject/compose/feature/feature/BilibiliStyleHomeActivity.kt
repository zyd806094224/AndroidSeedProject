package com.demo.androidseedproject.compose.feature.feature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alibaba.android.arouter.facade.annotation.Route
import com.demo.androidseedproject.compose.feature.components.*
import com.demo.androidseedproject.compose.feature.model.ListItem
import com.demo.androidseedproject.compose.feature.mvi.ListIntent
import kotlinx.coroutines.delay

@Route(path = "/compose/bilibiliStyleHome")
class BilibiliStyleHomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BilibiliStyleHomeScreen(onClose = { finish() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BilibiliStyleHomeScreen(
    viewModel: ListViewModel = viewModel(),
    onClose: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val lazyListState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshState()

    // 初始化加载数据
    LaunchedEffect(Unit) {
        if (!state.isInitialized) {
            viewModel.handleIntent(ListIntent.LoadInitial)
        }
    }

    // 监听下拉刷新状态
    LaunchedEffect(pullToRefreshState.isRefreshing) {
        if (pullToRefreshState.isRefreshing && !state.isRefreshing) {
            viewModel.handleIntent(ListIntent.Refresh)
        }
    }

    // 监听ViewModel的刷新状态
    LaunchedEffect(state.isRefreshing) {
        if (!state.isRefreshing && pullToRefreshState.isRefreshing) {
            pullToRefreshState.endRefresh()
        }
    }

    // 计算下拉偏移量，实现"拉出"效果
    val pullOffset = remember { derivedStateOf {
        if (pullToRefreshState.progress > 0) {
            pullToRefreshState.progress * 100 // 最多下拉100dp
        } else {
            0f
        }
    } }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (state.shouldShowError) {
            BilibiliErrorContent(
                error = state.error ?: "未知错误",
                onRetry = {
                    viewModel.handleIntent(ListIntent.Retry)
                }
            )
        } else if (state.isLoading && state.items.isEmpty()) {
            BilibiliLoadingContent()
        } else {
            // 整个容器处理下拉刷新
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(pullToRefreshState.nestedScrollConnection)
            ) {
                // 主内容区域 - 根据下拉进度向下偏移
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = pullOffset.value.dp),
                    contentPadding = PaddingValues(bottom = 80.dp) // 给底部导航留空间
                ) {
                    // 1. 顶部渐变头部区域
                    item(key = "bilibili_header") {
                        BilibiliHeader()
                    }

                    // 2. 快捷入口区域
                    item(key = "quick_entry") {
                        QuickEntrySection()
                    }

                    // 3. 横向滚动推荐区域
                    item(key = "banner_section") {
                        BannerSection()
                    }

                    // 4. Tab栏 (sticky)
                    stickyHeader(key = "tabs") {
                        TabBar(
                            tabs = bilibiliTabs,
                            selectedTabIndex = state.selectedTabIndex,
                            onTabSelected = { tabIndex ->
                                viewModel.handleIntent(ListIntent.ScrollToTab(tabIndex))
                            }
                        )
                    }

                    // 5. 列表项
                    items(
                        items = state.items,
                        key = { item -> item.id }
                    ) { item ->
                        BilibiliVideoCard(
                            item = item,
                            onItemClicked = { clickedItem ->
                                viewModel.handleIntent(ListIntent.ItemClick(clickedItem))
                            }
                        )
                    }

                    // 6. 加载更多
                    item(key = "load_more") {
                        BilibiliLoadMoreContent(
                            isLoadingMore = state.isLoadingMore,
                            hasMore = state.hasMore,
                            onLoadMore = {
                                viewModel.handleIntent(ListIntent.LoadMore)
                            },
                            lazyListState = lazyListState
                        )
                    }
                }

                // 刷新指示器 - 固定在顶部，不会被内容覆盖
                PullToRefreshContainer(
                    state = pullToRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

// B站风格的头部
@Composable
fun BilibiliHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFB7299),  // B站粉色
                        Color(0xFFF5F5F5)   // 过渡到浅灰色
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 顶部导航栏
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo区域
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "B",
                            color = Color(0xFFFB7299),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    Text(
                        text = "哔哩哔哩",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 右侧功能区
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "搜索",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "通知",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "我的",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 搜索框
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "搜索",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "搜索视频、番剧、UP主",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// 快捷入口区域
@Composable
fun QuickEntrySection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // 横向4列的快捷入口
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            QuickEntryItem(icon = Icons.Default.LiveTv, title = "直播")
            QuickEntryItem(icon = Icons.Default.PlayArrow, title = "番剧")
            QuickEntryItem(icon = Icons.Default.MusicNote, title = "音乐")
            QuickEntryItem(icon = Icons.Default.VideogameAsset, title = "游戏")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            QuickEntryItem(icon = Icons.Default.Movie, title = "电影")
            QuickEntryItem(icon = Icons.Default.Tv, title = "电视剧")
            QuickEntryItem(icon = Icons.Default.FoodBank, title = "美食")
            QuickEntryItem(icon = Icons.Default.More, title = "更多")
        }
    }
}

@Composable
fun QuickEntryItem(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable { /* TODO: 处理点击 */ },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFFFB7299),
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = title,
            fontSize = 12.sp,
            color = Color(0xFF333333)
        )
    }
}

// 横向滚动推荐区域
@Composable
fun BannerSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "热门推荐",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) {
            items(5) { index ->
                BannerCard(title = "推荐内容 ${index + 1}")
            }
        }
    }
}

@Composable
fun BannerCard(
    title: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(140.dp)
            .height(80.dp)
            .clickable { /* TODO: 处理点击 */ },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = Color(0xFF333333),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// B站风格的视频卡片
@Composable
fun BilibiliVideoCard(
    item: ListItem,
    onItemClicked: (ListItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var isLiked by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onItemClicked(item) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // 左侧缩略图区域
            Box(
                modifier = Modifier
                    .size(120.dp, 90.dp)
                    .background(
                        color = Color(0xFFF0F0F0),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .clip(RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "播放",
                        tint = Color(0xFFFB7299),
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "${(1..99).random()}万播放",
                        fontSize = 10.sp,
                        color = Color.White,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 右侧内容区域
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // 标题
                    Text(
                        text = item.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333),
                        maxLines = 2,
                        lineHeight = 18.sp
                    )

                    // UP主
                    Text(
                        text = "UP主: ${item.category}",
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                }

                // 底部互动信息
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 时间
                    Text(
                        text = formatBilibiliTimestamp(item.timestamp),
                        fontSize = 11.sp,
                        color = Color(0xFF999999)
                    )

                    // 点赞和弹幕数
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.clickable { isLiked = !isLiked },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "点赞",
                                tint = if (isLiked) Color(0xFFFB7299) else Color(0xFF999999),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (isLiked) (item.likesCount + 1).toString() else item.likesCount.toString(),
                                fontSize = 11.sp,
                                color = if (isLiked) Color(0xFFFB7299) else Color(0xFF999999)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "弹幕",
                                tint = Color(0xFF999999),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = item.commentsCount.toString(),
                                fontSize = 11.sp,
                                color = Color(0xFF999999)
                            )
                        }
                    }
                }
            }
        }
    }
}

// B站风格的错误页面
@Composable
fun BilibiliErrorContent(
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
            // 错误图标
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = Color(0xFFFFF0F0),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = "错误",
                    tint = Color(0xFFFB7299),
                    modifier = Modifier.size(40.dp)
                )
            }

            Text(
                text = "加载失败",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF333333)
            )

            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF999999),
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFB7299)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "重新加载",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

// B站风格的加载内容
@Composable
fun BilibiliLoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = Color(0xFFFB7299)
            )
            Text(
                text = "正在加载...",
                color = Color(0xFF333333),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

// B站风格的加载更多
@Composable
fun BilibiliLoadMoreContent(
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
                        strokeWidth = 2.dp,
                        color = Color(0xFFFB7299)
                    )
                    Text(
                        text = "加载中...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF999999)
                    )
                }
            }
            !hasMore -> {
                Text(
                    text = "─ 没有更多内容了 ─",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF999999),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

// B站风格的时间格式化
private fun formatBilibiliTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val minutes = diff / (1000 * 60)
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 60 -> "${minutes}分钟前"
        hours < 24 -> "${hours}小时前"
        days < 30 -> "${days}天前"
        else -> "${days / 30}个月前"
    }
}

// B站风格的Tab选项
val bilibiliTabs = listOf(
    "推荐", "关注", "热门", "影视", "游戏"
)