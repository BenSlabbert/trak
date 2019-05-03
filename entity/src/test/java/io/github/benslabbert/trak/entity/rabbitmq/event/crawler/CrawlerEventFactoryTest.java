package io.github.benslabbert.trak.entity.rabbitmq.event.crawler;

import io.github.benslabbert.trak.entity.jpa.Seller;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CrawlerEventFactoryTest {

  @Test
  public void createTest() {

    CrawlerEvent event =
        CrawlerEventFactory.createCrawlerEvent(
            UUID.randomUUID().toString(), Seller.builder().name("s1").build(), 123L);

    assertNotNull(event);
    assertNotNull(event.getRequestId());
    assertEquals(123L, event.getProductId());
    assertEquals("s1", event.getSeller().getName());
  }
}
