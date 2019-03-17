package io.github.benslabbert.trak.entity.rabbit.event.price.update;

import io.github.benslabbert.trak.entity.jpa.Product;

public class CreatePriceUpdateEvent implements PriceUpdateEvent {

  private static final long serialVersionUID = 8623837985667035203L;

  private final Product product;
  private final String requestId;

  public CreatePriceUpdateEvent(Product product, String requestId) {
    this.product = product;
    this.requestId = requestId;
  }

  @Override
  public Product getProduct() {
    return product;
  }

  @Override
  public String getRequestId() {
    return requestId;
  }
}
