package io.github.benslabbert.trak.worker.listener;

import io.github.benslabbert.trak.core.model.Promotion;
import io.github.benslabbert.trak.entity.jpa.Product;
import io.github.benslabbert.trak.entity.jpa.Seller;
import io.github.benslabbert.trak.entity.jpa.service.ProductService;
import io.github.benslabbert.trak.entity.jpa.service.PromotionEntityService;
import io.github.benslabbert.trak.entity.jpa.service.SellerService;
import io.github.benslabbert.trak.entity.rabbitmq.event.promotion.PromotionEvent;
import io.github.benslabbert.trak.entity.rabbitmq.event.promotion.PromotionEventFactory;
import io.github.benslabbert.trak.entity.rabbitmq.rpc.AddProductRPCRequestFactory;
import io.github.benslabbert.trak.worker.model.PromotionIds;
import io.github.benslabbert.trak.worker.model.TakealotPromotion;
import io.github.benslabbert.trak.worker.model.TakealotPromotionsResponse;
import io.github.benslabbert.trak.worker.rpc.AddProductRPC;
import io.github.benslabbert.trak.worker.service.TakealotAPIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.github.benslabbert.trak.core.rabbitmq.Queue.PROMOTIONS_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(queues = PROMOTIONS_QUEUE)
public class PromotionEventListener {

  private final PromotionEntityService promotionEntityService;
  private final TakealotAPIService takealotAPIService;
  private final RabbitTemplate rabbitTemplate;
  private final ProductService productService;
  private final SellerService sellerService;
  private final AddProductRPC addProductRPC;
  private final Queue promotionQueue;

  // todo add Message message to method params to check for redelivery and other headers to avoid
  //  reprocessing events
  @Async
  @RabbitHandler
  public void receive(PromotionEvent promotionEvent) {
    long start = Instant.now().toEpochMilli();
    String reqId = promotionEvent.getRequestId();
    Promotion p = promotionEvent.getPromotion();
    log.info("{}: Got promotion event for: {}", reqId, p);

    switch (p.getName()) {
      case Promotion.DAILY_DEAL:
        log.info("{}: Getting Daily Deals", reqId);
        savePromotion(takealotAPIService.getPLIDsOnPromotion(p.getName()));
        break;
      case Promotion.ALL:
        log.info("{}: Getting All Promotions", reqId);
        Optional<TakealotPromotion> allPromotions = takealotAPIService.getTakealotPromotions();

        if (allPromotions.isEmpty()) {
          log.warn("{}: Failed to get all promotions", reqId);
          return;
        }

        for (TakealotPromotionsResponse r : allPromotions.get().getResponseList()) {
          PromotionEvent event =
              PromotionEventFactory.createPromotionEvent(
                  Promotion.create(r.getDisplayName()), UUID.randomUUID().toString());

          log.info("{}: Creating promotion event: {}", reqId, event.getRequestId());
          rabbitTemplate.convertAndSend(promotionQueue.getName(), event);
        }

        break;
      default:
        log.info("{}: Creating promotion: {}", reqId, p.getName());
        savePromotion(takealotAPIService.getPLIDsOnPromotion(p.getName()));
    }

    long total = Instant.now().toEpochMilli() - start;
    log.info("{}: time to process: {}ms", reqId, total);
  }

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

      log.info("Must add PLIds: {}", productPLIds);

      for (Long plId : onPromotion.getPlIDs()) {
        log.info("Before async request");
        Optional<Long> productId = getProductId(seller.get(), plId);
        log.info("After async request");

        if (productId.isEmpty()) {
          log.warn("Failed to add product for plId: {}", plId);
          continue;
        }

        Optional<Product> p = productService.findOne(productId.get());

        if (p.isPresent()) {
          log.info("Adding PLId: {}", p.get().getPlId());
          products.add(p.get());
        } else {
          log.warn("No product for id: {}", productId);
        }
      }
    }

    log.info("{}: Saving products for promotion...", onPromotion.getName());
    promotionEntityService.save(
        onPromotion.getName(), onPromotion.getPromotionId(), onPromotion.getPlIDs());
  }

  private Optional<Long> getProductId(Seller seller, Long plId) {
    try {
      Long res =
          addProductRPC.addProduct(
              AddProductRPCRequestFactory.create(URI.create(getApiUrl(plId)), seller, plId));

      return Optional.ofNullable(res);
    } catch (Exception e) {
      log.warn("General exception while getting value from addProductAsync for plId: " + plId, e);
      return Optional.empty();
    }
  }

  private String getApiUrl(long plId) {
    return "https://api.takealot.com/rest/v-1-8-0/product-details/PLID"
        + plId
        + "?platform=desktop";
  }
}
