package com.trak.api.data;

import com.trak.grpc.HelloRequest;
import com.trak.grpc.HelloResponse;
import com.trak.grpc.HelloServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
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
