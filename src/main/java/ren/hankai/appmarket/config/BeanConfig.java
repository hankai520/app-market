/*
 * Copyright © 2016 Jiangsu Sparknet Software Co., Ltd. All rights reserved.
 *
 * http://www.sparksoft.com.cn
 */

package ren.hankai.appmarket.config;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import freemarker.cache.ClassTemplateLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.web.client.RestTemplate;
import ren.hankai.cordwood.core.Preferences;
import ren.hankai.cordwood.core.config.CoreSpringConfig;

/**
 * 通用Bean配置
 *
 * @author hankai
 * @version 1.0
 * @since Mar 31, 2016 7:45:47 PM
 */
@Configuration
public class BeanConfig extends CoreSpringConfig {

  @Override
  public ReloadableResourceBundleMessageSource getMessageSource() {
    final ReloadableResourceBundleMessageSource ms = super.getMessageSource();
    final String externalFile = "file:" + Preferences.getConfigFilePath("i18n");
    ms.addBasenames(externalFile);
    ms.setCacheSeconds(0);
    return ms;
  }

  /**
   * 用于调用 RESTful 风格的 API
   *
   * @param jacksonJsonCvt JSON消息转换器
   * @param jacksonXmlCvt XML消息转换器
   * @return
   * @author hankai
   * @since Jun 21, 2016 12:58:14 PM
   */
  @Bean
  public RestTemplate getRestTemplate(MappingJackson2HttpMessageConverter jacksonJsonCvt) {
    final HttpComponentsClientHttpRequestFactory factory =
        new HttpComponentsClientHttpRequestFactory();
    factory.setConnectTimeout(1000 * 10);// 10s
    factory.setReadTimeout(1000 * 30);// 30s
    final RestTemplate restTemplate = new RestTemplate(factory);
    restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
    restTemplate.getMessageConverters().add(new ResourceHttpMessageConverter());
    restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
    final FormHttpMessageConverter fmc = new FormHttpMessageConverter();
    fmc.addPartConverter(jacksonJsonCvt);
    restTemplate.getMessageConverters().add(fmc);
    restTemplate.getMessageConverters().add(jacksonJsonCvt);
    jacksonJsonCvt.getObjectMapper().setSerializationInclusion(Include.NON_NULL);
    return restTemplate;
  }

  @Primary
  @Bean
  public FreeMarkerConfigurationFactoryBean freeMarkerConfigurationFactory() {
    final FreeMarkerConfigurationFactoryBean factory = new FreeMarkerConfigurationFactoryBean();
    factory.setDefaultEncoding("UTF-8");
    final ClassLoader cl = Thread.currentThread().getContextClassLoader();
    final ClassTemplateLoader loader = new ClassTemplateLoader(cl, "templates");
    factory.setPreTemplateLoaders(loader);
    return factory;
  }
}
