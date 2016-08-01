/*
 * Copyright © 2016 Jiangsu Sparknet Software Co., Ltd. All rights reserved.
 *
 * http://www.sparksoft.com.cn
 */

package ren.hankai.util;

/**
 * APP 元数据
 *
 * @author hankai
 * @version 1.0
 * @since Aug 1, 2016 2:21:55 PM
 */
public class MobileAppInfo {

    private String version;   // 版本号
    private String bundleId;  // 包ID
    private byte[] icon;      // 应用图标
    private String bundlePath;// 应用包缓存位置

    public String getVersion() {
        return version;
    }

    public void setVersion( String version ) {
        this.version = version;
    }

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId( String bundleId ) {
        this.bundleId = bundleId;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon( byte[] icon ) {
        this.icon = icon;
    }

    public String getBundlePath() {
        return bundlePath;
    }

    public void setBundlePath( String bundlePath ) {
        this.bundlePath = bundlePath;
    }
}
