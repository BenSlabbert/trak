package io.github.benslabbert.trak.entity.rabbit.event;

import io.github.benslabbert.trak.entity.jpa.Product;

public class CreatePriceUpdateEventFactory {

  private CreatePriceUpdateEventFactory() {}

  public static PriceUpdateEvent CreatePriceUpdateEvent(Product product, String requestId) {
    return new CreatePriceUpdateEvent(product, requestId);
  }
}
