package io.github.benslabbert.trak.entity.rabbit.event;

import io.github.benslabbert.trak.entity.jpa.Seller;

public class CreateCrawlerEvent implements CrawlerEvent {

  private static final long serialVersionUID = -8685816501160613188L;

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
