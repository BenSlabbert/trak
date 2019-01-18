package io.github.benslabbert.trak.entity.rabbit.event;

import io.github.benslabbert.trak.entity.jpa.Product;

public interface ProductSummaryEvent extends Event {

  Product getProduct();
}
