/*
 * Copyright © 2015 Jiangsu Sparknet Software Co., Ltd. All rights reserved
 *
 * http://www.sparksoft.com.cn
 */

package cn.com.sparksoft.config;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;
import org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;

import java.util.Properties;

import javax.sql.DataSource;

import cn.com.sparksoft.Preferences;
import cn.com.sparksoft.persist.util.Slf4jSessionLogger;

/**
 * 用于测试的 Mysql 数据库配置基类
 *
 * @author hankai
 * @version 1.0
 * @since Jul 14, 2015 12:38:04 PM
 */
@Profile( Preferences.PROFILE_TEST )
@Configuration
public class MysqlDataBaseConfig {

    protected String     driverClassName;
    protected String     databaseUrl;
    protected String     userName;
    protected String     password;
    protected Properties jpaProperties = new Properties();

    /**
     * Configure the properties that will be used in JPA implementation.
     * By using this method, persistence.xml is no longer need.
     * This method will configure the properties for eclipselink by default, subclasses should
     * override this method to fit other JPA implementations.
     */
    protected void configureJpaProperties() {
        jpaProperties.setProperty( "eclipselink.target-database",
            "org.eclipse.persistence.platform.database.MySQLPlatform" );
        jpaProperties.setProperty( "eclipselink.weaving", "static" );
        jpaProperties.setProperty( "eclipselink.logging.level", "FINE" );
        jpaProperties.setProperty( "eclipselink.logging.parameters", "true" );
        jpaProperties
            .setProperty( "eclipselink.logging.logger", Slf4jSessionLogger.class.getName() );
    }

    /**
     * Construct basic data source object base on Apache DBCP.
     *
     * @param driver the qualified driver class name
     * @param url the database connection string
     * @param user the database user
     * @param pwd the database user password
     * @return an DBCP instance which implements DataSource interface
     * @see javax.sql.DataSource
     * @see org.apache.commons.dbcp.BasicDataSource
     */
    @Bean(
        destroyMethod = "close" )
    protected DataSource getDataSource() {
        PoolProperties pp = new PoolProperties();
        pp.setUrl( "jdbc:mysql://localhost:3306/game-dist-test?characterEncoding=utf8" );
        pp.setDriverClassName( "com.mysql.jdbc.Driver" );
        pp.setUsername( "root" );
        pp.setPassword( "CDROMdvd1" );
        pp.setJmxEnabled( true );
        pp.setTestWhileIdle( false );
        pp.setTestOnBorrow( true );
        pp.setValidationQuery( "SELECT 1" );
        pp.setTestOnReturn( false );
        pp.setValidationInterval( 30000 );
        pp.setTimeBetweenEvictionRunsMillis( 30000 );
        pp.setMaxActive( 100 );
        pp.setInitialSize( 10 );
        pp.setMaxWait( 10000 );
        pp.setRemoveAbandonedTimeout( 60 );
        pp.setMinEvictableIdleTimeMillis( 30000 );
        pp.setMinIdle( 10 );
        pp.setLogAbandoned( true );
        pp.setRemoveAbandoned( true );
        pp.setJdbcInterceptors( ConnectionState.class.getName() + ";"
            + StatementFinalizer.class.getName() );
        org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
        ds.setPoolProperties( pp );
        return ds;
    }

    /**
     * Construct entity manager factory which will be used to generate EntityManager for database
     * operations.
     *
     * @param persistenceUnitName the JPA persistence unit name, must not be null
     * @param entityBasePackage where should JPA scan the entities.
     * @return a bean used to generate EntityManagerFactory
     * @see javax.persistence.EntityManagerFactory
     * @see org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean getEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factory =
                                                         new LocalContainerEntityManagerFactoryBean();
        factory.setPersistenceUnitName( "defaultTestUnit" );
        factory.setDataSource( getDataSource() );
        EclipseLinkJpaVendorAdapter adapter = new EclipseLinkJpaVendorAdapter();
        adapter.setDatabasePlatform( "org.eclipse.persistence.platform.database.MySQLPlatform" );
        adapter.setShowSql( true );
        factory.setJpaVendorAdapter( adapter );
        factory.setPackagesToScan( "cn.com.sparksoft" );
        configureJpaProperties();
        factory.setJpaProperties( jpaProperties );
        return factory;
    }
}
