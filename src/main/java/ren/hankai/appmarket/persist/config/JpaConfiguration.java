/*
 * Copyright © 2016 hankai.ren, All rights reserved.
 *
 * http://www.hankai.ren
 */

package ren.hankai.appmarket.persist.config;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;
import ren.hankai.appmarket.persist.support.BaseRepositoryFactoryBean;
import ren.hankai.appmarket.persist.util.Slf4jSessionLogger;
import ren.hankai.cordwood.core.Preferences;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

/**
 * JPA 配置基类。
 *
 * @author hankai
 * @version 1.0
 * @since Aug 18, 2016 9:57:22 AM
 */
@Configuration
@EnableJpaRepositories(basePackages = {"ren.hankai"},
    repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
@EnableTransactionManagement
public class JpaConfiguration extends JpaBaseConfiguration {

  /**
   * 初始化 JPA 配置。
   *
   * @param dataSource 数据源
   * @param properties JPA 属性配置
   * @param jtaTransactionManagerProvider 事务管理器提供者
   */
  protected JpaConfiguration(DataSource dataSource, JpaProperties properties,
      ObjectProvider<JtaTransactionManager> jtaTransactionManagerProvider) {
    super(dataSource, properties, jtaTransactionManagerProvider);
  }

  protected static Logger logger = LoggerFactory.getLogger(JpaConfiguration.class);
  @Autowired
  private DataSourceInfo dataSourceInfo;

  @Override
  protected String[] getPackagesToScan() {
    final String[] packages = super.getPackagesToScan();
    final String[] customPackages = dataSourceInfo.getEntityBasePackages();
    if ((customPackages != null) && (customPackages.length > 0)) {
      return ArrayUtils.addAll(packages, customPackages);
    }
    return packages;
  }

  @Override
  protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
    final EclipseLinkJpaVendorAdapter adapter = new EclipseLinkJpaVendorAdapter();
    adapter.setDatabasePlatform(dataSourceInfo.getDatabasePlatform());
    adapter.setShowSql(true);
    return adapter;
  }

  @Override
  protected Map<String, Object> getVendorProperties() {
    final Map<String, Object> jpaProperties = new HashMap<>();
    jpaProperties.put("eclipselink.target-database", dataSourceInfo.getDatabasePlatform());
    /*
     * create-tables, create-or-extend-tables, drop-and-create-tables, none
     */
    jpaProperties.put("eclipselink.ddl-generation", "create-or-extend-tables");
    /*
     * both, database, sql-script
     */
    jpaProperties.put("eclipselink.ddl-generation.output-mode", "sql-script");
    jpaProperties.put("eclipselink.application-location", Preferences.getTempDir());
    jpaProperties.put("eclipselink.create-ddl-jdbc-file-name", "eclipselink_create.sql");
    jpaProperties.put("eclipselink.drop-ddl-jdbc-file-name", "eclipselink_drop.sql");
    jpaProperties.put("eclipselink.weaving", "static");
    jpaProperties.put("eclipselink.logging.level", "FINE");
    jpaProperties.put("eclipselink.logging.parameters", "true");
    jpaProperties.put("eclipselink.logging.logger", Slf4jSessionLogger.class.getName());
    return jpaProperties;
  }
}
