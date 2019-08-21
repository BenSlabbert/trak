package io.github.benslabbert.trak.worker.listener;

import io.github.benslabbert.trak.entity.jpa.*;
import io.github.benslabbert.trak.entity.jpa.service.BrandService;
import io.github.benslabbert.trak.entity.jpa.service.CategoryService;
import io.github.benslabbert.trak.entity.jpa.service.PriceService;
import io.github.benslabbert.trak.entity.jpa.service.ProductService;
import io.github.benslabbert.trak.entity.rabbitmq.event.crawler.CrawlerEvent;
import io.github.benslabbert.trak.worker.response.ProductResponse;
import io.github.benslabbert.trak.worker.util.ProductRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.github.benslabbert.trak.core.rabbitmq.Queue.CRAWLER_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(queues = CRAWLER_QUEUE, containerFactory = "customRabbitListenerContainerFactory")
public class CrawlerEventListener extends ProductRequest {

  private final CategoryService categoryService;
  private final ProductService productService;
  private final PriceService priceService;
  private final BrandService brandService;

  @Async
  @RabbitHandler
  public void receive(CrawlerEvent crawlerEvent) {
    log.info("{}: Processing request", crawlerEvent.getRequestId());

    long productId = crawlerEvent.getProductId();
    Seller seller = crawlerEvent.getSeller();

    log.debug("{}: Looking for product with PLID: {}", productId, productId);

    findNewProduct(crawlerEvent.getRequestId(), seller, productId);
  }

  private void findNewProduct(String reqId, Seller seller, long plId) {
    try {
      String apiUrl = getApiUrl(plId);

      Optional<ProductResponse> response = getProductResponse(apiUrl);

      if (response.isEmpty()) {
        log.info("{}: Failed to get product response", reqId);
        return;
      }

      Brand brand;

      if (response.get().isBook()) {
        brand = brandService.findByNameEquals(response.get().getAuthors().get(0).getAuthor());
      } else {
        brand = brandService.findByNameEquals(response.get().getProductBrand());
      }

      List<Category> categories = createCategories(response.get());
      Product product = createProduct(seller, plId, apiUrl, response.get(), brand, categories);
      createPrice(response.get(), product);
    } catch (Exception e) {
      log.debug("General exception", e);
    }
  }

  private String getApiUrl(long plId) {
    return "https://api.takealot.com/rest/v-1-8-0/product-details/PLID"
        + plId
        + "?platform=desktop";
  }

  private void createPrice(ProductResponse response, Product product) {
    priceService.save(
        Price.builder()
            .productId(product.getId())
            .listedPrice(response.getListedPrice())
            .currentPrice(response.getCurrentPrice())
            .build());
  }

  private Product createProduct(
      Seller seller,
      long plId,
      String apiUrl,
      ProductResponse response,
      Brand brand,
      List<Category> categories) {

    Optional<Product> p = productService.findByPlID(plId);

    return p.orElseGet(
        () ->
            productService.save(
                Product.builder()
                    .name(response.getProductName())
                    .url(response.getProductUrl().trim())
                    .apiEndpoint(apiUrl)
                    .sellerId(seller.getId())
                    .brandId(brand.getId())
                    .categories(categories)
                    .plId(plId)
                    .sku(response.getSKU())
                    .images(getImages(response))
                    .build()));
  }

  private List<ProductImage> getImages(ProductResponse response) {
    return response.getImageUrls().stream()
        .map(u -> ProductImage.builder().url(u).build())
        .collect(Collectors.toList());
  }

  private List<Category> createCategories(ProductResponse response) {
    return categoryService.createCategories(
        response.getCategories().stream().filter(Objects::nonNull).collect(Collectors.toList()));
  }
}
