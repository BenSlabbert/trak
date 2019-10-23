package io.github.benslabbert.trak.entity.rabbitmq.event.sonic;

public class IngestEventFactory {

  private IngestEventFactory() {}

  public static IngestEvent createIngestEvent(
      Collection collection, String objectId, String text, String requestId) {
    return new CreateIngestEvent(collection, requestId, objectId, text);
  }
}
