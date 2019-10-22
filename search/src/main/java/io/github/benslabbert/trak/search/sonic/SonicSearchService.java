package io.github.benslabbert.trak.search.sonic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SonicSearchService implements Search {

  private static final String CATEGORY_COLLECTION = "category_collect";
  private static final String CATEGORY_BUCKET = "category_bucket";
  private static final String PRODUCT_COLLECTION = "product_collect";
  private static final String PRODUCT_BUCKET = "product_bucket";
  private static final String BRAND_COLLECTION = "brand_collect";
  private static final String BRAND_BUCKET = "brand_bucket";

  private final SonicBlockingObjectPool pool;

  @Override
  public List<Long> brand(String text) {
    return getIds(BRAND_COLLECTION, BRAND_BUCKET, text);
  }

  private void addToIds(List<Long> ids, String q) {
    try {
      long l = Long.parseLong(q);
      ids.add(l);
    } catch (NumberFormatException e) {
      log.error("Failed to parse string {} as long, skipping from search results", q);
    }
  }

  @Override
  public List<Long> product(String text) {
    return getIds(PRODUCT_COLLECTION, PRODUCT_BUCKET, text);
  }

  @Override
  public List<Long> category(String text) {
    return getIds(CATEGORY_COLLECTION, CATEGORY_BUCKET, text);
  }

  @NotNull
  private List<Long> getIds(String collection, String bucket, String text) {
    try {
      List<String> query = pool.query(collection, bucket, text);
      List<Long> ids = new ArrayList<>();
      for (String q : query) addToIds(ids, q);
      return ids;
    } catch (InterruptedException e) {
      log.warn("Thread interrupted, cannot query Sonic!", e);
      Thread.currentThread().interrupt();
      return Collections.emptyList();
    }
  }
}
