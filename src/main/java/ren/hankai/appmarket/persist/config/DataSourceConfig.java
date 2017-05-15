/*
 * Copyright © 2016 hankai.ren, All rights reserved.
 *
 * http://www.hankai.ren
 */

package ren.hankai.appmarket.persist.config;

import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;
import org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;
import org.eclipse.persistence.platform.database.HSQLPlatform;
import org.eclipse.persistence.platform.database.MySQLPlatform;
import org.eclipse.persistence.platform.database.OraclePlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ren.hankai.cordwood.core.Preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

/**
 * 数据源配置类。
 *
 * @author hankai
 * @version 1.0
 * @since Aug 18, 2016 2:19:51 PM
 */
@Configuration
public class DataSourceConfig {

  private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);
  private static final String[] basePackages = {"ren.hankai.cordwood"};

  /**
   * 从外部配置文件加载数据库连接配置。如果数据库配置子类需要在程序启动时，从程序包外部获取配置文件， 则调用此方法获取外部文件配置参数。
   *
   * @param fileName 外部配置文件名
   * @return 加载到的配置内容
   * @author hankai
   * @since Jun 21, 2016 10:45:48 AM
   */
  public static Properties loadExternalConfig(String fileName) {
    Properties props = null;
    try {
      props = new Properties();
      props.load(new FileInputStream(Preferences.getDbConfigFile(fileName)));
    } catch (final IOException ex) {
      logger.warn("Failed to load external database configuration file for production profile.",
          ex);
    }
    if ((props != null) && (props.size() > 0)) {
      return props;
    }
    return null;
  }

  @Profile(Preferences.PROFILE_HSQL)
  @Configuration
  public static class HsqlConfig {

    /**
     * 数据源。
     *
     * @return 数据源
     * @author hankai
     * @since Oct 25, 2016 11:35:04 AM
     */
    @Bean
    public DataSource dataSource() {
      final Properties props = loadExternalConfig("hsql.properties");
      final DriverManagerDataSource ds = new DriverManagerDataSource();
      ds.setDriverClassName(props.getProperty("driverClassName"));
      final String dbPath = Preferences.getDbDir() + File.separator + "application";
      ds.setUrl(String.format(props.getProperty("url"), dbPath));
      ds.setUsername(props.getProperty("username"));
      ds.setPassword(props.getProperty("password"));
      return ds;
    }

    /**
     * 数据源配置信息。
     *
     * @return 数据源配置信息
     * @author hankai
     * @since Oct 25, 2016 11:35:17 AM
     */
    @Bean
    public DataSourceInfo dataSourceInfo() {
      return new DataSourceInfo(HSQLPlatform.class.getName(), basePackages);
    }
  }

  @Configuration
  @Profile(Preferences.PROFILE_MYSQL)
  public static class MySqlConfig {

    /**
     * MySQL 数据源。
     *
     * @return 数据源
     * @author hankai
     * @since Oct 25, 2016 11:35:34 AM
     */
    @Bean
    public DataSource dataSource() {
      final Properties props = loadExternalConfig("mysql.properties");
      final PoolProperties pp = new PoolProperties();
      pp.setUrl(props.getProperty("url"));
      pp.setDriverClassName(props.getProperty("driverClassName"));
      pp.setUsername(props.getProperty("username"));
      pp.setPassword(props.getProperty("password"));
      pp.setJmxEnabled(true);
      pp.setTestWhileIdle(false);
      pp.setTestOnBorrow(true);
      pp.setValidationQuery("select 1");
      pp.setTestOnReturn(false);
      pp.setValidationInterval(30000);
      pp.setTimeBetweenEvictionRunsMillis(30000);
      pp.setMaxActive(100);
      pp.setInitialSize(10);
      pp.setMaxWait(10000);
      pp.setRemoveAbandonedTimeout(60);
      pp.setMinEvictableIdleTimeMillis(30000);
      pp.setMinIdle(10);
      pp.setLogAbandoned(true);
      pp.setRemoveAbandoned(true);
      pp.setJdbcInterceptors(
          ConnectionState.class.getName() + ";" + StatementFinalizer.class.getName());
      final org.apache.tomcat.jdbc.pool.DataSource ds =
          new org.apache.tomcat.jdbc.pool.DataSource();
      ds.setPoolProperties(pp);
      return ds;
    }

    @Bean
    public DataSourceInfo dataSourceInfo() {
      return new DataSourceInfo(MySQLPlatform.class.getName(), basePackages);
    }
  }

  @Configuration
  @Profile(Preferences.PROFILE_ORACLE)
  public static class OracleConfig {

    /**
     * ORACLE 数据源。
     *
     * @return 数据源
     * @author hankai
     * @since Oct 25, 2016 11:35:50 AM
     */
    @Bean
    public DataSource dataSource() {
      final Properties props = loadExternalConfig("oracle.properties");
      final PoolProperties pp = new PoolProperties();
      pp.setUrl(props.getProperty("url"));
      pp.setDriverClassName(props.getProperty("driverClassName"));
      pp.setUsername(props.getProperty("userName"));
      pp.setPassword(props.getProperty("password"));
      pp.setJmxEnabled(true);
      pp.setTestWhileIdle(false);
      pp.setTestOnBorrow(true);
      pp.setValidationQuery("select * from dual");
      pp.setTestOnReturn(false);
      pp.setValidationInterval(30000);
      pp.setTimeBetweenEvictionRunsMillis(30000);
      pp.setMaxActive(100);
      pp.setInitialSize(10);
      pp.setMaxWait(10000);
      pp.setRemoveAbandonedTimeout(60);
      pp.setMinEvictableIdleTimeMillis(30000);
      pp.setMinIdle(10);
      pp.setLogAbandoned(true);
      pp.setRemoveAbandoned(true);
      pp.setJdbcInterceptors(
          ConnectionState.class.getName() + ";" + StatementFinalizer.class.getName());
      final org.apache.tomcat.jdbc.pool.DataSource ds =
          new org.apache.tomcat.jdbc.pool.DataSource();
      ds.setPoolProperties(pp);
      return ds;
    }

    @Bean
    public DataSourceInfo dataSourceInfo() {
      return new DataSourceInfo(OraclePlatform.class.getName(), basePackages);
    }
  }
}
