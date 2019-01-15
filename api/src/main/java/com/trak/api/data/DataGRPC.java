package com.trak.api.data;

import com.trak.api.proto.HelloRequest;
import com.trak.api.proto.HelloResponse;
import com.trak.api.proto.HelloServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataGRPC extends HelloServiceGrpc.HelloServiceImplBase {

  @Override
  public void hello(HelloRequest request, StreamObserver<HelloResponse> responseObserver) {

    String firstName = request.getFirstName();
    String lastName = request.getLastName();

    log.info("firstName: {}", firstName);
    log.info("lastName: {}", lastName);

    responseObserver.onNext(HelloResponse.newBuilder().setGreeting("greetings").build());
    responseObserver.onCompleted();
  }
}