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

import java.util.HashMap;
import java.util.concurrent.Executor;

import static io.github.benslabbert.trak.core.rabbitmq.Header.X_MESSAGE_TTL;
import static io.github.benslabbert.trak.core.rabbitmq.Queue.*;

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
  public Executor executor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(4);
    executor.setQueueCapacity(500);
    executor.setThreadNamePrefix("TRAK-WORKER-");
    executor.initialize();

    return executor;
  }

  private HashMap<String, Object> queueProperties() {
    HashMap<String, Object> arguments = new HashMap<>();
    arguments.put(X_MESSAGE_TTL, 300000);
    return arguments;
  }

  @Bean
  public Queue crawlerQueue() {
    return new Queue(CRAWLER_QUEUE, true, false, false, queueProperties());
  }

  @Bean
  public Queue productQueue() {
    return new Queue(PRODUCT_QUEUE, true, false, false, queueProperties());
  }

  @Bean
  public Queue priceQueue() {
    return new Queue(PRICE_QUEUE, true, false, false, queueProperties());
  }

  @Bean
  public Queue promotionQueue() {
    return new Queue(PROMOTIONS_QUEUE, true, false, false, queueProperties());
  }

  @Bean
  public Queue savingsQueue() {
    return new Queue(SAVINGS_QUEUE, true, false, false, queueProperties());
  }
}
