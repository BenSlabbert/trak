package io.github.benslabbert.trak.worker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;

import static io.github.benslabbert.trak.core.rabbitmq.Header.X_MESSAGE_TTL;
import static io.github.benslabbert.trak.core.rabbitmq.Queue.*;
import static io.github.benslabbert.trak.core.rabbitmq.RPC.*;

@Slf4j
@EnableAsync
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

    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(4);
    executor.setQueueCapacity(Integer.MAX_VALUE);
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

  @Bean
  public DirectExchange addProductDirectExchange() {
    return new DirectExchange(ADD_PRODUCT_RPC, true, false);
  }

  private HashMap<String, Object> queueProperties() {

    HashMap<String, Object> arguments = new HashMap<>();
    arguments.put(X_MESSAGE_TTL, 300000);

    return arguments;
  }

  @Bean
  public Queue addProductQueue() {
    return new Queue(ADD_PRODUCT_RPC_QUEUE, true, false, false, queueProperties());
  }

  @Bean
  public Binding binding(DirectExchange addProductDirectExchange, Queue addProductQueue) {
    return BindingBuilder.bind(addProductQueue)
        .to(addProductDirectExchange)
        .with(ADD_PRODUCT_RPC_ROUTING_KEY);
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
