/*
 * Copyright © 2016 hankai.ren, All rights reserved.
 * http://www.hankai.ren
 */

package ren.hankai.appmarket.persist.config;

import org.eclipse.persistence.platform.database.HSQLPlatform;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ren.hankai.cordwood.core.Preferences;
import ren.hankai.cordwood.data.jpa.config.JpaDataSourceInfo;

import javax.sql.DataSource;

/**
 * 数据源配置类。
 *
 * @author hankai
 * @version 1.0
 * @since Aug 18, 2016 2:19:51 PM
 */
@Profile(Preferences.PROFILE_TEST)
@Configuration
public class InMemoryDataSourceConfig {

  private static final String[] basePackages = {"ren.hankai.appmarket"};

  /**
   * 数据源。
   *
   * @return 数据源
   * @author hankai
   * @since Oct 25, 2016 11:35:04 AM
   */
  @Bean
  public DataSource dataSource() {
    final DriverManagerDataSource ds = new DriverManagerDataSource();
    ds.setDriverClassName("org.hsqldb.jdbcDriver");
    ds.setUrl("jdbc:hsqldb:mem:ut-db");
    ds.setUsername("sa");
    ds.setPassword(null);
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
  public JpaDataSourceInfo dataSourceInfo() {
    return new JpaDataSourceInfo(HSQLPlatform.class.getName(), basePackages);
  }
}
