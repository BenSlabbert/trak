package io.github.benslabbert.trak.core.rabbitmq;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RPCTest {

  @Test
  public void test() {
    assertEquals("rpc.requests", RPC.ADD_PRODUCT_RPC_QUEUE);
    assertEquals("add-product-rpc", RPC.ADD_PRODUCT_RPC);
    assertEquals("rpc", RPC.ADD_PRODUCT_RPC_ROUTING_KEY);
  }
}
