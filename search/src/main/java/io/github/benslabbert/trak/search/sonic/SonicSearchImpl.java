package io.github.benslabbert.trak.search.sonic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SonicSearchImpl implements SonicSearch {

  private static final String CATEGORY_COLLECTION = "category_collect";
  private static final String CATEGORY_BUCKET = "category_bucket";
  private static final String PRODUCT_COLLECTION = "product_collect";
  private static final String PRODUCT_BUCKET = "product_bucket";
  private static final String BRAND_COLLECTION = "brand_collect";
  private static final String BRAND_BUCKET = "brand_bucket";

  private static final String[] COLLECTIONS =
      new String[] {BRAND_COLLECTION, PRODUCT_COLLECTION, CATEGORY_COLLECTION};
  private static final String[] BUCKETS =
      new String[] {BRAND_BUCKET, PRODUCT_BUCKET, CATEGORY_BUCKET};

  private final ConcurrentMap<String, ConcurrentMap<String, String>> ingestBatchMap;
  private final SonicBlockingObjectPool pool;

  public SonicSearchImpl(SonicBlockingObjectPool pool) {
    this.pool = pool;
    ingestBatchMap = new ConcurrentHashMap<>();
    for (String collection : COLLECTIONS) {
      ingestBatchMap.put(collection, new ConcurrentHashMap<>());
    }
  }

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
    List<String> query = pool.query(collection, bucket, text);
    List<Long> ids = new ArrayList<>();
    for (String q : query) addToIds(ids, q);
    return ids;
  }

  @Override
  public void brandIngest(String id, String text) {
    ingestBatchMap.get(BRAND_COLLECTION).put(id, text);
  }

  @Override
  public void productIngest(String id, String text) {
    ingestBatchMap.get(PRODUCT_COLLECTION).put(id, text);
  }

  @Override
  public void categoryIngest(String id, String text) {
    ingestBatchMap.get(CATEGORY_COLLECTION).put(id, text);
  }

  // following the sonic 10 second tick
  @Scheduled(initialDelay = 1000L, fixedDelay = (1000L * 10L))
  public void ingestAndConsolidateJob() {
    for (int i = 0; i < COLLECTIONS.length; i++) {
      String collection = COLLECTIONS[i];
      String bucket = BUCKETS[i];

      Set<String> keys = ingestBatchMap.get(collection).keySet();
      if (keys.isEmpty()) continue;
      log.info("Sonic ingesting {} values into collection {}", keys.size(), collection);

      List<KV> entries =
          keys.stream()
              .map(k -> new KV(k, ingestBatchMap.get(collection).get(k)))
              .collect(Collectors.toList());

      pool.ingestAndConsolidate(collection, bucket, entries);
      keys.forEach(k -> ingestBatchMap.get(collection).remove(k));
      log.info("Sonic completed ingestion");
    }
  }
}
