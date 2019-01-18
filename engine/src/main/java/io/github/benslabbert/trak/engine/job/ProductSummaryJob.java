package io.github.benslabbert.trak.engine.job;

import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.service.ProductService;
import io.github.benslabbert.trak.entity.rabbit.event.CreateProductSummaryEventFactory;
import io.github.benslabbert.trak.entity.rabbit.event.ProductSummaryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductSummaryJob implements Runnable {

  private final ProductService productService;

  public ProductSummaryJob(ProductService productService) {
    this.productService = productService;
  }

  @Async
  @Override
  @Scheduled(initialDelay = 0L, fixedDelay = 10000L)
  public void run() {

    log.info("Starting job!");

    for (Product product : productService.findAll()) {
      ProductSummaryEvent event =
          CreateProductSummaryEventFactory.createProductSummaryEvent(product);
      
    }
  }
}
