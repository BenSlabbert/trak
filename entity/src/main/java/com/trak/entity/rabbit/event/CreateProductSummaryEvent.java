package com.trak.entity.rabbit.event;

import com.trak.entity.jpa.Product;

public class CreateProductSummaryEvent implements ProductSummaryEvent {

  private final Product product;

  public CreateProductSummaryEvent(Product product) {
    this.product = product;
  }

  @Override
  public Product getProduct() {
    return null;
  }

  @Override
  public String getRequestId() {
    return null;
  }
}
