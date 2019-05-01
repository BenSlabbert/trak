package io.github.benslabbert.trak.core.rabbitmq;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HeaderTest {

  @Test
  public void test() {
    assertEquals("x-message-ttl", Header.X_MESSAGE_TTL);
  }
}
