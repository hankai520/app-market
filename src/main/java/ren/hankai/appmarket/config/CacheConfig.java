/*
 * Copyright © 2016 Jiangsu Sparknet Software Co., Ltd. All rights reserved.
 * http://www.sparksoft.com.cn
 */

package ren.hankai.appmarket.config;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
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

  @Override
  protected CacheConfiguration getLightWeightCacheConfig() {
    final PersistenceConfiguration pc = new PersistenceConfiguration();
    pc.setStrategy(PersistenceConfiguration.Strategy.LOCALTEMPSWAP.name());

    final CacheConfiguration lightWeightCache = new CacheConfiguration();
    lightWeightCache.setMaxEntriesLocalHeap(200);
    lightWeightCache.setTimeToIdleSeconds(60 * 5); // 距离上次访问5分钟内缓存
    lightWeightCache.setTimeToLiveSeconds(60 * 5); // 缓存建立后，5分钟过期
    lightWeightCache.persistence(pc);
    return lightWeightCache;
  }

}
