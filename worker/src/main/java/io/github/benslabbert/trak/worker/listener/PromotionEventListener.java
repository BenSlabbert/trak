package io.github.benslabbert.trak.worker.listener;

import io.github.benslabbert.trak.core.model.Promotion;
import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.Seller;
import io.github.benslabbert.trak.entity.jpa.service.ProductService;
import io.github.benslabbert.trak.entity.jpa.service.PromotionEntityService;
import io.github.benslabbert.trak.entity.jpa.service.SellerService;
import io.github.benslabbert.trak.entity.rabbitmq.event.promotion.PromotionEvent;
import io.github.benslabbert.trak.entity.rabbitmq.rpc.AddProductRPCRequestFactory;
import io.github.benslabbert.trak.worker.model.PromotionIds;
import io.github.benslabbert.trak.worker.model.TakealotPromotion;
import io.github.benslabbert.trak.worker.model.TakealotPromotionsResponse;
import io.github.benslabbert.trak.worker.rpc.AddProductRPC;
import io.github.benslabbert.trak.worker.service.TakealotAPIService;
import io.github.benslabbert.trak.worker.thread.TakealotPromotionThread;
import io.github.benslabbert.trak.worker.thread.TakealotPromotionThreadFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.github.benslabbert.trak.core.rabbitmq.Queue.PROMOTIONS_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(
    queues = PROMOTIONS_QUEUE,
    containerFactory = "customRabbitListenerContainerFactory")
public class PromotionEventListener {

  private final PromotionEntityService promotionEntityService;
  private final TakealotPromotionThreadFactory threadFactory;
  private final TakealotAPIService takealotAPIService;
  private final ProductService productService;
  private final SellerService sellerService;
  private final AddProductRPC addProductRPC;

  // todo add Message message to method params to check for redelivery and other headers to avoid
  // reprocessing enevts
  @RabbitHandler
  public void receive(PromotionEvent promotionEvent) throws InterruptedException {
    log.info(
        "{}: Got promotion event for: {}",
        promotionEvent.getRequestId(),
        promotionEvent.getPromotion());

    if (promotionEvent.getPromotion().equals(Promotion.DAILY_DEAL)) {
      log.info("{}: Getting Daily Deals", promotionEvent.getRequestId());

      getDailyDeals(promotionEvent.getPromotion());
    } else if (promotionEvent.getPromotion().equals(Promotion.ALL)) {
      log.info("{}: Getting All Promotions", promotionEvent.getRequestId());

      Optional<TakealotPromotion> takealotPromotion = takealotAPIService.getTakealotPromotion();

      if (takealotPromotion.isEmpty()) {
        log.warn("{}: Failed to get promotions", promotionEvent.getRequestId());
        return;
      }

      List<TakealotPromotionThread> threads = new ArrayList<>();

      for (TakealotPromotionsResponse r : takealotPromotion.get().getResponseList()) {
        threads.add(threadFactory.create(r, promotionEvent.getRequestId()));
      }

      for (Thread thread : threads) {
        log.info("{}: Starting promotion thread", promotionEvent.getRequestId());
        thread.start();
      }

      log.info("{}: Waiting for all worker threads to finish", promotionEvent.getRequestId());

      for (Thread thread : threads) {
        thread.join();
      }

      for (TakealotPromotionThread thread : threads) {
        if (thread.getResult() != null) {
          log.info(
              "{}: Created promotion: {} DB ID: {}",
              promotionEvent.getRequestId(),
              thread.getResult().getTakealotPromotionId(),
              thread.getResult().getId());
        } else {
          log.warn("{}: Failed to created promotion!", promotionEvent.getRequestId());
        }
      }

      log.info("{}: Done!", promotionEvent.getRequestId());
    }
  }

  private void getDailyDeals(Promotion promotion) {
    savePromotion(takealotAPIService.getPLIDsOnPromotion(promotion));
  }

  // todo: fix duplicate code
  private void savePromotion(PromotionIds onPromotion) {
    if (onPromotion.getPlIDs().isEmpty()) {
      log.info("No plIDs on promotion");
      return;
    }

    List<Product> products = productService.findAllByPLIDsIn(onPromotion.getPlIDs());

    if (products.size() != onPromotion.getPlIDs().size()) {
      log.warn("Not all Daily Deal items are in db, add one by one ...");
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
          log.warn("Failed to add product for plId: {}", plId);
          continue;
        }

        Optional<Product> p = productService.findOne(productId.get());

        if (p.isPresent()) {
          products.add(p.get());
        } else {
          log.warn("No product for id: {}", productId);
        }
      }
    }

    promotionEntityService.save(
        onPromotion.getName(), onPromotion.getPromotionId(), onPromotion.getPlIDs());
  }

  private String getApiUrl(long plId) {
    return "https://api.takealot.com/rest/v-1-8-0/product-details/PLID"
        + plId
        + "?platform=desktop";
  }
}