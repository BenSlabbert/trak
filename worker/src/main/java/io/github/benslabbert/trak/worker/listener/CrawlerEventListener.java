package io.github.benslabbert.trak.worker.listener;

import io.github.benslabbert.trak.entity.jpa.*;
import io.github.benslabbert.trak.entity.jpa.service.BrandService;
import io.github.benslabbert.trak.entity.jpa.service.CategoryService;
import io.github.benslabbert.trak.entity.jpa.service.PriceService;
import io.github.benslabbert.trak.entity.jpa.service.ProductService;
import io.github.benslabbert.trak.entity.rabbit.event.CrawlerEvent;
import io.github.benslabbert.trak.worker.response.ProductResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.github.benslabbert.trak.entity.rabbit.Queue.CRAWLER_QUEUE;

@Slf4j
@Component
@RabbitListener(queues = CRAWLER_QUEUE, containerFactory = "customRabbitListenerContainerFactory")
public class CrawlerEventListener extends ProductRequest {

  private final CategoryService categoryService;
  private final ProductService productService;
  private final PriceService priceService;
  private final BrandService brandService;

  public CrawlerEventListener(
      CategoryService categoryService,
      ProductService productService,
      PriceService priceService,
      BrandService brandService) {

    this.categoryService = categoryService;
    this.productService = productService;
    this.priceService = priceService;
    this.brandService = brandService;
  }

  @RabbitHandler
  public void receive(CrawlerEvent crawlerEvent) {

    log.info("{}: Processing request", crawlerEvent.getRequestId());

    long productId = crawlerEvent.getProductId();
    Seller seller = crawlerEvent.getSeller();

    log.debug("{}: Looking for product with PLID: {}", productId, productId);

    findNewProduct(seller, productId);
  }

  private void findNewProduct(Seller seller, long plId) {

    try {
      String apiUrl =
          "https://api.takealot.com/rest/v-1-8-0/product-details/PLID" + plId + "?platform=desktop";

      Optional<ProductResponse> response = getProductResponse(apiUrl);

      if (response.isEmpty()) return;

      Brand brand = brandService.findByNameEquals(response.get().getProductBrand());

      List<Category> categories =
          categoryService.createCategories(
              response.get().getCategories().stream()
                  .filter(Objects::nonNull)
                  .collect(Collectors.toList()));

      Product product =
          productService.save(
              Product.builder()
                  .name(response.get().getProductName())
                  .url(response.get().getProductUrl().trim())
                  .apiEndpoint(apiUrl)
                  .seller(seller)
                  .brand(brand)
                  .categories(categories)
                  .plId(plId)
                  .sku(response.get().getSKU())
                  .images(
                      response.get().getImageUrls().stream()
                          .map(u -> ProductImage.builder().url(u).build())
                          .collect(Collectors.toList()))
                  .build());

      priceService.save(
          Price.builder()
              .productId(product.getId())
              .listedPrice(response.get().getListedPrice())
              .currentPrice(response.get().getCurrentPrice())
              .build());

    } catch (Exception e) {
      log.debug("General exception", e);
    }
  }
}
