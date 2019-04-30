package io.github.benslabbert.trak.api.service;

import io.github.benslabbert.trak.api.model.TakealotDailyDeal;
import io.github.benslabbert.trak.api.model.TakealotDailyDealProduct;
import io.github.benslabbert.trak.api.model.TakealotPromotion;
import io.github.benslabbert.trak.api.model.TakealotPromotionsResponse;
import io.github.benslabbert.trak.core.model.Promotion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static io.github.benslabbert.trak.core.cache.CacheNames.TAKEALOT_PROMOTION_CACHE;

@Slf4j
@Service
public class TakealotAPIService {

  private RestTemplate restTemplate = new RestTemplate();

  private Optional<Long> getPromotionId(Promotion promotion) {

    Optional<TakealotPromotion> body = getTakealotPromotion();

    if (body.isEmpty()) return Optional.empty();

    return body.get().getResponseList().stream()
        .filter(f -> f.getDisplayName().equals(promotion.getName()))
        .map(TakealotPromotionsResponse::getPromotionId)
        .findFirst();
  }

  private Optional<TakealotPromotion> getTakealotPromotion() {

    ResponseEntity<TakealotPromotion> resp =
        restTemplate.exchange(
            URI.create("https://api.takealot.com/rest/v-1-8-0/promotions?is_bundle_included=True"),
            HttpMethod.GET,
            null,
            TakealotPromotion.class);

    if (!resp.getStatusCode().is2xxSuccessful()) {
      log.warn("Failed to get Daily Deal promotionId");
      return Optional.empty();
    }

    TakealotPromotion body = resp.getBody();

    if (Objects.isNull(body) || Objects.isNull(body.getResponseList())) return Optional.empty();

    return Optional.of(body);
  }

  private List<Long> getPLIDsOnPromotion(long promotionId) {

    int start = 0;
    List<Long> ids = new ArrayList<>(300);

    while (true) {

      log.info("Getting promotions: {}", promotionId);

      ResponseEntity<TakealotDailyDeal> resp =
          restTemplate.exchange(
              URI.create(
                  "https://api.takealot.com/rest/v-1-8-0/productlines/search?sort=BestSelling%20Descending&rows=100&daily_deals_rows=100&start="
                      + start
                      + "&detail=listing&filter=Available:true&filter=Promotions:"
                      + promotionId),
              HttpMethod.GET,
              null,
              TakealotDailyDeal.class);

      if (Objects.isNull(resp.getBody()) || Objects.isNull(resp.getBody().getResults())) {
        log.warn("Failed to get PLIDs for promotionId: {}", promotionId);
        return Collections.emptyList();
      }

      List<TakealotDailyDealProduct> productLines = resp.getBody().getResults().getProductLines();

      if (productLines.isEmpty()) break;

      ids.addAll(
          productLines.stream()
              .map(TakealotDailyDealProduct::getPlId)
              .collect(Collectors.toList()));

      start += 100;
    }

    return ids;
  }

  @Cacheable(
      value = TAKEALOT_PROMOTION_CACHE,
      key = "#promotion.name",
      unless = "#result == null || #result.empty")
  public List<Long> getPLIDsOnPromotion(Promotion promotion) {

    Optional<Long> promotionId = getPromotionId(promotion);

    if (promotionId.isEmpty()) {
      log.warn("No Daily Deals available");
      return Collections.emptyList();
    }

    return getPLIDsOnPromotion(promotionId.get());
  }
}
