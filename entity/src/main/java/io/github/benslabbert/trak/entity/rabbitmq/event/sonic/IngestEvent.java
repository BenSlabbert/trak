package io.github.benslabbert.trak.entity.rabbitmq.event.sonic;

import io.github.benslabbert.trak.entity.rabbitmq.event.Event;

public interface IngestEvent extends Event {

  String getObjectId();

  String getText();

  Collection getCollection();
}
