/*
 * Copyright © 2016 Jiangsu Sparknet Software Co., Ltd. All rights reserved.
 *
 * http://www.sparksoft.com.cn
 */

package cn.com.sparksoft.config;

/**
 * 系统路由
 *
 * @author hankai
 * @version 1.0
 * @since Jan 7, 2016 3:19:31 PM
 */
public final class Route {

    /* ######################## 网站接口 URL 定义 （开始） ############################ */
    /**
     * Web service URL前缀
     */
    private static final String API_PREFIX        = "/api";
    /**
     * ios manifest 文件生成
     */
    public static final String  API_IOS_MANIFEST  = API_PREFIX + "/app/ios_manifest";
    /**
     * app 程序包下载
     */
    public static final String  API_APP_PACKAGE   = API_PREFIX + "/app/{appId}/package";
    /* ######################## 网站接口 URL 定义 （结束） ############################ */
    /* ######################## 网站后台 URL 定义（开始） ############################ */
    /**
     * 网站后台URL前缀
     */
    public static final String  BACKGROUND_PREFIX = "/admin";
    /**
     * 后台登录
     */
    public static final String  BG_LOGIN          = BACKGROUND_PREFIX + "/login";
    /**
     * 后台应用列表
     */
    public static final String  BG_APPS           = BACKGROUND_PREFIX + "/apps";
    /**
     * 后台 AJAX 获取应用列表
     */
    public static final String  BG_AJAX_APPS      = BACKGROUND_PREFIX + "/apps.json";
    /**
     * 后台添加应用
     */
    public static final String  BG_APP_ADD        = BACKGROUND_PREFIX + "/apps/add";
    /**
     * 后台编辑应用
     */
    public static final String  BG_APP_EDIT       = BACKGROUND_PREFIX + "/apps/{appId}/edit";
    /**
     * 后台删除应用
     */
    public static final String  BG_APP_DELETE     = BACKGROUND_PREFIX + "/apps/{appId}/delete";
    /* ######################## 网站后台 URL 定义（结束） ############################ */
    /* ######################## 网站前台 URL 定义 ############################ */
    /**
     * 网站前台应用列表
     */
    public static final String  FG_APPS           = "/apps";
    /* ######################## 网站前台 URL 定义 ############################ */
}
