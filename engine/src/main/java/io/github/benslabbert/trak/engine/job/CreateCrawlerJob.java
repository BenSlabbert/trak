package io.github.benslabbert.trak.engine.job;

import io.github.benslabbert.trak.entity.jpa.Crawler;
import io.github.benslabbert.trak.entity.jpa.Seller;
import io.github.benslabbert.trak.entity.jpa.service.CrawlerService;
import io.github.benslabbert.trak.entity.jpa.service.SellerService;
import io.github.benslabbert.trak.entity.rabbit.event.CreateCrawlerEventFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class CreateCrawlerJob extends PageOverAll<Seller> implements Runnable {

  private final RabbitTemplate rabbitTemplate;
  private final CrawlerService crawlerService;
  private final SellerService sellerService;
  private final Queue queue;

  public CreateCrawlerJob(
      RabbitTemplate rabbitTemplate,
      CrawlerService crawlerService,
      SellerService sellerService,
      Queue crawlerQueue) {

    this.rabbitTemplate = rabbitTemplate;
    this.crawlerService = crawlerService;
    this.sellerService = sellerService;
    this.queue = crawlerQueue;
  }

  @Async
  @Override
  @Scheduled(initialDelay = 0L, fixedDelay = 5000L)
  public void run() {

    try {
      pageOverAll(sellerService.findAll(PageRequest.of(0, 10)));
    } catch (Exception e) {
      log.debug("General exception", e);
    }

    log.debug("Finished job");
  }

  @Override
  Page<Seller> nextPage(Page<Seller> page) {
    return sellerService.findAll(page.nextPageable());
  }

  @Override
  void processItem(Seller seller) {

    Optional<Crawler> crawler = crawlerService.findBySeller(seller);

    if (crawler.isPresent()) {

      long lastProductId = crawler.get().getLastId();

      for (int i = 0; i < 10; i++) {

        String requestId = UUID.randomUUID().toString();
        log.info("{}: Creating event", requestId);

        rabbitTemplate.convertAndSend(
            queue.getName(),
            CreateCrawlerEventFactory.createProductEvent(requestId, seller, lastProductId++));
      }

      crawler.get().setLastId(lastProductId);
      crawlerService.save(crawler.get());

    } else {
      log.warn("No crawler for sellerId: {}", seller.getId());
    }
  }
}
