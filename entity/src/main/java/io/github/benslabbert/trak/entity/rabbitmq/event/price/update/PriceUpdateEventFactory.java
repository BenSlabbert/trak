package io.github.benslabbert.trak.entity.rabbitmq.event.price.update;

import io.github.benslabbert.trak.entity.jpa.Product;

public class PriceUpdateEventFactory {

  private PriceUpdateEventFactory() {}

  public static PriceUpdateEvent createPriceUpdateEvent(Product product, String requestId) {
    return new CreatePriceUpdateEvent(product, requestId);
  }
}
