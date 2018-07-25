/*
 * Copyright © 2015 Jiangsu Sparknet Software Co., Ltd, All rights reserved.
 *
 * http://www.sparksoft.com.cn
 */

package ren.hankai.appmarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ren.hankai.cordwood.core.ApplicationInitializer;

@SpringBootApplication
@EnableSpringConfigured
@EnableAspectJAutoProxy
@EnableTransactionManagement
@EnableConfigurationProperties
public class Application {

  public static void main(String[] args) {
    final String[] configs =
        {"mysql.properties", "system.yml", "i18n.properties"};
    if (ApplicationInitializer.initialize(configs)) {
      SpringApplication.run(Application.class, args);
    }
  }
}
