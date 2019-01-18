package io.github.benslabbert.trak.worker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static io.github.benslabbert.trak.entity.rabbit.Queue.CRAWLER_QUEUE;

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

  @Bean
  public Queue createCrawlerQueue() {
    return new Queue(CRAWLER_QUEUE, true, false, false);
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
