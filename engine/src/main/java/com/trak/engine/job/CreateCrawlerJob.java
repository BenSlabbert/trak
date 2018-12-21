package com.trak.engine.job;

import com.trak.entity.jpa.Crawler;
import com.trak.entity.jpa.Seller;
import com.trak.entity.jpa.service.CrawlerService;
import com.trak.entity.jpa.service.SellerService;
import com.trak.entity.rabbit.event.CreateProductEventFactory;
import com.trak.entity.rabbit.event.ProductEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class CreateCrawlerJob {

  private final RabbitTemplate rabbitTemplate;
  private final CrawlerService crawlerService;
  private final SellerService sellerService;
  private final Queue queue;

  public CreateCrawlerJob(
      RabbitTemplate rabbitTemplate,
      CrawlerService crawlerService,
      SellerService sellerService,
      Queue queue) {

    this.rabbitTemplate = rabbitTemplate;
    this.crawlerService = crawlerService;
    this.sellerService = sellerService;
    this.queue = queue;
  }

  @Transactional
  @Scheduled(initialDelay = 0L, fixedDelay = 1000L)
  public void job() {

    String requestId = UUID.randomUUID().toString();

    log.info("{}: Creating event", requestId);

    for (Seller seller : sellerService.findAll()) {

      Optional<Crawler> crawler = crawlerService.findBySeller(seller);

      if (crawler.isEmpty()) {
        log.warn("{}: No crawler for sellerId: {}", requestId, seller.getId());
        return;
      }

      Long lastProductId = crawler.get().getLastId();

      ProductEvent productEvent =
          CreateProductEventFactory.createProductEvent(requestId, seller, lastProductId);

      crawler.get().setLastId(++lastProductId);

      crawlerService.save(crawler.get());

      rabbitTemplate.convertAndSend(queue.getName(), productEvent);
    }
  }
}
