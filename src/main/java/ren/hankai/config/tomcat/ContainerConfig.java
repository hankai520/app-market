/*
 * Copyright © 2015 Jiangsu Sparknet Software Co., Ltd. All rights reserved
 *
 * http://www.sparksoft.com.cn
 */

package ren.hankai.config.tomcat;

import org.apache.catalina.valves.RemoteIpValve;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * 自定义servlet容器配置。
 *
 * @author hankai
 * @version 1.0.0
 * @since Jul 14, 2015 10:08:48 AM
 */
@Component
public class ContainerConfig implements EmbeddedServletContainerCustomizer {

    @Autowired
    private ConnectorConfig connectorConfig;

    @Override
    public void customize( ConfigurableEmbeddedServletContainer container ) {
        container.addErrorPages( new ErrorPage( HttpStatus.BAD_REQUEST, "/400.html" ) );
        container.addErrorPages( new ErrorPage( HttpStatus.FORBIDDEN, "/403.html" ) );
        container.addErrorPages( new ErrorPage( HttpStatus.NOT_FOUND, "/404.html" ) );
        container.addErrorPages( new ErrorPage( HttpStatus.INTERNAL_SERVER_ERROR, "/500.html" ) );
        if ( container instanceof TomcatEmbeddedServletContainerFactory ) {
            TomcatEmbeddedServletContainerFactory cf =
                                                     (TomcatEmbeddedServletContainerFactory) container;
            cf.addConnectorCustomizers( connectorConfig );
            RemoteIpValve riv = new RemoteIpValve();
            riv.setRemoteIpHeader( "X-Forwarded-For" );
            riv.setProxiesHeader( "X-Forwarded-By" );
            riv.setProtocolHeader( "X-Forwarded-Proto" );
            cf.addContextValves( riv );
        }
    }
}
