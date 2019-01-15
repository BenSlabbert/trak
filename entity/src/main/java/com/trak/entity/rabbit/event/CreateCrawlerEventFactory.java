package com.trak.entity.rabbit.event;

import com.trak.entity.jpa.Seller;

public class CreateCrawlerEventFactory {

  private CreateCrawlerEventFactory() {}

  public static CrawlerEvent createProductEvent(String requestId, Seller seller, long productId) {
    return new CreateCrawlerEvent(requestId, seller, productId);
  }
}
