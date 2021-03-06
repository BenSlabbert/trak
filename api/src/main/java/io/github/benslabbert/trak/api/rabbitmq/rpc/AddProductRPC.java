package io.github.benslabbert.trak.api.rabbitmq.rpc;

import io.github.benslabbert.trak.core.annotation.RabbitMQRPC;
import io.github.benslabbert.trak.entity.rabbitmq.rpc.AddProductRPCRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static io.github.benslabbert.trak.core.rabbitmq.RPC.ADD_PRODUCT_RPC_ROUTING_KEY;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddProductRPC {

  private final DirectExchange addProductDirectExchange;
  private final RabbitTemplate rabbitTemplate;

  /**
   * Invokes an RPC request with RabbitMQ to a worker Node
   *
   * @param addProductRPCRequest the request
   * @return the productId from the new or existing product
   */
  @RabbitMQRPC(message = "Add product RPC")
  public Optional<Long> addProduct(AddProductRPCRequest addProductRPCRequest) {
    Long productId =
        (Long)
            rabbitTemplate.convertSendAndReceive(
                addProductDirectExchange.getName(),
                ADD_PRODUCT_RPC_ROUTING_KEY,
                addProductRPCRequest);

    return Optional.ofNullable(productId);
  }
}
