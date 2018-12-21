package com.trak.entity.rabbit.event;

import com.trak.entity.jpa.Seller;

public class CreateProductEventFactory {

  private CreateProductEventFactory() {}

  public static ProductEvent createProductEvent(String requestId, Seller seller, long productId) {
    return new CreateProductEvent(requestId, seller, productId);
  }
}
