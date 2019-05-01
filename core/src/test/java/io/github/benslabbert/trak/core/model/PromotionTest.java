package io.github.benslabbert.trak.core.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PromotionTest {

  @Test
  public void test() {
    Promotion dailyDeal = Promotion.DAILY_DEAL;
    assertEquals("Daily Deal", dailyDeal.getName());
  }
}
