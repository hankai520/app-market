/*
 * Copyright © 2016 hankai.ren, All rights reserved.
 * http://www.hankai.ren
 */

package ren.hankai.appmarket.persist.config;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.persistence.tools.profiler.PerformanceProfiler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;
import ren.hankai.appmarket.persist.util.Slf4jSessionLogger;
import ren.hankai.cordwood.core.Preferences;
import ren.hankai.cordwood.data.jpa.config.JpaDataSourceInfo;
import ren.hankai.cordwood.data.jpa.support.BaseRepositoryFactoryBean;

import java.io.File;
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

  @Autowired
  private JpaDataSourceInfo dataSourceInfo;

  /**
   * 初始化 JPA 配置。
   *
   * @param dataSource 数据源
   * @param properties JPA配置属性
   * @param jtaTransactionManager 事务管理器
   * @param transactionManagerCustomizers 自定义事务管理
   */
  protected JpaConfiguration(final DataSource dataSource, final JpaProperties properties,
      final ObjectProvider<JtaTransactionManager> jtaTransactionManager,
      final ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
    super(dataSource, properties, jtaTransactionManager, transactionManagerCustomizers);
  }

  @Override
  protected String[] getPackagesToScan() {
    final String[] defaultPackages = ArrayUtils.add(super.getPackagesToScan(), "sparksoft");
    final String[] packages = dataSourceInfo.getPackagesToScan(defaultPackages);
    return packages;
  }

  @Override
  protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
    return dataSourceInfo.createJpaVendorAdapter(true);
  }

  @Override
  protected Map<String, Object> getVendorProperties() {
    final Map<String, Object> jpaProperties = dataSourceInfo.getVendorProperties();
    final boolean enabled =
        Boolean.parseBoolean(Preferences.getCustomConfig("persistence.profiler.enabled"));
    if (enabled) {
      jpaProperties.put("eclipselink.profiler", PerformanceProfiler.class.getName());
      jpaProperties.put("eclipselink.logging.file",
          Preferences.getTempDir() + File.separator + "eclipselink_performance.txt");
    } else {
      jpaProperties.put("eclipselink.logging.logger", Slf4jSessionLogger.class.getName());
    }
    return jpaProperties;
  }
}
