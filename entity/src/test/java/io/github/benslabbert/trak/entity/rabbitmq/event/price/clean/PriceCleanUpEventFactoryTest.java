package io.github.benslabbert.trak.entity.rabbitmq.event.price.clean;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PriceCleanUpEventFactoryTest {

  @Test
  public void createTest() {

    PriceCleanUpEvent event = PriceCleanUpEventFactory.create(123L);

    assertNotNull(event);
    assertEquals(123L, event.getProductId());
    assertNotNull(event.getRequestId());
  }
}
