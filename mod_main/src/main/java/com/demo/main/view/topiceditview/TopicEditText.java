package com.demo.main.view.topiceditview;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ReplacementTransformationMethod;
import android.text.style.BackgroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:
 * @Date: 2025/3/17 14:47
 * @author: zhaoyudong
 */
public class TopicEditText extends androidx.appcompat.widget.AppCompatEditText {
    private int preTextLength = 0;//EditText之前的长度

    // 话题文本高亮颜色
    private final int mForegroundColor = Color.parseColor("#133667");

    // 话题背景高亮颜色
    private final int mBackgroundColor = Color.parseColor("#4DFF5500");

    private final String PREFIX_TAG = "#";
    private final String END_TAG = " ";
    private String tagRegex = DEF_REGEX;

    public static final String DEF_REGEX = "#[^#|\\s]+?\\s|#[^#|\\s]+?$";     //默认的正则表达式
    //话题内容最大长度限制
    public static final int TOPIC_MAX_WORDS_LIMIT = 20;
    private boolean isChanged;
    /**
     * 通过正则表达式解析文本，提取话题集合
     * 1、包含开头#，不包含末尾空格
     * 2、包含自定义话题
     */
    private List<String> tagList = new ArrayList<>();
    /**
     * 选中的系统提供的话题（有Id，没有#）
     */
    private List<TagInfo> tagInfoList = new ArrayList<>();
    /**
     * 记录选中的话题（包含#）
     */
    private String selectTagStr;

    public void setTagInfoList(List<TagInfo> tagInfoList) {
        this.tagInfoList = tagInfoList;
    }

    public List<TagInfo> getTagInfoList() {
        return tagInfoList;
    }

    public TopicEditText(Context context) {
        super(context);
        initView();
    }

    public TopicEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TopicEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    /**
     * 插入话题
     *
     * @param tagInfo 话题内容（包含#）
     */
    public void insertTopic(TagInfo tagInfo, int maxLength) {
        String topicStr = tagInfo.getContent();
        if (TextUtils.isEmpty(topicStr)) {
            return;
        }
        topicStr = PREFIX_TAG + topicStr;
        int index = getSelectionStart();
        Editable editable = getText();
        if (topicStr.length() >= maxLength - editable.length()) {
            //超出最大长度了
            return;
        }

        if (topicStr.length() > 1) {
            editable.insert(index, topicStr.trim() + END_TAG);
            tagInfoList.add(tagInfo);
        } else {
            // 自定义话题(输入#)
            editable.insert(index, topicStr.trim());
        }
        setSelection(index + topicStr.length() + END_TAG.length());
    }

    private String beforeStr = "";
    private String afterStr = "";

    private void initView() {

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeStr = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                afterStr = s.toString();
                if (afterStr.length() > beforeStr.length()) {//内容比输入之前有新增
                    String left = afterStr.substring(0, beforeStr.length()); //新输入内容的左侧部分
                    String right = afterStr.substring(beforeStr.length()); //新输入内容的右侧部分
                    if (left.equals(beforeStr) && right.startsWith("#")) { //左侧部分的内容与之前输入框中的内容一致(有可能存在全选复制粘贴的情况)，并且右侧部分第一一个输入的内容是#号
                        if (!tagList.isEmpty()) {
                            String last = tagList.get(tagList.size() - 1);
                            if (left.endsWith(last)) { //左侧部分的输入内容是最后一个话题
                                if (!left.endsWith(" ")) { //不是以空格作为结尾
                                    String newContent = left + " " + right;
                                    TopicEditText.this.setText(newContent);
                                    TopicEditText.this.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            TopicEditText.this.setSelection(newContent.length());
                                        }
                                    }, 50);
                                }
                            }
                        }

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable)) {
                    preTextLength = 0;
                    tagList.clear();
                    tagInfoList.clear();
                    return;
                }

                if (isChanged) return;

                String inputStr = editable.toString().replace("＃", PREFIX_TAG);

                int length = inputStr.length();
                Log.e("zzz", "\n====afterTextChanged()====length:" + length + ",,preTextLength：" + preTextLength);
                if (length > preTextLength) {
                    // 刚刚执行了增加操作
                    tagList = new ArrayList<>();//提取话题列表
                    if (TextUtils.isEmpty(tagRegex)) tagRegex = DEF_REGEX;
                    Pattern pattern = Pattern.compile(tagRegex);
                    Matcher matcher = pattern.matcher(inputStr);
                    SpannableStringBuilder builder = new SpannableStringBuilder(inputStr);
                    while (matcher.find()) {
                        int pos = matcher.start();
                        int end = matcher.end();
                        Log.e("zzz", "匹配开始位置：" + pos + "-->" + end);
                        String item = matcher.group(0);
                        /*if(end - pos == TOPIC_MAX_WORDS_LIMIT + 2 && item.endsWith(" ")){

                        }else if(end - pos > TOPIC_MAX_WORDS_LIMIT + 1){ //+1 是加上开头#号这个字符  超过这个长度 不作为话题显示
                            continue;
                        }*/
                        // 话题颜色高亮
                        SpannableString span = TopicSpan.getSpan(mForegroundColor, item, item, null);
                        builder = builder.delete(pos, pos + span.length());
                        builder.insert(pos, span);
                        tagList.add(item);
                    }

                    if (tagList.isEmpty()) {
                        selectTagStr = "";
                        return;
                    }

                    isChanged = true;
                    int preIndex = getSelectionStart();
                    Log.e("zzz", "设置高亮前：" + getSelectionStart() + "-->" + getSelectionEnd());
                    setText(builder);// 会导致光标回到首位
                    Log.e("zzz", "设置高亮后：" + getSelectionStart() + "-->" + getSelectionEnd());
                    isChanged = false;
                    setSelection(preIndex);//重新setText()后会导致光标回到首位，需要重新设置光标位置。
                } else if (preTextLength > length) {
//                    // 删除数据了，需要更新tagList
//                    tagList = new ArrayList<>();//提取话题列表
//                    if (TextUtils.isEmpty(tagRegex)) tagRegex = DEF_REGEX;
//                    Pattern pattern = Pattern.compile(tagRegex);
//                    Matcher matcher = pattern.matcher(inputStr);
//                    while (matcher.find()) {
//                        String item = matcher.group(0);
//                        tagList.add(item);
//                    }
                }

                preTextLength = length;
            }
        });

        // 替换输入的中文#
        setTransformationMethod(new ReplacementTransformationMethod() {
            @Override
            protected char[] getOriginal() {
                char[] ori = {'＃'};
                return ori;
            }

            @Override
            protected char[] getReplacement() {
                char[] rep = {'#'};
                return rep;
            }
        });

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                    if (tagList.isEmpty()) {
                        selectTagStr = "";
                        return false;
                    }

                    if (!TextUtils.isEmpty(selectTagStr) && !tagInfoList.isEmpty() && !getText().toString().contains(selectTagStr)) {
                        // 刚刚删除了选中的话题
                        Log.e("zzz", "刚刚删除了官方的话题：" + selectTagStr);
                        for (int i = 0; i < tagInfoList.size(); i++) {
                            if (selectTagStr.replace(PREFIX_TAG, "").trim().equals(tagInfoList.get(i).getContent())) {
                                tagInfoList.remove(i);
                            }
                        }
                    }

                    int selectionStart = getSelectionStart();
                    int selectionEnd = getSelectionEnd();
                    if (selectionStart < selectionEnd) {
                        Log.e("zzz", "删除已选中的部分");
                        return false;
                    }

                    int lastPos = 0;

                    // 遍历判断光标的位置
                    for (int i = 0; i < tagList.size(); i++) {
                        String topicTxt = tagList.get(i);
                        if (!topicTxt.endsWith(END_TAG)) {
                            // 正常删除
                            Log.e("zzz", "正常的删除操作");
                        } else {
                            lastPos = getText().toString().indexOf(topicTxt, lastPos);

                            if (lastPos != -1) {
                                if (selectionStart != 0 && selectionStart > lastPos && selectionStart <= (lastPos + topicTxt.length())) {
                                    Log.e("zzz", "选中了话题：" + topicTxt);
                                    // 选中话题
                                    setSelection(lastPos, lastPos + topicTxt.length());
                                    // 记录选中的话题
                                    selectTagStr = topicTxt;
                                    // 设置背景色
                                    getText().setSpan(new BackgroundColorSpan(mBackgroundColor), lastPos, lastPos + topicTxt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    return true;
                                }
                                lastPos += topicTxt.length();
                            }
                        }
                    }

                } else if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {//输入回车
                    inputEndTopicAddSpace();
                }
                return false;
            }
        });
    }

    /**
     * 监听光标的位置,若光标处于话题内容中间则移动光标到话题结束位置
     */
    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (selStart != selEnd) {
            Log.e("zzz", "选中了范围，无需移到光标");
            return;
        }
        if (tagList == null || tagList.size() == 0) {
            return;
        }
        Log.e("zzz", "======onSelectionChanged...话题数量：" + tagList.size());
        Log.e("zzz", "\n======onSelectionChanged====selStart=" + selStart + ",,selEnd=" + selEnd);

        int startPosition = 0;
        int endPosition;
        String topicTxt;

        int length = getText().toString().length();
        for (int i = 0; i < tagList.size(); i++) {
            topicTxt = tagList.get(i);

            if (!topicTxt.endsWith(END_TAG)) {
                //  结尾编辑中的话题，无需定位到结尾
                Log.e("zzz", "结尾编辑中的话题");
            } else {
                while (true) {
                    // 获取话题文本开始下标
                    startPosition = getText().toString().indexOf(topicTxt, startPosition);
                    endPosition = startPosition + topicTxt.length();

                    if (startPosition < 0) {
                        break;
                    }
                    // 若光标处于话题内容中间，则移动光标到话题结束位置
                    if (selStart > startPosition && selStart <= endPosition) {
                        Log.e("zzz", "设置光标位置，length=" + length + ",,startPosition=" + startPosition + ",,endPosition=" + endPosition);

                        setSelection(endPosition);
                        break;
                    }
                    startPosition = endPosition;
                }
            }
        }
    }

    /**
     * 获取话题的列表(包含自定义的话题)
     *
     * @return
     */
    public List<String> getTagList() {
        List list = new ArrayList();
        if (TextUtils.isEmpty(tagRegex)) tagRegex = DEF_REGEX;
        Editable editable = getText();
        Pattern pattern = Pattern.compile(tagRegex);
        Matcher matcher = pattern.matcher(editable);

        if (editable.toString().contains(PREFIX_TAG)) {
            //提取话题列表
            while (matcher.find()) {
                list.add(matcher.group(0).replace(PREFIX_TAG, "").replace(END_TAG, ""));
            }
        }
        return list;
    }

    /**
     * 获取所有话题列表  纯话题文字，去除开头#号 和结尾 空格
     * @return
     */
    public List<TagInfo> getAllTopicList(boolean filterTopicMaxWordsLimit){
        List<TagInfo> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("#(\\S+)\\s");
        Matcher matcher = pattern.matcher(getText().toString());
        while (matcher.find()) {
            if(filterTopicMaxWordsLimit){
                int pos = matcher.start();
                int end = matcher.end();
                if(end - pos > TOPIC_MAX_WORDS_LIMIT + 1){ //+1 是加上开头#号这个字符  超过这个长度 不作为话题显示
                    continue;
                }
            }
            TagInfo tagInfo = new TagInfo();
            tagInfo.setContent(matcher.group(0).replace(PREFIX_TAG, "").replace(END_TAG, ""));
            list.add(tagInfo);
        }
        return list;
    }

    /**
     * 输入的内容结尾如果是话题，添加空格 作为一个话题整体
     */
    public boolean inputEndTopicAddSpace(){
        if (!tagList.isEmpty()) {
            if (!getText().toString().endsWith(" ")) {
                String last = tagList.get(tagList.size() - 1);
                if (getText().toString().endsWith(last)) {
                    TopicEditText.this.setText(getText().toString() + " ");
                    TopicEditText.this.setSelection(getText().toString().length());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 输入的话题内容转换为上传给服务端的格式
     * "123#北京旅游 456#上海迪士尼 789"
     * @return
     */
    public String topicContentToServeFormat(String inputContent){
        if(TextUtils.isEmpty(inputContent)) return "";
        Pattern pattern = Pattern.compile("#(\\S+)\\s");
        Matcher matcher = pattern.matcher(inputContent);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            int pos = matcher.start();
            int end = matcher.end();
            if(end - pos > TOPIC_MAX_WORDS_LIMIT + 1){ //+1 是加上开头#号这个字符  超过这个长度 不作为话题显示
                continue;
            }
            matcher.appendReplacement(result, "#" + matcher.group(1) + "[HT]#");
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * 服务端返回的数据 转换成输入框显示的话题样式
     * @param content  "123#北京旅游[HT]#456#上海迪士尼[HT]#789"
     * @return
     */
    public String serveContentToTopicFormat(String content){
        if(TextUtils.isEmpty(content)) return "";
        Pattern pattern = Pattern.compile("#([^#]+?)\\[HT\\]#");
        Matcher matcher = pattern.matcher(content);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, "#" + matcher.group(1) + " ");
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
