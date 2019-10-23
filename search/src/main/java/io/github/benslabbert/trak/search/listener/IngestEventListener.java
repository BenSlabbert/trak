package io.github.benslabbert.trak.search.listener;

import io.github.benslabbert.trak.entity.rabbitmq.event.sonic.IngestEvent;
import io.github.benslabbert.trak.search.sonic.SonicSearch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static io.github.benslabbert.trak.core.rabbitmq.Queue.SONIC_INGEST_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(queues = SONIC_INGEST_QUEUE)
public class IngestEventListener {

  private final SonicSearch sonicSearch;

  @Async
  @RabbitHandler
  public void receive(IngestEvent ingestEvent) {
    long start = Instant.now().toEpochMilli();
    String reqId = ingestEvent.getRequestId();
    log.info("{}: Got Ingest event for collection: {}", reqId, ingestEvent.getCollection());

    switch (ingestEvent.getCollection()) {
      case BRAND:
        sonicSearch.brandIngest(ingestEvent.getObjectId(), ingestEvent.getText());
        break;
      case PRODUCT:
        sonicSearch.productIngest(ingestEvent.getObjectId(), ingestEvent.getText());
        break;
      case CATEGORY:
        sonicSearch.categoryIngest(ingestEvent.getObjectId(), ingestEvent.getText());
        break;
      default:
        log.warn("Unknown collection specified, will not ingest into Sonic!");
    }

    long total = Instant.now().toEpochMilli() - start;
    log.info("{}: time to process: {}ms", reqId, total);
  }
}
