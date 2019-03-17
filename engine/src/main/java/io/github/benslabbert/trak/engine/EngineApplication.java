package io.github.benslabbert.trak.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static io.github.benslabbert.trak.entity.rabbit.Queue.*;

@Slf4j
@EnableAsync
@EnableScheduling
@SpringBootApplication
@EnableTransactionManagement
public class EngineApplication {

  public static void main(String[] args) {

    try {
      SpringApplication.run(EngineApplication.class, args);
    } catch (Exception e) {
      log.error("Failed to run application!", e);
    }
  }

  @Primary
  @Bean
  public ThreadPoolTaskExecutor executor() {

    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    executor.setMaxPoolSize(500);
    executor.setQueueCapacity(Integer.MAX_VALUE);
    executor.setThreadNamePrefix("TRAK-ENGINE-");
    executor.initialize();

    return executor;
  }

  @Bean
  public Queue crawlerQueue() {
    return new Queue(CRAWLER_QUEUE, true, false, false);
  }

  @Bean
  public Queue productQueue() {
    return new Queue(PRODUCT_QUEUE, true, false, false);
  }

  @Bean
  public Queue priceQueue() {
    return new Queue(PRICE_QUEUE, true, false, false);
  }
}
