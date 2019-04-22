package io.github.benslabbert.trak.worker.listener;

import static io.github.benslabbert.trak.core.rabbitmq.Queue.SAVINGS_QUEUE;

import io.github.benslabbert.trak.core.pagination.PageOverAll;
import io.github.benslabbert.trak.entity.jpa.BestSaving;
import io.github.benslabbert.trak.entity.jpa.Price;
import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.service.BestSavingsService;
import io.github.benslabbert.trak.entity.jpa.service.PriceService;
import io.github.benslabbert.trak.entity.jpa.service.ProductService;
import io.github.benslabbert.trak.worker.model.ProductSavings;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(queues = SAVINGS_QUEUE, containerFactory = "customRabbitListenerContainerFactory")
public class BiggestSavingsListener extends PageOverAll<Product> {

  private final BestSavingsService bestSavingsService;
  private final ProductService productService;
  private final PriceService priceService;

  private final List<ProductSavings> savings = new ArrayList<>();

  @RabbitHandler
  public void processSavingsEvent(String uuid) {

    log.info("{}: Processing biggest savings event", uuid);

    try {
      pageOverAll(productService.findAll(PageRequest.of(0, 1000)));
    } catch (Exception e) {
      log.warn("{}: Failed to process", uuid);
      log.error("", e);
    }

    log.info("Got 100 best savings: {}", savings);

    bestSavingsService.saveAll(
        savings.stream()
            .map(
                f ->
                    BestSaving.builder().productId(f.getProductId()).saving(f.getSavings()).build())
            .collect(Collectors.toList()));

    log.info("{}: Done processing", uuid);
  }

  @Override
  protected Page<Product> nextPage(Page<Product> page) {
    return productService.findAll(page.nextPageable());
  }

  @Override
  protected void processItem(Product item) {

    Optional<Price> latestPrice = priceService.findLatestByProductId(item.getId());

    if (latestPrice.isEmpty()) {
      log.debug("No price for product");
      return;
    } else if (productAvailable(latestPrice.get())) {
      log.debug("Product not available");
      return;
    }

    long currentPrice = latestPrice.get().getCurrentPrice();
    long listedPrice = latestPrice.get().getListedPrice();

    float savingsPercentage = (listedPrice - currentPrice) * 1.0F / listedPrice;

    ProductSavings productSavings = new ProductSavings(item.getId(), savingsPercentage);

    if (savings.isEmpty() || savings.size() < 100) {
      savings.add(productSavings);
      savings.sort(Comparator.comparingDouble(ProductSavings::getSavings));
      return;
    }

    ProductSavings first = savings.get(0);

    if (productSavings.getSavings() > first.getSavings()) {
      savings.remove(first);
      savings.add(productSavings);
      savings.sort(Comparator.comparingDouble(ProductSavings::getSavings));
    } else if (productSavings.getSavings() == first.getSavings() && savings.size() < 100) {
      savings.add(productSavings);
      savings.sort(Comparator.comparingDouble(ProductSavings::getSavings));
    }
  }

  private boolean productAvailable(Price latestPrice) {
    return latestPrice.getCurrentPrice() == 0L || latestPrice.getListedPrice() == 0L;
  }
}
