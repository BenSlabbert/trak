package io.github.benslabbert.trak.engine.job;

import io.github.benslabbert.trak.core.pagination.PageOverAll;
import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.service.ProductService;
import io.github.benslabbert.trak.entity.rabbit.event.price.clean.PriceCleanUpEventFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PriceCleanUpJob extends PageOverAll<Product> implements Runnable {

  private final ProductService productService;
  private final RabbitTemplate rabbitTemplate;
  private final Queue queue;

  public PriceCleanUpJob(
      ProductService productService, RabbitTemplate rabbitTemplate, Queue priceQueue) {

    this.productService = productService;
    this.rabbitTemplate = rabbitTemplate;
    this.queue = priceQueue;
  }

  @Async
  @Override
  //  @Scheduled(cron = "0 0 0 0/1 * ?")
  @Scheduled(initialDelay = 1000L, fixedDelay = Long.MAX_VALUE)
  public void run() {

    log.info("Starting job");

    try {
      pageOverAll(productService.findAll(PageRequest.of(0, 10)));
    } catch (Exception e) {
      log.debug("General exception", e);
    }

    log.debug("Finished job");
  }

  @Override
  protected Page<Product> nextPage(Page<Product> page) {
    return productService.findAll(page.nextPageable());
  }

  @Override
  protected void processItem(Product product) {
    rabbitTemplate.convertAndSend(
        queue.getName(), PriceCleanUpEventFactory.create(product.getId()));
  }
}
