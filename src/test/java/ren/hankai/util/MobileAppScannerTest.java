/*
 * Copyright © 2016 Jiangsu Sparknet Software Co., Ltd. All rights reserved.
 *
 * http://www.sparksoft.com.cn
 */

package ren.hankai.util;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;

import ren.hankai.ApplicationTests;
import ren.hankai.appmarket.util.MobileAppInfo;

/**
 * 移动应用扫描器
 *
 * @author hankai
 * @version 1.0
 * @since May 20, 2016 3:48:20 PM
 */
public class MobileAppScannerTest extends ApplicationTests {

    /**
     * Test method for
     * {@link ren.hankai.appmarket.util.MobileAppScanner#scanAndroidApk(java.lang.String)}.
     */
    @Test
    public void testScanAndroidApk() throws Exception {
        File f = ResourceUtils.getFile( "classpath:test.apk" );
        MobileAppInfo mai = appScanner.scanAndroidApk( f.getAbsolutePath() );
        Assert.assertEquals( "cn.com.sparksoft.zjsc", mai.getBundleId() );
        Assert.assertEquals( "1.0#1", mai.getVersion() );
        Assert.assertNotNull( mai.getIcon() );
    }

    /**
     * Test method for {@link ren.hankai.appmarket.util.MobileAppScanner#scanIosIpa(java.lang.String)}.
     */
    @Test
    public void testScanIosIpa() throws Exception {
        File f = ResourceUtils.getFile( "classpath:test.ipa" );
        MobileAppInfo mai = appScanner.scanIosIpa( f.getAbsolutePath() );
        Assert.assertEquals( "cn.com.sparksoft.zjsc", mai.getBundleId() );
        Assert.assertEquals( "1.0#2", mai.getVersion() );
        Assert.assertNotNull( mai.getIcon() );
    }
}
