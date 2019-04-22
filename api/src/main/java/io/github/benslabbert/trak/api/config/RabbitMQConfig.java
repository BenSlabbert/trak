package io.github.benslabbert.trak.api.config;

import static io.github.benslabbert.trak.core.rabbitmq.Header.X_MESSAGE_TTL;
import static io.github.benslabbert.trak.core.rabbitmq.RPC.*;

import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitMQConfig {

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
}
