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
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class CreateCrawlerJob implements Runnable {

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

  @Async
  @Override
  @Scheduled(initialDelay = 0L, fixedDelay = 1000L)
  public void run() {

    for (Seller seller : sellerService.findAll()) {

      Optional<Crawler> crawler = crawlerService.findBySeller(seller);

      if (crawler.isPresent()) {

        String requestId = UUID.randomUUID().toString();

        log.info("{}: Creating event", requestId);

        long lastProductId = crawler.get().getLastId();

        ProductEvent productEvent =
            CreateProductEventFactory.createProductEvent(requestId, seller, lastProductId);

        crawler.get().setLastId(++lastProductId);

        crawlerService.save(crawler.get());

        rabbitTemplate.convertAndSend(queue.getName(), productEvent);
      } else {
        log.warn("No crawler for sellerId: {}", seller.getId());
      }
    }
  }
}
