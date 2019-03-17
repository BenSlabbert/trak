package io.github.benslabbert.trak.entity.rabbit.event.crawler;

import io.github.benslabbert.trak.entity.jpa.Seller;

public class CrawlerEventFactory {

  private CrawlerEventFactory() {}

  public static CrawlerEvent createCrawlerEvent(String requestId, Seller seller, long productId) {
    return new CreateCrawlerEvent(requestId, seller, productId);
  }
}
