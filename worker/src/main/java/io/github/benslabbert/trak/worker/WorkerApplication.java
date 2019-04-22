package io.github.benslabbert.trak.worker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@EnableAsync
@EnableScheduling
@SpringBootApplication
@EnableTransactionManagement
public class WorkerApplication {

  public static void main(String[] args) {

    try {
      SpringApplication.run(WorkerApplication.class, args);
    } catch (Exception e) {
      log.error("Failed to run application!", e);
    }
  }

  @Primary
  @Bean
  public ThreadPoolTaskExecutor executor() {

    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(4);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(10);
    executor.setThreadNamePrefix("TRAK-WORKER-");
    executor.initialize();

    return executor;
  }

  @Bean
  public RabbitListenerContainerFactory<SimpleMessageListenerContainer>
      customRabbitListenerContainerFactory(ConnectionFactory rabbitConnectionFactory) {

    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();

    factory.setConnectionFactory(rabbitConnectionFactory);
    factory.setPrefetchCount(0);

    return factory;
  }
}
