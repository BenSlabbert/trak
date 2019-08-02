package io.github.benslabbert.trak.entity.rabbitmq.event.promotion;

import io.github.benslabbert.trak.core.model.Promotion;

public class CreatePromotionEvent implements PromotionEvent {

  private static final long serialVersionUID = -5261797607400295613L;
  private final Promotion promotion;
  private final String requestId;

  public CreatePromotionEvent(Promotion promotion, String requestId) {
    this.promotion = promotion;
    this.requestId = requestId;
  }

  @Override
  public Promotion getPromotion() {
    return promotion;
  }

  @Override
  public String getRequestId() {
    return requestId;
  }
}
