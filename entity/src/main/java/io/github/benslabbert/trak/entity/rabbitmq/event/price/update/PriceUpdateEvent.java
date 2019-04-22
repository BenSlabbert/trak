package io.github.benslabbert.trak.entity.rabbitmq.event.price.update;

import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.rabbitmq.event.Event;

public interface PriceUpdateEvent extends Event {

  Product getProduct();
}
