/*
 * Copyright © 2015 Jiangsu Sparknet Software Co., Ltd. All Rights Reserved
 *
 *    http://www.sparksoft.com.cn
 */

package cn.com.sparksoft.persist;

import org.springframework.stereotype.Service;

import java.util.List;

import javax.persistence.TypedQuery;

import cn.com.sparksoft.persist.model.App;
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
}
