package io.github.benslabbert.trak.api;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;
import java.util.Map;

@Slf4j
@SpringBootApplication
@EnableTransactionManagement
public class ApiApplication {

  private final ApplicationContext context;

  public ApiApplication(ApplicationContext context) {
    this.context = context;
  }

  public static void main(String[] args) {

    try {
      SpringApplication.run(ApiApplication.class, args);
    } catch (Exception e) {
      log.error("Failed to run application!", e);
    }
  }

  @Bean
  public Server server() throws IOException, InterruptedException {

    Map<String, BindableService> beansOfType = context.getBeansOfType(BindableService.class);

    ServerBuilder<?> serverBuilder = ServerBuilder.forPort(50051);

    for (Map.Entry<String, BindableService> entry : beansOfType.entrySet()) {
      serverBuilder.addService(entry.getValue());
    }

    Server server = serverBuilder.build();

    server.start();
    server.awaitTermination();

    return server;
  }
}
