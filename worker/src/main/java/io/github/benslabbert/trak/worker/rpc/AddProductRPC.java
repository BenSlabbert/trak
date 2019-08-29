package io.github.benslabbert.trak.worker.rpc;

import io.github.benslabbert.trak.entity.jpa.*;
import io.github.benslabbert.trak.entity.jpa.service.BrandService;
import io.github.benslabbert.trak.entity.jpa.service.CategoryService;
import io.github.benslabbert.trak.entity.jpa.service.PriceService;
import io.github.benslabbert.trak.entity.jpa.service.ProductService;
import io.github.benslabbert.trak.entity.rabbitmq.rpc.AddProductRPCRequest;
import io.github.benslabbert.trak.worker.response.ProductResponse;
import io.github.benslabbert.trak.worker.util.ProductRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static io.github.benslabbert.trak.core.rabbitmq.RPC.ADD_PRODUCT_RPC_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddProductRPC extends ProductRequest {

  private final CategoryService categoryService;
  private final ProductService productService;
  private final BrandService brandService;
  private final PriceService priceService;

  @Async
  @Nullable
  @RabbitListener(queues = ADD_PRODUCT_RPC_QUEUE)
  public CompletableFuture<Long> addProductAsync(AddProductRPCRequest addProductRPCRequest) {
    return CompletableFuture.completedFuture(addProduct(addProductRPCRequest));
  }

  @Nullable
  public Long addProduct(AddProductRPCRequest addProductRPCRequest) {
    log.info("Adding product: {}", addProductRPCRequest);

    Optional<Product> product = productService.findByPlID(addProductRPCRequest.getPlId());

    if (product.isPresent()) {
      log.info("Product exists for PLID: {}", addProductRPCRequest.getPlId());
      return product.get().getId();
    }

    Optional<ProductResponse> productResponse =
        getProductResponse(addProductRPCRequest.getUri().toString());

    if (productResponse.isPresent()) {
      log.info("Got product: {}", productResponse.get());
      return createProduct(
              addProductRPCRequest.getSeller(),
              addProductRPCRequest.getPlId(),
              addProductRPCRequest.getUri(),
              productResponse.get())
          .getId();
    } else {
      log.warn("Failed to find product at: {}", addProductRPCRequest.getUri());
      return null;
    }
  }

  private Product createProduct(Seller seller, long plId, URI apiUrl, ProductResponse response) {
    log.info("Looking for brand: {}", response.getProductBrand());
    Brand brand = brandService.findByNameEquals(response.getProductBrand());
    log.info("Found brand: {}", brand);

    List<Category> categories = createCategories(response);

    Product product =
        productService.save(
            Product.builder()
                .name(response.getProductName())
                .url(response.getProductUrl().trim())
                .apiEndpoint(apiUrl.toString())
                .sellerId(seller.getId())
                .brandId(brand.getId())
                .categories(categories)
                .plId(plId)
                .sku(response.getSKU())
                .images(getImages(response))
                .build());

    createPrice(response, product);

    return product;
  }

  private void createPrice(ProductResponse response, Product product) {
    priceService.save(
        Price.builder()
            .productId(product.getId())
            .listedPrice(response.getListedPrice())
            .currentPrice(response.getCurrentPrice())
            .build());
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
