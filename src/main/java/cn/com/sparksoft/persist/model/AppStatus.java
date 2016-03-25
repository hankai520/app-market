/*
 * Copyright © 2015 Jiangsu Sparknet Software Co., Ltd. All Rights Reserved
 *
 *    http://www.sparksoft.com.cn
 */

package cn.com.sparksoft.persist.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 应用状态
 *
 * @author hankai
 * @version 1.0
 * @since Jul 16, 2015 2:25:52 PM
 */
public enum AppStatus {
    /**
     * 开发中
     */
    Developing( 0 ),
    /**
     * 已上架
     */
    ReadyToSale( 1 ),
    /**
     * 已下架
     */
    Removed( 2 );

    @JsonCreator
    public static AppStatus fromInteger( Integer value ) {
        if ( value == Developing.value ) {
            return Developing;
        } else if ( value == ReadyToSale.value ) {
            return ReadyToSale;
        } else if ( value == Removed.value ) {
            return Removed;
        }
        return null;
    }

    private final int value;

    private AppStatus( int value ) {
        this.value = value;
    }

    /**
     * 获取用于国际化的键名
     */
    public String i18nKey() {
        return String.format( "app.status.%d", value );
    }

    @JsonValue
    public int value() {
        return value;
    }
}
