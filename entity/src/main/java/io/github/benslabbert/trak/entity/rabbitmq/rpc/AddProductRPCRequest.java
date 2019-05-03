package io.github.benslabbert.trak.entity.rabbitmq.rpc;

import io.github.benslabbert.trak.entity.jpa.Seller;
import io.github.benslabbert.trak.entity.rabbitmq.event.Event;
import lombok.Builder;
import lombok.Data;

import java.net.URI;

@Data
@Builder
public class AddProductRPCRequest implements Event {

  private static final long serialVersionUID = 2570389861140876129L;

  private String requestId;
  private URI uri;
  private Long plId;
  private Seller seller;
}
