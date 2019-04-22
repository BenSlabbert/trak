package io.github.benslabbert.trak.entity.rabbitmq.rpc;

import io.github.benslabbert.trak.entity.jpa.Seller;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.net.URI;

@Data
@Builder
public class AddProductRPCRequest implements Serializable {

    private static final long serialVersionUID = 2570389861140876129L;

    private URI uri;
    private Long plId;
    private Seller seller;
}
