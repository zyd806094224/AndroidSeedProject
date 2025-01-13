package com.demo.main.utils

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan

/**
 * @Description:
 * @Date: 2025/1/3 14:11
 * @author:  zhaoyudong
 * @version: 1.0
 */
object CommentTextHandler {

    /**
     * 根据回复名称、内容、日期生成评论文本
     * @param replyName 回复名称
     * @param content   内容
     * @param date      日期
     * @return SpannableString
     */
    fun getCommentText(replyName: String?, content: String?, date: String?): SpannableString {
        var fullText = ""
        var replyNameStart = -1
        var replyNameEnd = -1
        if (!TextUtils.isEmpty(replyName)) {//有回复的名称
            fullText += "回复"
            fullText += " $replyName: "
            replyNameStart = 2 + 1
            replyNameEnd = replyNameStart + replyName!!.length
        }
        var contentStart = -1
        var contentEnd = -1
        if (!TextUtils.isEmpty(content)) {
            fullText += content
            contentStart = if (replyNameEnd != -1) {
                replyNameEnd
            } else {
                fullText.length - content!!.length
            }
            contentEnd = fullText.length
        }
        var dateStart = -1
        var dateEnd = -1
        if (!TextUtils.isEmpty(date)) {
            fullText += " $date"
            dateStart = fullText.length - date!!.length
            dateEnd = fullText.length
        }
        // 创建 SpannableString 并为日期文本添加样式
        val spannable = SpannableString(fullText)
        if (replyNameStart != -1) {//有回复
            spannable.setSpan(
                ForegroundColorSpan(Color.BLACK),
                0,
                2,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannable.setSpan(
                ForegroundColorSpan(Color.GRAY),
                replyNameStart,
                replyNameEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            if (contentStart != -1 && contentEnd > contentStart) {
                spannable.setSpan(
                    ForegroundColorSpan(Color.BLACK),
                    contentStart,
                    contentEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            if (dateStart != -1 && dateEnd > dateStart) {
                // 设置日期文本的颜色和大小
                spannable.setSpan(
                    RelativeSizeSpan(0.75f),
                    dateStart,
                    dateEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable.setSpan(
                    NonBreakingSpan(Color.GRAY),
                    dateStart,
                    dateEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        } else {
            if (contentStart != -1 && contentEnd > contentStart) {
                // 设置普通文本颜色
                spannable.setSpan(
                    ForegroundColorSpan(Color.BLACK),
                    contentStart,
                    contentEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            if (dateStart != -1 && dateEnd > dateStart) {
                // 设置日期文本的颜色和大小
                spannable.setSpan(
                    RelativeSizeSpan(0.75f),
                    dateStart,
                    dateEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                // 设置日期部分为不可分割的整体
                spannable.setSpan(
                    NonBreakingSpan(Color.GRAY),
                    dateStart,
                    dateEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        return spannable
    }
}