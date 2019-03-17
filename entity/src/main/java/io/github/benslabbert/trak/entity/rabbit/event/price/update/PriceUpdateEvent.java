package io.github.benslabbert.trak.entity.rabbit.event.price.update;

import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.rabbit.event.Event;

public interface PriceUpdateEvent extends Event {

  Product getProduct();
}
