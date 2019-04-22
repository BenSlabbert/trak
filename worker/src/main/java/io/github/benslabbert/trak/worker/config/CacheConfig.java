package io.github.benslabbert.trak.worker.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

@Slf4j
@Configuration
@EnableCaching
@EnableAspectJAutoProxy
public class CacheConfig {

    @Bean
    public RedisCacheManager redisCacheManager(final RedisConnectionFactory connectionFactory) {

        final RedisCacheConfiguration cacheConfiguration =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ZERO)
                        .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory).cacheDefaults(cacheConfiguration).build();
    }
}
