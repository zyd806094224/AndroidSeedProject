package com.demo.androidseedproject.compose.feature.model

data class ListItem(
    val id: Long,
    val title: String,
    val description: String,
    val imageUrl: String? = null,
    val timestamp: Long,
    val category: String,
    val likesCount: Int,
    val commentsCount: Int
)