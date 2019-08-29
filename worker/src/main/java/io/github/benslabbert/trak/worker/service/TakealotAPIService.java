package io.github.benslabbert.trak.worker.service;

import io.github.benslabbert.trak.worker.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TakealotAPIService {

  private RestTemplate restTemplate = new RestTemplate();

  private Optional<Long> getPromotionId(String promotionName) {

    Optional<TakealotPromotion> p = getTakealotPromotions();

    if (p.isEmpty()) return Optional.empty();

    return p.get().getResponseList().stream()
        .filter(f -> f.getDisplayName().equals(promotionName))
        .map(TakealotPromotionsResponse::getPromotionId)
        .findFirst();
  }

  public Optional<TakealotPromotion> getTakealotPromotions() {
    ResponseEntity<TakealotPromotion> resp =
        restTemplate.exchange(
            URI.create("https://api.takealot.com/rest/v-1-8-0/promotions?is_bundle_included=True"),
            HttpMethod.GET,
            null,
            TakealotPromotion.class);

    if (!resp.getStatusCode().is2xxSuccessful()) {
      log.warn("Failed to get promotionId");
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

  public PromotionIds getPLIDsOnPromotion(String promotionName) {
    Optional<Long> promotionId = getPromotionId(promotionName);

    if (promotionId.isEmpty()) {
      log.warn("No Deals available");
      return PromotionIds.builder().plIDs(Collections.emptyList()).build();
    }

    return PromotionIds.builder()
        .name(promotionName)
        .promotionId(promotionId.get())
        .plIDs(getPLIDsOnPromotion(promotionId.get()))
        .build();
  }
}
