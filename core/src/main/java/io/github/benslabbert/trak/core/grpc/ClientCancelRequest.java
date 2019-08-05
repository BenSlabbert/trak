package io.github.benslabbert.trak.core.grpc;

import com.google.rpc.Code;
import com.google.rpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;

public class ClientCancelRequest {
  private ClientCancelRequest() {}

  public static StatusRuntimeException getClientCancelMessage() {
    Status status =
        Status.newBuilder()
            .setCode(Code.CANCELLED.getNumber())
            .setMessage("Request cancelled by client")
            .build();

    return StatusProto.toStatusRuntimeException(status);
  }
}
