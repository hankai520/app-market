/*
 * Copyright © 2015 Jiangsu Sparknet Software Co., Ltd. All rights reserved
 *
 * http://www.sparksoft.com.cn
 */

package cn.com.sparksoft.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;

import java.util.Properties;

import javax.sql.DataSource;

import cn.com.sparksoft.Preferences;
import cn.com.sparksoft.persist.util.Slf4jSessionLogger;

/**
 * 数据库配置基类
 *
 * @author hankai
 * @version 1.0
 * @since Jul 14, 2015 12:38:04 PM
 */
@Configuration
public class DatabaseConfig {

    // private static final Logger logger = LoggerFactory.getLogger( DatabaseConfig.class );
    protected Properties jpaProperties = new Properties();

    /**
     * Configure the properties that will be used in JPA implementation.
     * By using this method, persistence.xml is no longer need.
     * This method will configure the properties for eclipselink by default, subclasses should
     * override this method to fit other JPA implementations.
     */
    protected void configureJpaProperties() {
        jpaProperties.setProperty( "eclipselink.target-database",
            "org.eclipse.persistence.platform.database.H2Platform" );
        // jpaProperties.setProperty( "eclipselink.ddl-generation", "drop-and-create-tables" );
        // this controls what will be logged during DDL execution
        // jpaProperties.setProperty("eclipselink.ddl-generation.output-mode", "both");
        jpaProperties.setProperty( "eclipselink.weaving", "static" );
        jpaProperties.setProperty( "eclipselink.logging.level", "FINE" );
        jpaProperties.setProperty( "eclipselink.logging.parameters", "true" );
        jpaProperties
            .setProperty( "eclipselink.logging.logger", Slf4jSessionLogger.class.getName() );
    }

    @Bean
    protected DataSource getDataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName( "org.h2.Driver" );
        // jdbc:h2:mem:app-db;DB_CLOSE_DELAY=-1
        // jdbc:h2:file:./h2/app-db
        String dbPath = Preferences.getDataDir() + "/database";
        ds.setUrl( "jdbc:h2:file:" + dbPath + ";DB_CLOSE_DELAY=-1" );
        ds.setUsername( "sa" );
        ds.setPassword( null );
        return ds;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean getEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory =
                                                         new LocalContainerEntityManagerFactoryBean();
        factory.setPersistenceUnitName( "defaultTestUnit" );
        factory.setDataSource( getDataSource() );
        EclipseLinkJpaVendorAdapter adapter = new EclipseLinkJpaVendorAdapter();
        adapter.setDatabasePlatform( "org.eclipse.persistence.platform.database.H2Platform" );
        adapter.setShowSql( true );
        factory.setJpaVendorAdapter( adapter );
        factory.setPackagesToScan( "cn.com.sparksoft" );
        configureJpaProperties();
        factory.setJpaProperties( jpaProperties );
        return factory;
    }
}
