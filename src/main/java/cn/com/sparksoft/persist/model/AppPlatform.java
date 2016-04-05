/*
 * Copyright © 2015 Jiangsu Sparknet Software Co., Ltd. All Rights Reserved
 *
 *    http://www.sparksoft.com.cn
 */

package cn.com.sparksoft.persist.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 应用平台
 *
 * @author hankai
 * @version 1.0
 * @since Jul 16, 2015 2:25:52 PM
 */
public enum AppPlatform {
    /**
     * iOS
     */
    iOS( 0 ),
    /**
     * Android
     */
    Android( 1 ),;

    @JsonCreator
    public static AppPlatform fromInteger( Integer value ) {
        if ( value == iOS.value ) {
            return iOS;
        } else if ( value == Android.value ) {
            return Android;
        }
        return null;
    }

    private final int value;

    private AppPlatform( int value ) {
        this.value = value;
    }

    /**
     * 获取用于国际化的键名
     */
    public String i18nKey() {
        return String.format( "app.platform.%s", name() );
    }

    @JsonValue
    public int value() {
        return value;
    }
}
