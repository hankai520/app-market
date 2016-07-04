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

    private static final String API_PREFIX              = "/api";
    public static final String  API_IOS_MANIFEST        =
                                                 API_PREFIX + "/app/{appId}/ios_manifest.plist";
    public static final String  API_APP_ICON            = API_PREFIX + "/app/{appId}/icon";
    public static final String  API_APP_IOS_PACKAGE     = API_PREFIX + "/app/{appId}/package.ipa";
    public static final String  API_APP_ANDROID_PACKAGE = API_PREFIX + "/app/{appId}/package.apk";
    public static final String  API_APP_METADATA        = API_PREFIX + "/app/{appId}/metadata";
    public static final String  BACKGROUND_PREFIX       = "/admin";
    public static final String  BG_LOGIN                = BACKGROUND_PREFIX + "/login";
    public static final String  BG_APPS                 = BACKGROUND_PREFIX + "/apps";
    public static final String  BG_AJAX_APPS            = BACKGROUND_PREFIX + "/apps.json";
    public static final String  BG_APP_ADD              = BACKGROUND_PREFIX + "/apps/add";
    public static final String  BG_APP_EDIT             = BACKGROUND_PREFIX + "/apps/{appId}/edit";
    public static final String  BG_APP_DELETE           =
                                              BACKGROUND_PREFIX + "/apps/{appId}/delete";
    public static final String  FG_APPS                 = "/apps";
}
