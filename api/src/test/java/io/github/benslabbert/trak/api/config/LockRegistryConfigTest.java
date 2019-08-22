package io.github.benslabbert.trak.api.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

import java.util.concurrent.locks.Lock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Slf4j
@RunWith(SpringRunner.class)
public class LockRegistryConfigTest {

  private static LettuceConnectionFactory connectionFactory;
  private LockRegistryConfig lockRegistryConfig;

  @ClassRule
  public static GenericContainer redis =
      new GenericContainer<>("redis:5.0.4-alpine")
          .withCommand("redis-server --requirepass password")
          .withExposedPorts(6379);

  @BeforeClass
  public static void beforeClass() {
    log.info("Getting RedisConnectionFactory");
    RedisStandaloneConfiguration rsc =
        new RedisStandaloneConfiguration(redis.getIpAddress(), redis.getMappedPort(6379));
    rsc.setPassword("password");

    connectionFactory = new LettuceConnectionFactory(rsc);
    connectionFactory.afterPropertiesSet();
    RedisConnection connection = connectionFactory.getConnection();
    assertNotNull(connection);
  }

  @AfterClass
  public static void afterClass() {
    log.info("Destroying RedisConnectionFactory");
    connectionFactory.destroy();
  }

  @Before
  public void setUp() {
    lockRegistryConfig = new LockRegistryConfig(connectionFactory);
  }

  @Test
  public void getRegistryKeyTest() {
    LockRegistryConfig registryConfig = new LockRegistryConfig(connectionFactory);
    String registryKey = registryConfig.getRegistryKey();
    assertNotNull(registryKey);
    assertEquals("api-locks", registryKey);
  }

  @Test
  public void obtainTest() {
    Lock lock = lockRegistryConfig.obtain("test");
    assertNotNull(lock);
    lock.lock();
    lock.unlock();
  }
}
