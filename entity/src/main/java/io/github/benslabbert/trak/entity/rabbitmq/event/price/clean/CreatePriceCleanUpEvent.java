package io.github.benslabbert.trak.entity.rabbitmq.event.price.clean;

public class CreatePriceCleanUpEvent implements PriceCleanUpEvent {

  private final String requestId;
  private final long productId;

  public CreatePriceCleanUpEvent(String requestId, long productId) {
    this.requestId = requestId;
    this.productId = productId;
  }

  @Override
  public String getRequestId() {
    return requestId;
  }

  @Override
  public long getProductId() {
    return productId;
  }
}
