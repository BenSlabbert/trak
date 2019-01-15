package com.trak.entity.rabbit.event;

import com.trak.entity.jpa.Product;

public class CreateProductSummaryEventFactory {

  private CreateProductSummaryEventFactory() {}

  public static ProductSummaryEvent createProductSummaryEvent(Product product) {
    return new CreateProductSummaryEvent(product);
  }
}
