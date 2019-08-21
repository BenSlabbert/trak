package io.github.benslabbert.trak.worker.listener;

import io.github.benslabbert.trak.entity.jpa.Price;
import io.github.benslabbert.trak.entity.jpa.service.PriceService;
import io.github.benslabbert.trak.entity.rabbitmq.event.price.update.PriceUpdateEvent;
import io.github.benslabbert.trak.worker.response.ProductResponse;
import io.github.benslabbert.trak.worker.util.ProductRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static io.github.benslabbert.trak.core.rabbitmq.Queue.PRODUCT_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(queues = PRODUCT_QUEUE, containerFactory = "customRabbitListenerContainerFactory")
public class ProductEventListener extends ProductRequest {

  private final PriceService priceService;

  @Async
  @RabbitHandler
  public void receive(PriceUpdateEvent priceUpdateEvent) {
    log.info("{}: Processing update price event", priceUpdateEvent.getRequestId());

    Optional<ProductResponse> productResponse =
        getProductResponse(priceUpdateEvent.getProduct().getApiEndpoint());

    if (productResponse.isEmpty()) {
      log.warn(
          "Failed to get product response for productId: {}, productName: {}",
          priceUpdateEvent.getProduct().getId(),
          priceUpdateEvent.getProduct().getName());

      return;
    }

    Long currentPrice = productResponse.get().getCurrentPrice();
    Long listedPrice = productResponse.get().getListedPrice();

    priceService.save(
        Price.builder()
            .productId(priceUpdateEvent.getProduct().getId())
            .currentPrice(currentPrice)
            .listedPrice(listedPrice)
            .build());
  }
}
