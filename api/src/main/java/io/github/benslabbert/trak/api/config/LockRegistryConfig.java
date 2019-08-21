package io.github.benslabbert.trak.api.config;

import io.github.benslabbert.trak.core.concurrent.DistributedLockRegistry;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.locks.Lock;

@Component
@Scope("singleton")
public class LockRegistryConfig implements DistributedLockRegistry {

  private static final String REGISTRY_KEY = "api-locks";
  private final RedisLockRegistry registry;

  public LockRegistryConfig(RedisConnectionFactory redisConnectionFactory) {
    this.registry =
        new RedisLockRegistry(
            redisConnectionFactory, REGISTRY_KEY, Duration.ofSeconds(100).toMillis());
  }

  @Override
  public String getRegistryKey() {
    return REGISTRY_KEY;
  }

  @Override
  public Lock obtain(String lockKey) {
    return registry.obtain(lockKey);
  }
}
