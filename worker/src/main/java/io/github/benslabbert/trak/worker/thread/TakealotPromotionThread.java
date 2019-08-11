package io.github.benslabbert.trak.worker.thread;

import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.PromotionEntity;
import io.github.benslabbert.trak.entity.jpa.Seller;
import io.github.benslabbert.trak.entity.jpa.service.ProductService;
import io.github.benslabbert.trak.entity.jpa.service.PromotionEntityService;
import io.github.benslabbert.trak.entity.jpa.service.SellerService;
import io.github.benslabbert.trak.entity.rabbitmq.rpc.AddProductRPCRequestFactory;
import io.github.benslabbert.trak.worker.model.PromotionIds;
import io.github.benslabbert.trak.worker.model.TakealotPromotionsResponse;
import io.github.benslabbert.trak.worker.rpc.AddProductRPC;
import io.github.benslabbert.trak.worker.service.TakealotAPIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class TakealotPromotionThread extends Thread {

  private final PromotionEntityService promotionEntityService;
  private final TakealotAPIService takealotAPIService;
  private final TakealotPromotionsResponse response;
  private final ProductService productService;
  private final SellerService sellerService;
  private final AddProductRPC addProductRPC;
  private final String requestId;
  private PromotionEntity result;

  @Override
  public void run() {
    String displayName = response.getDisplayName();
    Long promotionId = response.getPromotionId();
    log.info("{}: Found promotion: {} with ID: {}", requestId, displayName, promotionId);

    savePromotion(takealotAPIService.getPLIDsOnPromotion(displayName));
  }

  public Optional<PromotionEntity> getResult() {
    return Optional.ofNullable(result);
  }

  private void savePromotion(PromotionIds onPromotion) {
    if (onPromotion.getPlIDs().isEmpty()) {
      log.info("{}: No plIDs on promotion", onPromotion.getName());
      return;
    }

    List<Product> products = productService.findAllByPLIDsIn(onPromotion.getPlIDs());

    if (products.size() != onPromotion.getPlIDs().size()) {
      log.warn("{}: Not all items are in db, add one by one ...", onPromotion.getName());
      Optional<Seller> seller = sellerService.findByNameEquals("Takealot");

      if (seller.isEmpty()) {
        log.warn("Failed to find Takealot seller!");
        return;
      }

      List<Long> productPLIds =
          products.stream().map(Product::getPlId).collect(Collectors.toList());
      onPromotion.getPlIDs().removeAll(productPLIds);

      for (Long plId : onPromotion.getPlIDs()) {
        Optional<Long> productId =
            Optional.ofNullable(
                addProductRPC.addProduct(
                    AddProductRPCRequestFactory.create(
                        URI.create(getApiUrl(plId)), seller.get(), plId)));

        if (productId.isEmpty()) {
          log.warn("{}: Failed to add product for plId: {}", onPromotion.getName(), plId);
          continue;
        }

        Optional<Product> p = productService.findOne(productId.get());

        if (p.isPresent()) {
          products.add(p.get());
        } else {
          log.warn("{}: N product for id: {}", onPromotion.getName(), productId);
        }
      }
    }

    log.info("{}: Saving products for promotion", onPromotion.getName());

    result =
        promotionEntityService.save(
            onPromotion.getName(), onPromotion.getPromotionId(), onPromotion.getPlIDs());
  }

  private String getApiUrl(long plId) {
    return "https://api.takealot.com/rest/v-1-8-0/product-details/PLID"
        + plId
        + "?platform=desktop";
  }
}
