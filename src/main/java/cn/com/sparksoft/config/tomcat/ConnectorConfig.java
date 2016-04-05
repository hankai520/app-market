/*
 * Copyright © 2016 Jiangsu Sparknet Software Co., Ltd. All rights reserved.
 *
 * http://www.sparksoft.com.cn
 */

package cn.com.sparksoft.config.tomcat;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Tomcat Connector 配置
 *
 * @author hankai
 * @version 1.0
 * @since Apr 5, 2016 4:11:36 PM
 */
@Component
public class ConnectorConfig implements TomcatConnectorCustomizer {

    @Value( "${proxy.name}" )
    private String  proxyName;
    @Value( "${proxy.port}" )
    private Integer proxyPort;

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer#customize(org.
     * apache.catalina.connector.Connector)
     */
    @Override
    public void customize( Connector connector ) {
        if ( !StringUtils.isEmpty( proxyName ) && ( proxyPort != null ) && ( proxyPort > 0 ) ) {
            connector.setProxyName( proxyName );
            connector.setProxyPort( proxyPort );
            connector.setRedirectPort( proxyPort );
        }
    }
}
