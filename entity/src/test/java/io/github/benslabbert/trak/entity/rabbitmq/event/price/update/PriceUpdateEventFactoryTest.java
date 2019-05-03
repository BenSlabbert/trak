package io.github.benslabbert.trak.entity.rabbitmq.event.price.update;

import io.github.benslabbert.trak.entity.jpa.Product;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PriceUpdateEventFactoryTest {

  @Test
  public void createTest() {
    PriceUpdateEvent event =
        PriceUpdateEventFactory.createPriceUpdateEvent(
            Product.builder().plId(123L).build(), UUID.randomUUID().toString());

    assertNotNull(event);
    assertNotNull(event.getRequestId());
    assertEquals(123L, event.getProduct().getPlId().longValue());
  }
}
