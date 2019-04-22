package io.github.benslabbert.trak.entity.rabbitmq.event;

import java.io.Serializable;

public interface Event extends Serializable {

  String getRequestId();
}
