/*
 * Copyright © 2015 Jiangsu Sparknet Software Co., Ltd. All Rights Reserved
 *
 *    http://www.sparksoft.com.cn
 */

package cn.com.sparksoft.persist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.persistence.TypedQuery;

import cn.com.sparksoft.Preferences;
import cn.com.sparksoft.persist.model.App;
import cn.com.sparksoft.persist.model.AppPlatform;
import cn.com.sparksoft.persist.model.AppStatus;

/**
 * 应用信息业务
 *
 * @author hankai
 * @version 1.0
 * @since Jul 17, 2015 9:21:02 AM
 */
@Service
public class AppService extends JpaBasedService<App> {

    private static final Logger logger = LoggerFactory.getLogger( AppService.class );

    public AppService() {
        super( App.class );
    }

    /**
     * 查询可用应用
     *
     * @return 应用列表
     * @author hankai
     * @since Apr 5, 2016 5:28:23 PM
     */
    public List<App> getAvailableApps() {
        TypedQuery<App> q = jpaServiceUtil.getEntityManager()
            .createQuery( "select o from App o where o.status=:status order by o.id desc",
                App.class );
        q.setParameter( "status", AppStatus.ReadyToSale );
        return q.getResultList();
    }

    /**
     * 保存 app 程序包，保存的文件名命名规则为：app_{appId}_{platform}.{ipa | apk}
     *
     * @param appId 应用ID
     * @param platform 应用运行平台
     * @param packageFile 程序包文件
     * @return 是否保存成功
     * @author hankai
     * @since Apr 6, 2016 2:32:12 PM
     */
    public void saveAppPackage( Integer appId, AppPlatform platform,
                    MultipartFile packageFile ) {
        try {
            String path = getAppPackagePath( appId, platform );
            FileOutputStream fos = new FileOutputStream( path );
            FileCopyUtils.copy( packageFile.getInputStream(), fos );
        } catch (IOException e) {
            logger.error(
                String.format( "Failed to save app package for app with id \"%d\"", appId ), e );
        }
    }

    /**
     * 获取应用包保存路径
     *
     * @param appId 应用ID
     * @param platform 应用运行平台
     * @return 包路径
     * @author hankai
     * @since Apr 6, 2016 2:46:48 PM
     */
    public String getAppPackagePath( Integer appId, AppPlatform platform ) {
        String suffix = ( platform == AppPlatform.iOS ) ? "ipa" : "apk";
        String fileName = String.format( "app_%d_%d.%s", appId, platform.value(), suffix );
        String path = String.format( "%s/%s", Preferences.getAttachmentDir(), fileName );
        return path;
    }

    /**
     * 保存应用图标
     *
     * @param appId 应用ID
     * @param platform 运行平台
     * @param iconFile 图标
     * @return 是否成功
     * @author hankai
     * @since Apr 7, 2016 5:54:39 PM
     */
    public void saveAppIcon( Integer appId, AppPlatform platform,
                    MultipartFile iconFile ) {
        try {
            String path = getAppIconPath( appId, platform );
            FileOutputStream fos = new FileOutputStream( path );
            FileCopyUtils.copy( iconFile.getInputStream(), fos );
        } catch (IOException e) {
            logger.error(
                String.format( "Failed to save app icon for app with id \"%d\"", appId ), e );
        }
    }

    /**
     * 获取应用图标文件路径
     *
     * @param appId 应用ID
     * @param platform 运行平台
     * @return 文件路径
     * @author hankai
     * @since Apr 7, 2016 5:54:08 PM
     */
    public String getAppIconPath( Integer appId, AppPlatform platform ) {
        String fileName = String.format( "app_%d_%d.icon", appId, platform.value() );
        String path = String.format( "%s/%s", Preferences.getAttachmentDir(), fileName );
        return path;
    }

    /**
     * 删除应用相关的磁盘文件（程序包，图标）
     *
     * @param appId 应用ID
     * @param platform 运行平台
     * @author hankai
     * @since Apr 7, 2016 6:05:01 PM
     */
    public void deleteRelatedFiles( Integer appId, AppPlatform platform ) {
        String path = getAppIconPath( appId, platform );
        File f = new File( path );
        if ( f.exists() ) {
            f.delete();
        }
        path = getAppPackagePath( appId, platform );
        f = new File( path );
        if ( f.exists() ) {
            f.delete();
        }
    }
}
