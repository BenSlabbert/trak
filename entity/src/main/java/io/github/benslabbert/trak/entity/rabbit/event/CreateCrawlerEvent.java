package io.github.benslabbert.trak.entity.rabbit.event;

import io.github.benslabbert.trak.entity.jpa.Seller;

import java.io.Serializable;

public class CreateCrawlerEvent implements CrawlerEvent, Serializable {

  private final String requestId;
  private final long productId;
  private final Seller seller;

  CreateCrawlerEvent(String requestId, Seller seller, long productId) {
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