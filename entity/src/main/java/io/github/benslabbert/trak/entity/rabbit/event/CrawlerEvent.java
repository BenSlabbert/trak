package io.github.benslabbert.trak.entity.rabbit.event;

import io.github.benslabbert.trak.entity.jpa.Seller;

public interface CrawlerEvent extends Event {

  Seller getSeller();

  long getProductId();
}
