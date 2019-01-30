package io.github.benslabbert.trak.entity.rabbit.event;

import java.io.Serializable;

public interface Event extends Serializable {

  String getRequestId();
}
