package io.github.benslabbert.trak.entity.rabbit.event;

import io.github.benslabbert.trak.entity.jpa.Seller;

public class CreateCrawlerEventFactory {

  private CreateCrawlerEventFactory() {}

  public static CrawlerEvent createProductEvent(String requestId, Seller seller, long productId) {
    return new CreateCrawlerEvent(requestId, seller, productId);
  }
}
