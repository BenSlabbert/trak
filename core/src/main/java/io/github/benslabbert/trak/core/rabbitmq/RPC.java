package io.github.benslabbert.trak.core.rabbitmq;

public class RPC {

  public static final String ADD_PRODUCT_RPC_QUEUE = "rpc.requests";
  public static final String ADD_PRODUCT_RPC = "add-product-rpc";
  // todo rename "rpc" to something more meaningful
  public static final String ADD_PRODUCT_RPC_ROUTING_KEY = "rpc";
  private RPC() {}
}
