/*
 * Copyright © 2015 Jiangsu Sparknet Software Co., Ltd, All rights reserved.
 *
 * http://www.sparksoft.com.cn
 */

package ren.hankai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableSpringConfigured
@EnableAspectJAutoProxy
@EnableTransactionManagement
@EnableConfigurationProperties
public class Application extends SpringBootServletInitializer {

    private static final Logger logger = LoggerFactory.getLogger( Application.class );

    public static void main( String[] args ) {
        logger.info( System.getenv().toString() );
        if ( ApplicationInitializer.initialize() ) {
            SpringApplication.run( Application.class, args );
        }
    }

    @Override
    protected SpringApplicationBuilder configure( SpringApplicationBuilder application ) {
        if ( ApplicationInitializer.initialize() ) {
            return application.sources( Application.class );
        }
        return null;
    }
}
