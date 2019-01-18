package io.github.benslabbert.trak.entity.rabbit.event;

import io.github.benslabbert.trak.entity.jpa.Product;

public class CreateProductSummaryEventFactory {

  private CreateProductSummaryEventFactory() {}

  public static ProductSummaryEvent createProductSummaryEvent(Product product) {
    return new CreateProductSummaryEvent(product);
  }
}
