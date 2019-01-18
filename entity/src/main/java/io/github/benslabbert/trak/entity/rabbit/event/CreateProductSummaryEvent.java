package io.github.benslabbert.trak.entity.rabbit.event;

import io.github.benslabbert.trak.entity.jpa.Product;

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
