package io.github.benslabbert.trak.engine.job;

import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.Seller;
import io.github.benslabbert.trak.entity.jpa.service.ProductService;
import io.github.benslabbert.trak.entity.jpa.service.SellerService;
import io.github.benslabbert.trak.entity.rabbit.event.CreatePriceUpdateEventFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class PriceUpdateJob extends PageOverAll<Seller> implements Runnable {

  private final ProductService productService;
  private final RabbitTemplate rabbitTemplate;
  private final SellerService sellerService;
  private final Queue queue;

  public PriceUpdateJob(
      ProductService productService,
      RabbitTemplate rabbitTemplate,
      SellerService sellerService,
      Queue productQueue) {

    this.productService = productService;
    this.rabbitTemplate = rabbitTemplate;
    this.sellerService = sellerService;
    this.queue = productQueue;
  }

  @Async
  @Override
  @Scheduled(cron = "0 0 0/1 * * ?")
  public void run() {

    log.debug("Starting job");

    try {
      pageOverAll(sellerService.findAll(PageRequest.of(0, 10)));
    } catch (Exception e) {
      log.debug("General exception", e);
    }

    log.debug("Finished job");
  }

  private void createProductPriceUpdateEvents(Seller seller) {

    Page<Product> products = productService.findAll(seller, PageRequest.of(0, 100));

    while (products.hasContent()) {

      for (Product product : products.getContent()) {

        String requestId = UUID.randomUUID().toString();

        log.info("{}: Creating event", requestId);

        rabbitTemplate.convertAndSend(
            queue.getName(),
            CreatePriceUpdateEventFactory.CreatePriceUpdateEvent(product, requestId));
      }

      if (products.hasNext()) {
        products = productService.findAll(seller, products.nextPageable());
      } else {
        break;
      }
    }
  }

  @Override
  Page<Seller> nextPage(Page<Seller> page) {
    return sellerService.findAll(page.nextPageable());
  }

  @Override
  void processItem(Seller item) {
    createProductPriceUpdateEvents(item);
  }
}
