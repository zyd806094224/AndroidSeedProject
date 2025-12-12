package com.demo.androidseedproject.compose.feature.feature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alibaba.android.arouter.facade.annotation.Route
import com.demo.androidseedproject.compose.feature.R

@Route(path = "/xiaohongshu/ios")
class SharedTransitionLayoutActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RedBookStyleWaterfallGrid()
        }
    }
}

data class ListItem(
    val id: Int,
    val title: String,
    val imageRes: Int,
    val height: Int
)

val sampleItems = listOf(
    ListItem(1, "如何选择一款适合自己的相机", R.drawable.cupcake, 200),
    ListItem(2, "秋季旅行穿搭指南", R.drawable.cupcake, 300),
    ListItem(3, "10分钟搞定美味早餐", R.drawable.cupcake, 250),
    ListItem(4, "家居收纳技巧，让你的家焕然一新", R.drawable.cupcake, 280),
    ListItem(5, "健身入门：如何制定你的第一个健身计划", R.drawable.cupcake, 220),
    ListItem(6, "周末去哪儿？发现城市周边的好去处", R.drawable.cupcake, 320),
    ListItem(7, "学习一门新语言的有效方法", R.drawable.cupcake, 270),
    ListItem(8, "如何拍出令人惊艳的风景照", R.drawable.cupcake, 290),
    ListItem(9, "打造属于你自己的家庭影院", R.drawable.cupcake, 240),
    ListItem(10, "美食探店：这家餐厅你绝对不能错过", R.drawable.cupcake, 310)
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RedBookStyleWaterfallGrid() {
    var selectedItem by remember { mutableStateOf<ListItem?>(null) }

    SharedTransitionLayout {
        AnimatedContent(
            targetState = selectedItem,
            label = "grid_transition"
        ) { item ->
            if (item == null) {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalItemSpacing = 8.dp,
                    content = {
                        items(sampleItems, key = { it.id }) { listItem ->
                            GridItem(
                                item = listItem,
                                onItemSelected = { selectedItem = it },
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibilityScope = this@AnimatedContent
                            )
                        }
                    }
                )
            } else {
                DetailItem(
                    item = item,
                    onBack = { selectedItem = null },
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@AnimatedContent
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun GridItem(
    item: ListItem,
    onItemSelected: (ListItem) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    with(sharedTransitionScope) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItemSelected(item) }
                .sharedElement(
                    state = rememberSharedContentState(key = "item-${item.id}"),
                    animatedVisibilityScope = animatedVisibilityScope
                ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column {
                Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(item.height.dp)
                        .sharedElement(
                            state = rememberSharedContentState(key = "image-${item.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                )
                Text(
                    text = item.title,
                    modifier = Modifier
                        .padding(8.dp)
                        .sharedElement(
                            state = rememberSharedContentState(key = "title-${item.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        ),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DetailItem(
    item: ListItem,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    BackHandler {
        onBack()
    }
    with(sharedTransitionScope) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .clickable { onBack() }
                .sharedElement(
                    state = rememberSharedContentState(key = "item-${item.id}"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
        ) {
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .sharedElement(
                        state = rememberSharedContentState(key = "image-${item.id}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
            )
            Text(
                text = item.title,
                modifier = Modifier
                    .padding(16.dp)
                    .sharedElement(
                        state = rememberSharedContentState(key = "title-${item.id}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    ),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "这里是详情内容...".repeat(20),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}
