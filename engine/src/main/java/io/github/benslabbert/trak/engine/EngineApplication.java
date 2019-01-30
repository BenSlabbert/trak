package io.github.benslabbert.trak.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static io.github.benslabbert.trak.entity.rabbit.Queue.CRAWLER_QUEUE;
import static io.github.benslabbert.trak.entity.rabbit.Queue.PRODUCT_QUEUE;

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

  @Bean
  public ThreadPoolTaskScheduler threadPoolTaskScheduler() {

    ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

    threadPoolTaskScheduler.setPoolSize(5);
    threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");

    return threadPoolTaskScheduler;
  }

  @Bean
  public Queue crawlerQueue() {
    return new Queue(CRAWLER_QUEUE, true, false, false);
  }

  @Bean
  public Queue productQueue() {
    return new Queue(PRODUCT_QUEUE, true, false, false);
  }
}
