package io.github.benslabbert.trak.entity.rabbitmq.rpc;

import io.github.benslabbert.trak.entity.jpa.Seller;

import java.net.URI;

public class AddProductRPCRequestFactory {

    private AddProductRPCRequestFactory() {
    }

    public static AddProductRPCRequest create(URI uri, Seller seller, long plId) {
        return AddProductRPCRequest.builder().uri(uri).seller(seller).plId(plId).build();
    }
}
