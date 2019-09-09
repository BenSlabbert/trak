package io.github.benslabbert.trak.api;

import io.github.benslabbert.trak.api.grpc.interceptor.GlobalInterceptor;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;
import java.util.Map;

@Slf4j
@EnableCaching
@SpringBootApplication
@EnableTransactionManagement
public class ApiApplication {

  private static ThreadPoolTaskExecutor executor;
  private static Server server;

  public static void main(String[] args) {
    try {
      executor = setUpExecutor();
      ConfigurableApplicationContext context = SpringApplication.run(ApiApplication.class, args);
      startGRPCServer(context);
      log.info("Api initialized!");
    } catch (Exception e) {
      log.error("Failed to run application!", e);
    }
  }

  private static ThreadPoolTaskExecutor setUpExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(4);
    executor.setQueueCapacity(500);
    executor.setThreadNamePrefix("TRAK-API-");
    executor.initialize();

    return executor;
  }

  private static void startGRPCServer(ApplicationContext context)
      throws IOException, InterruptedException {
    log.info("Setting up gRPC server");

    Map<String, BindableService> beansOfType = context.getBeansOfType(BindableService.class);

    ServerBuilder<?> serverBuilder =
        ServerBuilder.forPort(50051).intercept(new GlobalInterceptor());

    for (Map.Entry<String, BindableService> entry : beansOfType.entrySet()) {
      log.info("Adding gRPC service: {}", entry.getKey());
      serverBuilder.addService(entry.getValue());
    }

    log.info("Starting gRPC server");
    serverBuilder.executor(executor);
    server = serverBuilder.build().start();
    server.awaitTermination();
    addShutdownHook();
  }

  private static void addShutdownHook() {
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                  System.err.println("*** shutting down gRPC server since JVM is shutting down");
                  stop();
                  System.err.println("*** server shut down");
                }));
  }

  private static void stop() {
    if (server != null) server.shutdown();
    if (executor != null) executor.shutdown();
  }
}
