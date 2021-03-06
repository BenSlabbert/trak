package io.github.benslabbert.trak.search;

import io.grpc.*;
import io.grpc.protobuf.services.ProtoReflectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import static io.github.benslabbert.trak.core.rabbitmq.Header.X_MESSAGE_TTL;
import static io.github.benslabbert.trak.core.rabbitmq.Queue.SONIC_INGEST_QUEUE;

@Slf4j
@EnableAsync
@EnableScheduling
@SpringBootApplication
public class SearchApplication {

  private static ThreadPoolTaskExecutor executor;
  private static Server server;

  public static void main(String[] args) {
    try {
      executor = setUpExecutor();
      ConfigurableApplicationContext context = SpringApplication.run(SearchApplication.class, args);
      startGRPCServer(context);
      log.info("Api initialized!");
    } catch (Exception e) {
      log.error("Failed to run application!", e);
    }
  }

  @Bean
  @Primary
  public Executor executor() {
    return executor;
  }

  private static ThreadPoolTaskExecutor setUpExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    executor.setCorePoolSize(2);
    executor.setMaxPoolSize(4);
    executor.setQueueCapacity(500);
    executor.setThreadNamePrefix("TRAK-SEARCH-");
    executor.initialize();

    return executor;
  }

  private static void startGRPCServer(ApplicationContext context)
      throws IOException, InterruptedException {
    log.info("Setting up gRPC server");

    Map<String, BindableService> beansOfType = context.getBeansOfType(BindableService.class);

    ServerBuilder<?> serverBuilder = ServerBuilder.forPort(50052).intercept(addCompression());

    for (Map.Entry<String, BindableService> entry : beansOfType.entrySet()) {
      log.info("Adding gRPC service: {}", entry.getKey());
      serverBuilder.addService(entry.getValue());
    }

    serverBuilder.addService(ProtoReflectionService.newInstance());

    log.info("Starting gRPC server");
    serverBuilder.executor(executor);
    server = serverBuilder.build().start();
    server.awaitTermination();
    addShutdownHook();
  }

  private static ServerInterceptor addCompression() {

    return new ServerInterceptor() {
      @Override
      public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
          ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        call.setCompression("gzip");
        return next.startCall(call, headers);
      }
    };
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

  @Bean
  public Queue sonicIngestQueue() {
    return new Queue(SONIC_INGEST_QUEUE, true, false, false, queueProperties());
  }

  private HashMap<String, Object> queueProperties() {

    HashMap<String, Object> arguments = new HashMap<>();
    arguments.put(X_MESSAGE_TTL, 300000);

    return arguments;
  }
}
