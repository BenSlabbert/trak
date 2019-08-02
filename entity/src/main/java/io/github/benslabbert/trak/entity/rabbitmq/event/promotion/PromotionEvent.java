package io.github.benslabbert.trak.entity.rabbitmq.event.promotion;

import io.github.benslabbert.trak.core.model.Promotion;
import io.github.benslabbert.trak.entity.rabbitmq.event.Event;

public interface PromotionEvent extends Event {
  Promotion getPromotion();
}
