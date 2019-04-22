package io.github.benslabbert.trak.entity.rabbitmq.event.price.clean;

import io.github.benslabbert.trak.entity.rabbitmq.event.Event;

public interface PriceCleanUpEvent extends Event {

  long getProductId();
}
