package io.github.benslabbert.trak.entity.rabbit.event.price.clean;

import io.github.benslabbert.trak.entity.rabbit.event.Event;

public interface PriceCleanUpEvent extends Event {

  long getProductId();
}
