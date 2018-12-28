package com.trak.engine.job;

import com.trak.entity.jpa.service.PriceService;
import com.trak.entity.jpa.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PriceUpdateJob {

  private final ProductService productService;
  private final PriceService priceService;

  public PriceUpdateJob(ProductService productService, PriceService priceService) {

    this.productService = productService;
    this.priceService = priceService;
  }

  //  @Async
  //  @Scheduled(cron = "0 0 */6 * * ?")
  //  public void job() {
  //
  //    log.debug("Starting job");
  //
  //    try {
  //
  //      for (Product product : productService.findAll()) {
  //
  //        Optional<ProductResponse> response = getProductResponse(product.getApiEndpoint());
  //
  //        if (response.isEmpty()) {
  //          log.warn("Invalid api endpoint");
  //          continue;
  //        }
  //
  //        priceService.save(
  //            Price.builder()
  //                .currentPrice(response.get().getCurrentPrice())
  //                .listedPrice(response.get().getListedPrice())
  //                .productId(product.getId())
  //                .build());
  //      }
  //
  //    } catch (Exception e) {
  //      log.debug("General exception", e);
  //    }
  //
  //    log.debug("Finished job");
  //  }
}
