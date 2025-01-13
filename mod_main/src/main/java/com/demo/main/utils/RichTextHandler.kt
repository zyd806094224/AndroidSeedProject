package com.demo.main.utils

import android.graphics.Color
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.util.Log
import android.view.View
import android.widget.TextView

/**
 * @Description:
 * @Date: 2025/1/3 11:07
 * @author:  zhaoyudong
 * @version: 1.0
 */
object RichTextHandler {

    fun setRichText(
        textView: TextView?,
        htmlText: String?,
        onILinkClickHandler: ILinkClickHandler? = null
    ) {
        if(textView == null || htmlText == null) return
        textView.highlightColor = Color.TRANSPARENT // 去掉点击后的背景颜色
        // 提取颜色值
        val linkStyleList = parseLinks(htmlText)
        // 解析 HTML 文本为 Spanned
        val spannedText: Spanned = Html.fromHtml(htmlText)
        // 转换 <a> 标签的点击事件为自定义逻辑
        val spannable = SpannableString(spannedText)
        val urlSpans = spannable.getSpans(0, spannable.length, URLSpan::class.java)

        for (i in urlSpans.indices) {
            val urlSpan = urlSpans[i]
            val start = spannable.getSpanStart(urlSpan)
            val end = spannable.getSpanEnd(urlSpan)
            spannable.removeSpan(urlSpan)
            // 替换为自定义的 ClickableSpan
            spannable.setSpan(object : ClickableSpan() {
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false // 去掉下划线
                    if(!TextUtils.isEmpty(linkStyleList[i].color)){
                        ds.color = Color.parseColor(linkStyleList[i].color!!)
                    }
                    if(!TextUtils.isEmpty(linkStyleList[i].fontSize)){
//                        ds.textSize =
//                            DpPxUtil.dip2px(linkStyleList[i].fontSize!!.substring(0, linkStyleList[i].fontSize!!.length - 2).toFloat())
//                                .toFloat()
                    }
                }

                override fun onClick(widget: View) {
                    // 自定义点击事件
                    handleLinkClick(urlSpan.url)
                    onILinkClickHandler?.onLinkClick(urlSpan.url)
                }
            }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // 设置 TextView 的内容和点击行为
        textView?.text = spannable
        textView?.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun handleLinkClick(url: String?) {
        // 自定义点击逻辑
        Log.e("zzz", "Link clicked: $url")
    }

    /**
     * 正则匹配出所有 <a> 标签的 style 属性，并提取 color 和 font-size 的值
     */
    private fun parseLinks(html: String): List<LinkStyle> {
        // 匹配所有 <a> 标签
        val regex = """<a [^>]*>""".toRegex()
        val matches = regex.findAll(html)
        val result = mutableListOf<LinkStyle>()
        // 遍历所有的 <a> 标签
        matches.forEach { matchResult ->
            val linkTag = matchResult.value
            // 提取style属性的内容
            val styleContent = linkTag.substringAfter("style=", "").substringBefore(">")
            // 提取color和font-size的正则
            val colorRegex = """color:\s*(#[0-9A-Fa-f]{6}|#[0-9A-Fa-f]{3})""".toRegex()
            val fontSizeRegex = """font-size:\s*([\d.]+px)""".toRegex()
            // 获取color和font-size的值，如果不存在则为null
            val color = colorRegex.find(styleContent)?.groups?.get(1)?.value
            val fontSize = fontSizeRegex.find(styleContent)?.groups?.get(1)?.value
            // 添加结果
            result.add(LinkStyle(color, fontSize))
        }
        return result
    }

    data class LinkStyle(
        val color: String?,
        val fontSize: String?
    )

    interface ILinkClickHandler {
        fun onLinkClick(url: String?)
    }
}