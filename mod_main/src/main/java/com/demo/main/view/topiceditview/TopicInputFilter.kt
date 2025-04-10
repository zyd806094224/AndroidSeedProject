package com.demo.main.view.topiceditview

import android.text.InputFilter
import android.text.Spanned

/**
 * @Description:
 * @Date: 2025/4/1 19:27
 * @author:  zhaoyudong
 */
class TopicInputFilter : InputFilter {

    // 定义正则表达式：从光标位置向前匹配第一个#号及其后的内容
    private val regex = Regex(".*?(#\\S*)(?=\$)")

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val currentText = dest.subSequence(0, dstart).toString() // 获取光标前文本
        val matchResult = regex.find(currentText)
        val matchedText = matchResult?.groups?.get(1)?.value ?: return null // 捕获#号后的内容

        // 规则触发条件：匹配段长度≥10且新输入字符不是#
        if(matchedText.length == TopicEditText.TOPIC_MAX_WORDS_LIMIT + 1){
            if (source.toString() != "#" && source.toString() != " " && source.toString() != " ") {
//                ToastUtils.showToastCenter(DaojiaApplication.getAppContext(),"话题最多允许输入${TopicEditText.TOPIC_MAX_WORDS_LIMIT}个字符")
                return "" // 拦截非#号输入
            }
        }
        return null // 允许正常输入
    }

}