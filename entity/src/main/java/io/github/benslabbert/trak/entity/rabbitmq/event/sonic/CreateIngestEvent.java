package io.github.benslabbert.trak.entity.rabbitmq.event.sonic;

public class CreateIngestEvent implements IngestEvent {
  private static final long serialVersionUID = -2131290535238067597L;
  private final Collection collection;
  private final String requestId;
  private final String objectId;
  private final String text;

  public CreateIngestEvent(Collection collection, String requestId, String objectId, String text) {
    this.collection = collection;
    this.requestId = requestId;
    this.objectId = objectId;
    this.text = text;
  }

  @Override
  public String getObjectId() {
    return objectId;
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public Collection getCollection() {
    return collection;
  }

  @Override
  public String getRequestId() {
    return requestId;
  }
}
