package com.demo.androidseedproject.compose.feature.mvi

/**
 * Tab 锚定配置
 * 定义每个 Tab 对应的滚动位置和相关的数据标识
 */
data class TabAnchor(
    val tabIndex: Int,
    val title: String,
    val anchorIndex: Int,  // 滚动到列表中的第几个项目
    val dataPrefix: String // 数据前缀，用于标识这个 Tab 对应的数据
)

/**
 * Tab 锚定配置表
 */
object TabAnchors {
    private val tabAnchors = listOf(
        TabAnchor(0, "推荐", 0, "rec"),
        TabAnchor(1, "热门", 15, "hot"),
        TabAnchor(2, "最新", 30, "new"),
        TabAnchor(3, "关注", 45, "follow")
    )

    fun getAnchorByIndex(index: Int): TabAnchor? {
        return tabAnchors.find { it.tabIndex == index }
    }

    fun getAllAnchors(): List<TabAnchor> = tabAnchors

    fun getAnchorCount(): Int = tabAnchors.size
}