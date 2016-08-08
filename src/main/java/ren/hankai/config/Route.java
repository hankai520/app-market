/*
 * Copyright © 2016 Jiangsu Sparknet Software Co., Ltd. All rights reserved.
 *
 * http://www.sparksoft.com.cn
 */

package ren.hankai.config;

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
    public static final String  API_APP_METADATA        = API_PREFIX + "/app/{sku}/metadata";
    public static final String  API_UPDATE_APP          = API_PREFIX + "/app/{sku}/update";
    public static final String  BACKGROUND_PREFIX       = "/admin";
    public static final String  BG_LOGIN                = BACKGROUND_PREFIX + "/login";
    public static final String  BG_LOGOUT               = BACKGROUND_PREFIX + "/logout";
    public static final String  BG_APPS                 = BACKGROUND_PREFIX + "/apps";
    public static final String  BG_AJAX_APPS            = BACKGROUND_PREFIX + "/apps.json";
    public static final String  BG_APP_ADD              = BACKGROUND_PREFIX + "/apps/add";
    public static final String  BG_APP_EDIT             = BACKGROUND_PREFIX + "/apps/{appId}/edit";
    public static final String  BG_APP_DELETE           =
                                              BACKGROUND_PREFIX + "/apps/{appId}/delete";
    public static final String  BG_USERS                = BACKGROUND_PREFIX + "/users";
    public static final String  BG_USERS_JSON           = BACKGROUND_PREFIX + "/users.json";
    public static final String  BG_ADD_USER             = BACKGROUND_PREFIX + "/users/add";
    public static final String  BG_EDIT_USER            =
                                             BACKGROUND_PREFIX + "/users/{user_id}/edit";
    public static final String  BG_CHANGE_USER_PWD      = BACKGROUND_PREFIX
        + "/users/{user_id}/change_pwd";
    public static final String  BG_DELETE_USER          =
                                               BACKGROUND_PREFIX + "/users/{user_id}/delete";
    public static final String  FOREGROUND_PREFIX       = "/market";
    public static final String  FG_APPS                 = FOREGROUND_PREFIX + "/apps";
    public static final String  FG_LOGIN                = FOREGROUND_PREFIX + "/login";
    public static final String  FG_LOGOUT               = FOREGROUND_PREFIX + "/logout";
}
