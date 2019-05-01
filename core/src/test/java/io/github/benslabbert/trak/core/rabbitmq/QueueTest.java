package io.github.benslabbert.trak.core.rabbitmq;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class QueueTest {

  @Test
  public void test() {

    assertEquals("crawler", Queue.CRAWLER_QUEUE);
    assertEquals("product", Queue.PRODUCT_QUEUE);
    assertEquals("price", Queue.PRICE_QUEUE);
    assertEquals("savings", Queue.SAVINGS_QUEUE);
  }
}
