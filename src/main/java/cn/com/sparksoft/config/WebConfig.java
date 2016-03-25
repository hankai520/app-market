/*
 * Copyright © 2015 Jiangsu Sparknet Software Co., Ltd. All Rights Reserved
 *
 *    http://www.sparksoft.com.cn
 */

package cn.com.sparksoft.config;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ResourceBundleViewResolver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Web相关配置
 *
 * @author hankai
 * @version 1.0
 * @since Jul 16, 2015 4:21:45 PM
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    public static final String           DATE_FORMAT       = "yyyy-MM-dd";
    public static final String           DATE_TIME_FORMAT  = "yyyy-MM-dd HH:mm:ss";
    public static final SimpleDateFormat dateFormatter     = new SimpleDateFormat( DATE_FORMAT );
    public static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(
                                                               DATE_TIME_FORMAT );
    /**
     * 用于在页面渲染前传递页面级提示消息
     */
    public static final String           WEB_PAGE_MESSAGE  = "pageMessage";
    /**
     * 用于在页面渲染前传递页面级错误
     */
    public static final String           WEB_PAGE_ERROR    = "pageError";

    @Override
    public void addFormatters( FormatterRegistry registry ) {
        // 处理表单录入日期
        registry.addConverter( new Converter<String, Date>() {

            @Override
            public Date convert( String source ) {
                Date date = null;
                try {
                    date = dateFormatter.parse( source );
                } catch (ParseException e) {
                    try {
                        date = dateTimeFormatter.parse( source );
                    } catch (ParseException e1) {
                    }
                }
                return date;
            }
        } );
    }

    @Bean(
        name = "messageSource" )
    public ReloadableResourceBundleMessageSource getMessageSource() {
        ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
        ms.setBasenames( "WEB-INF/i18n/messages", "WEB-INF/i18n/validation" );
        ms.setDefaultEncoding( "UTF-8" );
        ms.setCacheSeconds( 0 );
        ms.setFallbackToSystemLocale( false );
        ms.setUseCodeAsDefaultMessage( true );
        return ms;
    }

    @Bean
    public ResourceBundleViewResolver getResourceBundleViewResolver() {
        ResourceBundleViewResolver resolver = new ResourceBundleViewResolver();
        resolver.setBasename( "views" );
        return resolver;
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion( Include.NON_NULL );
        return objectMapper;
    }

    @Bean
    public MappingJackson2HttpMessageConverter getJackson2HttpMessageConverter( ObjectMapper om ) {
        return new MappingJackson2HttpMessageConverter( om );
    }

    @Bean
    public RestTemplate getRestTemplate( MappingJackson2HttpMessageConverter jacksonCvt ) {
        HttpComponentsClientHttpRequestFactory factory =
                                                         new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout( 1000 * 10 );// 10s
        factory.setReadTimeout( 1000 * 30 );// 30s
        RestTemplate restTemplate = new RestTemplate( factory );
        restTemplate.getMessageConverters().add( new StringHttpMessageConverter() );
        restTemplate.getMessageConverters().add( new ResourceHttpMessageConverter() );
        restTemplate.getMessageConverters().add( new ByteArrayHttpMessageConverter() );
        FormHttpMessageConverter fmc = new FormHttpMessageConverter();
        fmc.addPartConverter( jacksonCvt );
        restTemplate.getMessageConverters().add( fmc );
        restTemplate.getMessageConverters().add( jacksonCvt );
        return restTemplate;
    }
}
