package io.github.benslabbert.trak.core.rabbitmq;

public class RPC {

    private RPC() {
    }

    public static final String ADD_PRODUCT_RPC_QUEUE = "rpc.requests";
    public static final String ADD_PRODUCT_RPC = "add-product-rpc";
    public static final String ADD_PRODUCT_RPC_ROUTING_KEY = "rpc";
}
