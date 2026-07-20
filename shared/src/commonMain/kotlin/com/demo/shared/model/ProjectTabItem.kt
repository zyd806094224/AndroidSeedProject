package com.demo.shared.model

/**
 * @Description: 跨平台业务模型示例（从 lib_common 下沉的纯 Kotlin 数据类）。
 *               原始实现保留在 lib_common 不动，此处为跨平台副本，供 shared 复用。
 */
data class ProjectTabItem(
    val id: Int,
    val name: String
)
