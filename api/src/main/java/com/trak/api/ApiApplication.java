package com.trak.api;

import com.trak.api.data.DataGRPC;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;

@Slf4j
@SpringBootApplication
@EnableTransactionManagement
public class ApiApplication {

  public static void main(String[] args) throws IOException, InterruptedException {

    try {
      SpringApplication.run(ApiApplication.class, args);
    } catch (Exception e) {
      log.error("Failed to run application!", e);
    }

    Server server = ServerBuilder.forPort(50051).addService(new DataGRPC()).build();

    server.start();
    server.awaitTermination();
  }
}
