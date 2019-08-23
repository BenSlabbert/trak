package io.github.benslabbert.trak.api.config;

import io.github.benslabbert.trak.core.cache.CacheNames;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Configuration
@EnableCaching
@EnableAspectJAutoProxy
public class CacheConfig {

  @Bean
  public RedisCacheManager redisCacheManager(final RedisConnectionFactory connectionFactory) {
    final RedisCacheConfiguration cacheConfiguration =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(15))
            .disableCachingNullValues();

    // todo remove duplicate
    Set<String> s = new HashSet<>();
    s.add(CacheNames.PRODUCT_CACHE);
    s.add(CacheNames.PRICE_CACHE);
    s.add(CacheNames.SELLER_CACHE);
    s.add(CacheNames.BRAND_CACHE);
    s.add(CacheNames.CRAWLER_CACHE);
    s.add(CacheNames.BEST_SAVINGS_CACHE);
    s.add(CacheNames.CATEGORY_CACHE);
    s.add(CacheNames.TAKEALOT_PROMOTION_CACHE);
    s.add(CacheNames.PROMOTION_ENTITY_CACHE);

    return RedisCacheManager.builder(connectionFactory)
        .initialCacheNames(s)
        .cacheDefaults(cacheConfiguration)
        .build();
  }
}
