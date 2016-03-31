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
    /* ######################## 网站接口 URL 定义 （结束） ############################ */
    /* ######################## 网站后台 URL 定义（开始） ############################ */
    /**
     * 网站后台URL前缀
     */
    private static final String BACKGROUND_PREFIX = "/admin";
    /**
     * 后台登录
     */
    public static final String  BG_LOGIN          = BACKGROUND_PREFIX + "/login";
    /* ######################## 网站后台 URL 定义（结束） ############################ */
    /* ######################## 网站前台 URL 定义 ############################ */
}
