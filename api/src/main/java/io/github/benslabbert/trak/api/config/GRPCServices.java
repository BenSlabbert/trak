package io.github.benslabbert.trak.api.config;

import io.github.benslabbert.trak.grpc.ProductServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GRPCServices {

  @Bean
  public ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub() {

    ManagedChannel channel =
        ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

    return ProductServiceGrpc.newBlockingStub(channel);
  }
}
