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
            .createQuery( "select o from App o where o.status=:status", App.class );
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
    public boolean saveAppPackage( Integer appId, AppPlatform platform,
                    MultipartFile packageFile ) {
        try {
            String path = getAppPackagePath( appId, platform );
            FileOutputStream fos = new FileOutputStream( path );
            FileCopyUtils.copy( packageFile.getInputStream(), fos );
            return true;
        } catch (IOException e) {
            logger.error(
                String.format( "Failed to save app package for app with id \"%d\"", appId ), e );
            return false;
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
}
