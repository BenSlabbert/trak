package com.trak.entity.rabbit.event;

import com.trak.entity.jpa.Seller;

import java.io.Serializable;

public class CreateProductEvent implements ProductEvent, Serializable {

  private final String requestId;
  private final long productId;
  private final Seller seller;

  CreateProductEvent(String requestId, Seller seller, long productId) {
    this.requestId = requestId;
    this.productId = productId;
    this.seller = seller;
  }

  @Override
  public String getRequestId() {
    return requestId;
  }

  @Override
  public Seller getSeller() {
    return seller;
  }

  @Override
  public long getProductId() {
    return productId;
  }
}
