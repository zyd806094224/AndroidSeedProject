package com.demo.common.constant

/**
 * @Description: ARouter路由路径定义
 * @Date: 2024/8/30 11:27
 * @author:  zhaoyudong
 * @version: 1.0
 */

// 应用URL Scheme
const val APP_SCHEME = "seedapp"

//**********************Activity路由**********************
/**
 * 首页模块
 */
//首页
const val MAIN_ACTIVITY_HOME = "/main/activity/home"

// WebView页面
const val WEB_ACTIVITY = "/web/activity"

// 文本编辑页面
const val EDIT_ACTIVITY = "/edit/activity"

// 启动页
const val SPLASH_ACTIVITY = "/splash/activity"

// 测试页面
const val TEST_ACTIVITY = "/test/activity"

// IM 聊天模块
const val IM_LOGIN_ACTIVITY = "/im/login/activity"
const val IM_CONVERSATION_ACTIVITY = "/im/conversation/activity"
const val IM_CHAT_ACTIVITY = "/im/chat/activity"
const val IM_USER_LIST_ACTIVITY = "/im/userlist/activity"

//**********************Fragment路由**********************
/**
 * Fragment路由
 */
const val HOME_FRAGMENT = "/main/fragment/home"
const val MINE_FRAGMENT = "/main/fragment/mine"

//**********************服务相关**********************
/**
 * 主页模块-主页服务
 */
const val MAIN_SERVICE_HOME = "/main/service/home"

/**
 * 路由服务
 */
const val ROUTER_SERVICE = "/router/service"

//**********************路由参数常量**********************
/**
 * 通用参数
 */
const val PARAM_URL = "url"
const val PARAM_TITLE = "title"
const val PARAM_TAB_INDEX = "tab"
const val PARAM_CONTENT = "content"
const val PARAM_HINT = "hint"
const val PARAM_SHOW_SHARE = "showShare"
const val PARAM_USER_ID = "userId"
const val PARAM_ANIMATION = "animation"
const val PARAM_CONVERSATION_ID = "conversationId"
const val PARAM_TARGET_NAME = "targetName"