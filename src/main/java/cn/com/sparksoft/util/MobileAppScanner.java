/*
 * Copyright © 2016 Jiangsu Sparknet Software Co., Ltd. All rights reserved.
 *
 * http://www.sparksoft.com.cn
 */

package cn.com.sparksoft.util;

import org.springframework.stereotype.Component;

/**
 * 扫描移动应用包元数据
 *
 * @author hankai
 * @version 1.0
 * @since May 16, 2016 11:44:24 AM
 */
@Component
public class MobileAppScanner {

    class MobileAppInfo {

        private String version; // 版本号

        public String getVersion() {
            return version;
        }

        public void setVersion( String version ) {
            this.version = version;
        }
    }

    /**
     * 扫描 Android .apk 文件元数据
     *
     * @param filePath apk 文件路径
     * @return 文件元数据
     * @author hankai
     * @since May 20, 2016 3:46:18 PM
     */
    public MobileAppInfo scanAndroidApk( String filePath ) {
        return null;
    }

    /**
     * 扫描 iOS .ipa 文件元数据
     *
     * @param filePath ipa 文件路径
     * @return 文件元数据
     * @author hankai
     * @since May 20, 2016 3:46:18 PM
     */
    public MobileAppInfo scanIosIpa( String filePath ) {
        return null;
    }
}
