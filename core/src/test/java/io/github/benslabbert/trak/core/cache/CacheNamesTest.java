package io.github.benslabbert.trak.core.cache;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CacheNamesTest {

  @Test
  public void entityCacheNamesTest() {
    assertEquals("product-cache", CacheNames.PRODUCT_CACHE);
    assertEquals("brand-cache", CacheNames.BRAND_CACHE);
    assertEquals("category-cache", CacheNames.CATEGORY_CACHE);
    assertEquals("crawler-cache", CacheNames.CRAWLER_CACHE);
    assertEquals("price-cache", CacheNames.PRICE_CACHE);
    assertEquals("seller-cache", CacheNames.SELLER_CACHE);
    assertEquals("best-savings-cache", CacheNames.BEST_SAVINGS_CACHE);
  }

  @Test
  public void apiCacheNamesTest() {
    assertEquals("takealot-promotion-cache", CacheNames.TAKEALOT_PROMOTION_CACHE);
  }
}
