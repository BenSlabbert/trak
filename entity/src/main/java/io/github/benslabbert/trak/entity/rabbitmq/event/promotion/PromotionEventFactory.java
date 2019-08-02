package io.github.benslabbert.trak.entity.rabbitmq.event.promotion;

import io.github.benslabbert.trak.core.model.Promotion;

public class PromotionEventFactory {

  private PromotionEventFactory() {}

  public static PromotionEvent createPromotionEvent(Promotion promotion, String requestId) {
    return new CreatePromotionEvent(promotion, requestId);
  }
}
