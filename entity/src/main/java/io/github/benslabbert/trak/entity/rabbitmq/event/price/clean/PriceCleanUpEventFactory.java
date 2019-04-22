package io.github.benslabbert.trak.entity.rabbitmq.event.price.clean;

import java.util.UUID;

public class PriceCleanUpEventFactory {

  private PriceCleanUpEventFactory() {}

  public static PriceCleanUpEvent create(long  productId) {
    return new CreatePriceCleanUpEvent(UUID.randomUUID().toString(), productId);
  }
}
