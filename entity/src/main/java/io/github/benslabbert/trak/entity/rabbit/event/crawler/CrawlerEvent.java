package io.github.benslabbert.trak.entity.rabbit.event.crawler;

import io.github.benslabbert.trak.entity.jpa.Seller;
import io.github.benslabbert.trak.entity.rabbit.event.Event;

public interface CrawlerEvent extends Event {

  Seller getSeller();

  long getProductId();
}
