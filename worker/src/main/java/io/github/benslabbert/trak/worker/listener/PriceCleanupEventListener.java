package io.github.benslabbert.trak.worker.listener;

import io.github.benslabbert.trak.core.pagination.PageOverContent;
import io.github.benslabbert.trak.entity.jpa.Price;
import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.service.PriceService;
import io.github.benslabbert.trak.entity.jpa.service.ProductService;
import io.github.benslabbert.trak.entity.rabbit.event.price.clean.PriceCleanUpEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.github.benslabbert.trak.entity.rabbit.Queue.PRICE_QUEUE;

@Slf4j
@Component
@RabbitListener(queues = PRICE_QUEUE, containerFactory = "customRabbitListenerContainerFactory")
public class PriceCleanupEventListener extends PageOverContent<Price> {

  private final ProductService productService;
  private final PriceService priceService;

  public PriceCleanupEventListener(ProductService productService, PriceService priceService) {

    this.productService = productService;
    this.priceService = priceService;
  }

  @RabbitHandler
  public void receive(PriceCleanUpEvent event) {

    long start = System.currentTimeMillis();
    log.debug("{}: Processing event, productId: {}", event.getRequestId(), event.getProductId());

    Optional<Product> product = productService.findOne(event.getProductId());

    if (product.isEmpty()) {
      log.warn("{}: No product for id: {}", event.getRequestId(), event.getProductId());
      return;
    }

    product.ifPresent(this::cleanPrices);

    long end = System.currentTimeMillis();
    log.info("{}: Time taken to process event: {} (millis)", event.getRequestId(), end - start);
  }

  private void cleanPrices(Product product) {

    Page<Price> prices =
        priceService.findAllByProductId(
            product.getId(), PageRequest.of(0, 100, Sort.by(Sort.Direction.ASC, "created")));

    pageOverContent(prices);
  }

  @Override
  protected Page<Price> nextPage(Page<Price> page) {
    return priceService.findAllByProductId(
        page.getContent().get(0).getProductId(), page.nextPageable());
  }

  @Override
  protected void processContent(List<Price> content) {

    List<Price> removeList = new ArrayList<>();

    for (int i = 0; i < content.size() - 1; i++) {

      boolean hasEqual = false;
      int equalId = 0;

      for (int j = i + 1; j < content.size(); j++) {
        if (Price.equals(content.get(i), content.get(j))) {
          hasEqual = true;
          equalId = j;
          break;
        }
      }

      if (hasEqual) {
        removeList.add(content.get(equalId));
      }
    }

    removeList.forEach(p -> priceService.delete(p.getId()));
  }
}
