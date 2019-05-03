package io.github.benslabbert.trak.entity.rabbitmq.rpc;

import io.github.benslabbert.trak.entity.jpa.Seller;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AddProductRPCRequestFactoryTest {

  @Test
  public void createTest() {

    AddProductRPCRequest request =
        AddProductRPCRequestFactory.create(
            URI.create("https://takealot.com"), Seller.builder().name("s1").id(1L).build(), 123L);

    assertNotNull(request);
    assertNotNull(request.getRequestId());
    assertEquals(123L, request.getPlId().longValue());
    assertEquals("https://takealot.com", request.getUri().toString());
    assertEquals("s1", request.getSeller().getName());
  }
}
