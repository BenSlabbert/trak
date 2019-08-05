package io.github.benslabbert.trak.api.config;

import io.github.benslabbert.trak.grpc.ProductServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GRPCServices {

  @Bean
  public ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub() {
    return ProductServiceGrpc.newBlockingStub(
            ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build())
        .withCompression("gzip");
  }
}
