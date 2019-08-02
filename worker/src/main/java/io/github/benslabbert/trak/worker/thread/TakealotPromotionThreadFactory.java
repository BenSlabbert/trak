package io.github.benslabbert.trak.worker.thread;

import io.github.benslabbert.trak.entity.jpa.service.ProductService;
import io.github.benslabbert.trak.entity.jpa.service.PromotionEntityService;
import io.github.benslabbert.trak.entity.jpa.service.SellerService;
import io.github.benslabbert.trak.worker.model.TakealotPromotionsResponse;
import io.github.benslabbert.trak.worker.rpc.AddProductRPC;
import io.github.benslabbert.trak.worker.service.TakealotAPIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TakealotPromotionThreadFactory {

  private final PromotionEntityService promotionEntityService;
  private final TakealotAPIService takealotAPIService;
  private final ProductService productService;
  private final SellerService sellerService;
  private final AddProductRPC addProductRPC;

  public TakealotPromotionThread create(TakealotPromotionsResponse response, String requestId) {
    TakealotPromotionThread thread =
        new TakealotPromotionThread(
            promotionEntityService,
            takealotAPIService,
            productService,
            sellerService,
            addProductRPC,
            response,
            requestId);

    thread.setName(requestId);

    return thread;
  }
}
