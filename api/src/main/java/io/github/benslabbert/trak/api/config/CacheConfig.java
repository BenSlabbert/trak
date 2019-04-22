package io.github.benslabbert.trak.api.config;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Slf4j
@Configuration
@EnableCaching
@EnableAspectJAutoProxy
public class CacheConfig {

  @Value("${cache.duration:300}")
  private long cacheDuration;

  @Bean
  public RedisCacheManager redisCacheManager(final RedisConnectionFactory connectionFactory) {

    final RedisCacheConfiguration cacheConfiguration =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofSeconds(cacheDuration))
            .disableCachingNullValues();

    return RedisCacheManager.builder(connectionFactory).cacheDefaults(cacheConfiguration).build();
  }
}
