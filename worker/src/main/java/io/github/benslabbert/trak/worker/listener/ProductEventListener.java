package io.github.benslabbert.trak.worker.listener;

import io.github.benslabbert.trak.entity.jpa.Price;
import io.github.benslabbert.trak.entity.jpa.service.PriceService;
import io.github.benslabbert.trak.entity.rabbit.event.PriceUpdateEvent;
import io.github.benslabbert.trak.worker.response.ProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static io.github.benslabbert.trak.entity.rabbit.Queue.PRODUCT_QUEUE;

@Slf4j
@Component
@RabbitListener(queues = PRODUCT_QUEUE, containerFactory = "customRabbitListenerContainerFactory")
public class ProductEventListener extends ProductRequest {

  private final PriceService priceService;

  public ProductEventListener(PriceService priceService) {
    this.priceService = priceService;
  }

  @RabbitHandler
  public void receive(PriceUpdateEvent priceUpdateEvent) {

    log.debug(priceUpdateEvent.getRequestId());

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
