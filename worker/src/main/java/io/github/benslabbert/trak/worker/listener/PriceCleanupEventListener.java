package io.github.benslabbert.trak.worker.listener;

import io.github.benslabbert.trak.core.pagination.PageOverContent;
import io.github.benslabbert.trak.entity.jpa.Price;
import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.service.PriceService;
import io.github.benslabbert.trak.entity.jpa.service.ProductService;
import io.github.benslabbert.trak.entity.rabbitmq.event.price.clean.PriceCleanUpEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static io.github.benslabbert.trak.core.rabbitmq.Queue.PRICE_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(queues = PRICE_QUEUE)
public class PriceCleanupEventListener extends PageOverContent<Price> {

  private final ProductService productService;
  private final PriceService priceService;

  @Async
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

    long total = Instant.now().toEpochMilli() - start;
    log.info("{}: time to process: {}ms", event.getRequestId(), total);
  }

  private void cleanPrices(Product product) {
    pageOverContent(
        priceService.findAllByProductId(
            product.getId(), PageRequest.of(0, 100, Sort.by(Sort.Direction.ASC, "created"))));
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
