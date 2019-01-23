package io.github.benslabbert.trak.api;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;
import java.util.Map;

@Slf4j
@SpringBootApplication
@EnableTransactionManagement
public class ApiApplication {

  public static void main(String[] args) {

    try {
      ConfigurableApplicationContext context = SpringApplication.run(ApiApplication.class, args);
      server(context);
      log.info("Api initialized!");
    } catch (Exception e) {
      log.error("Failed to run application!", e);
    }
  }

  private static void server(ApplicationContext context) throws IOException, InterruptedException {

    log.debug("Setting up gRPC server");

    Map<String, BindableService> beansOfType = context.getBeansOfType(BindableService.class);

    ServerBuilder<?> serverBuilder = ServerBuilder.forPort(50051);

    for (Map.Entry<String, BindableService> entry : beansOfType.entrySet()) {
      log.debug("Adding grPC service: {}", entry.getKey());
      serverBuilder.addService(entry.getValue());
    }

    log.debug("Starting gRPC server");
    Server server = serverBuilder.build();

    server.start();
    server.awaitTermination();
  }
}
