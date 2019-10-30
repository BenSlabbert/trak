package io.github.benslabbert.trak.api.config;

import io.github.benslabbert.trak.grpc.ProductServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GRPCServices {

  @Value("${api.host}")
  private String apiHost;

  @Bean
  public ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub() {
    return ProductServiceGrpc.newBlockingStub(
            ManagedChannelBuilder.forAddress(apiHost, 50051).usePlaintext().build())
        .withCompression("gzip");
  }
}
