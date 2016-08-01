/*
 * Copyright © 2015 Jiangsu Sparknet Software Co., Ltd. All Rights Reserved
 *
 *    http://www.sparksoft.com.cn
 */

package ren.hankai.persist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import ren.hankai.Preferences;
import ren.hankai.persist.model.App;
import ren.hankai.persist.model.AppPlatform;
import ren.hankai.persist.model.AppStatus;
import ren.hankai.persist.util.CriteriaQueryBuilder;
import ren.hankai.persist.util.JPABasedDAO;
import ren.hankai.persist.util.PaginatedResult;
import ren.hankai.util.MobileAppInfo;
import ren.hankai.util.MobileAppScanner;

/**
 * 应用信息业务
 *
 * @author hankai
 * @version 1.0
 * @since Jul 17, 2015 9:21:02 AM
 */
@Service
public class AppService extends JPABasedDAO<App> {

    private static final Logger logger = LoggerFactory.getLogger( AppService.class );
    @Autowired
    private MobileAppScanner    appScanner;

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
     * 查找所有APP
     *
     * @param keyword 关键字
     * @param sort 排序字段
     * @param asc 升序/降序
     * @param offset 结果集位置
     * @param limit 记录数
     * @return APP列表
     * @author hankai
     * @since Jul 4, 2016 1:41:17 PM
     */
    public PaginatedResult<App> getAllApps( final String keyword, final String sort,
                    final Boolean asc, int offset, int limit ) {
        return jpaServiceUtil.findAll( App.class, new CriteriaQueryBuilder() {

            @Override
            public List<Order> getOrderBys( CriteriaBuilder cb, Root<?> root ) {
                List<Order> orders = new ArrayList<>();
                if ( !StringUtils.isEmpty( sort ) && ( asc != null ) ) {
                    if ( asc ) {
                        orders.add( cb.asc( root.get( sort ) ) );
                    } else {
                        orders.add( cb.desc( root.get( sort ) ) );
                    }
                }
                orders.add( cb.asc( root.get( "id" ) ) );
                return orders;
            }

            @Override
            public Predicate buildPredicate( CriteriaBuilder cb, Root<?> root ) {
                String fuzzyKeyword = "%" + keyword + "%";
                Predicate pre = null;
                if ( !StringUtils.isEmpty( keyword ) ) {
                    pre = cb.or(
                        cb.like( root.<String>get( "name" ), fuzzyKeyword ),
                        cb.like( root.<String>get( "bundleIdentifier" ), fuzzyKeyword ) );
                }
                return pre;
            }
        }, offset, limit );
    }

    /**
     * 保存 APP 安装包并解析APP安装包信息
     *
     * @param appId 应用ID
     * @param platform 应用运行平台
     * @param packageFile 程序包文件
     * @return APP元数据（返回空表示保存失败）
     * @author hankai
     * @since Apr 6, 2016 2:32:12 PM
     */
    public MobileAppInfo saveAppPackage( AppPlatform platform, MultipartFile packageFile ) {
        MobileAppInfo mai = null;
        try {
            String path = getRandomAppPackageCachePath();
            FileOutputStream fos = new FileOutputStream( path );
            FileCopyUtils.copy( packageFile.getInputStream(), fos );
            if ( platform == AppPlatform.Android ) {
                mai = appScanner.scanAndroidApk( path );
            } else if ( platform == AppPlatform.iOS ) {
                mai = appScanner.scanIosIpa( path );
            }
            if ( mai != null ) {
                mai.setBundlePath( path );
            }
        } catch (IOException e) {
            logger.error( "Failed to save app package.", e );
        }
        return mai;
    }

    /**
     * 获取应用包随机缓存路径
     *
     * @param appId 应用ID
     * @param platform 应用运行平台
     * @return 包路径
     * @author hankai
     * @since Apr 6, 2016 2:46:48 PM
     */
    public String getRandomAppPackageCachePath() {
        String fileName = System.currentTimeMillis() + new Random().nextInt( 9999 ) + 1000 + "";
        String path = String.format( "%s/%s", Preferences.getAttachmentDir(), fileName );
        return path;
    }
}
