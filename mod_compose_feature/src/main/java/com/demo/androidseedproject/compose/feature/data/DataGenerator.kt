package com.demo.androidseedproject.compose.feature.data

import com.demo.androidseedproject.compose.feature.model.ListItem
import kotlin.random.Random

object DataGenerator {

    private val titles = listOf(
        "探索宇宙奥秘", "人工智能新突破", "气候变化挑战", "量子计算进展",
        "生物技术革命", "未来城市发展", "太空探索计划", "新能源技术",
        "医疗健康创新", "教育模式变革", "数字经济发展", "智能制造升级"
    )

    private val descriptions = listOf(
        "这是一项令人振奋的技术突破，将彻底改变我们的生活方式。",
        "科学家们经过长期研究，终于在这个领域取得了重大进展。",
        "这个发现具有重要的科学意义，为未来发展开辟了新道路。",
        "创新技术的应用前景广阔，有望带来巨大的社会效益。",
        "通过深入分析，我们发现了新的解决方案。",
        "这一成就标志着人类在科技进步道路上的重要里程碑。"
    )

    private val categories = listOf(
        "科技", "健康", "教育", "环境", "经济", "社会", "文化", "体育"
    )

    fun generateFakeData(startIndex: Int = 0, count: Int = 20): List<ListItem> {
        // 计算30天的毫秒数，使用Long避免溢出
        val thirtyDaysMs = 30L * 24 * 60 * 60 * 1000

        return (startIndex until startIndex + count).map { index ->
            ListItem(
                id = index.toLong(),
                title = titles.random() + " #${index + 1}",
                description = descriptions.random(),
                timestamp = System.currentTimeMillis() - Random.nextLong(0, thirtyDaysMs), // 过去30天内
                category = categories.random(),
                likesCount = Random.nextInt(0, 10000),
                commentsCount = Random.nextInt(0, 1000)
            )
        }
    }

    fun generateSingleItem(id: Long): ListItem {
        // 计算30天的毫秒数，使用Long避免溢出
        val thirtyDaysMs = 30L * 24 * 60 * 60 * 1000

        return ListItem(
            id = id,
            title = titles.random(),
            description = descriptions.random(),
            timestamp = System.currentTimeMillis() - Random.nextLong(0, thirtyDaysMs),
            category = categories.random(),
            likesCount = Random.nextInt(0, 10000),
            commentsCount = Random.nextInt(0, 1000)
        )
    }
}