/*
 * Copyright © 2016 Jiangsu Sparknet Software Co., Ltd. All rights reserved.
 * http://www.sparksoft.com.cn
 */

package ren.hankai.appmarket.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import ren.hankai.cordwood.core.config.CoreCacheConfig;

/**
 * 缓存配置。
 *
 * @author hankai
 * @version 1.0.0
 * @since Jul 30, 2018 1:54:39 PM
 */
@Configuration
@EnableCaching
public class CacheConfig extends CoreCacheConfig {

}
