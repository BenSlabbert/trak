package io.github.benslabbert.trak.entity.rabbitmq.event.crawler;

import io.github.benslabbert.trak.entity.jpa.Seller;
import io.github.benslabbert.trak.entity.rabbitmq.event.Event;

public interface CrawlerEvent extends Event {

  Seller getSeller();

  long getProductId();
}
