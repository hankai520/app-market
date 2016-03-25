/*
 * Copyright © 2015 Jiangsu Sparknet Software Co., Ltd. All rights reserved
 *
 * http://www.sparksoft.com.cn
 */

package cn.com.sparksoft.config;

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
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
public class ServerConfig implements EmbeddedServletContainerCustomizer {

    @Override
    public void customize( ConfigurableEmbeddedServletContainer container ) {
        container.addErrorPages( new ErrorPage( HttpStatus.BAD_REQUEST, "/400.html" ) );
        container.addErrorPages( new ErrorPage( HttpStatus.FORBIDDEN, "/403.html" ) );
        container.addErrorPages( new ErrorPage( HttpStatus.NOT_FOUND, "/404.html" ) );
        container.addErrorPages( new ErrorPage( HttpStatus.INTERNAL_SERVER_ERROR, "/500.html" ) );
    }
}
