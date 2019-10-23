package io.github.benslabbert.trak.search.integration;

import io.github.benslabbert.trak.core.rabbitmq.Queue;
import io.github.benslabbert.trak.entity.rabbitmq.event.sonic.Collection;
import io.github.benslabbert.trak.entity.rabbitmq.event.sonic.IngestEventFactory;
import io.github.benslabbert.trak.grpc.SearchRequest;
import io.github.benslabbert.trak.grpc.SearchResponse;
import io.github.benslabbert.trak.grpc.SearchServiceGrpc;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Slf4j
public class IT extends TestInfrastructure {

  private static SearchServiceGrpc.SearchServiceBlockingStub searchService;

  @ClassRule
  public static final GenericContainer SEARCH =
      new GenericContainer<>("benjaminslabbert/trak_search:dev")
          .withEnv("SONIC_HOST", "sonic")
          .withEnv("SONIC_PORT", "1491")
          .withEnv("SONIC_PASSWORD", "passwd")
          .withEnv("SONIC_POOL_SIZE", "2")
          .withEnv("SONIC_TIMEOUT_CONN", "5000")
          .withEnv("SONIC_TIMEOUT_READ", "5000")
          .withNetwork(Network.SHARED)
          .withLogConsumer(new Slf4jLogConsumer(log).withPrefix("search"))
          .withExposedPorts(50052);

  @BeforeClass
  public static void init() throws InterruptedException {
    searchService =
        SearchServiceGrpc.newBlockingStub(
                ManagedChannelBuilder.forAddress(
                        SEARCH.getContainerIpAddress(), SEARCH.getMappedPort(50052))
                    .usePlaintext()
                    .build())
            .withCompression("gzip");

    CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
    connectionFactory.setPassword("password");
    connectionFactory.setUsername("trak");
    connectionFactory.setHost(RABBITMQ.getContainerIpAddress());
    connectionFactory.setPort(RABBITMQ.getMappedPort(5672));

    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

    rabbitTemplate.convertAndSend(
        Queue.SONIC_INGEST_QUEUE,
        IngestEventFactory.createIngestEvent(
            Collection.BRAND, "1", "brand", UUID.randomUUID().toString()));

    rabbitTemplate.convertAndSend(
        Queue.SONIC_INGEST_QUEUE,
        IngestEventFactory.createIngestEvent(
            Collection.CATEGORY, "2", "category", UUID.randomUUID().toString()));

    rabbitTemplate.convertAndSend(
        Queue.SONIC_INGEST_QUEUE,
        IngestEventFactory.createIngestEvent(
            Collection.PRODUCT, "3", "product", UUID.randomUUID().toString()));
    // block until ingesting task is run
    Thread.sleep(1000L * 10L);
  }

  @AfterClass
  public static void cleanUp() {
    log.info("Stopping search container");
    SEARCH.stop();
    log.info("Stopping all external infrastructure");
    stopAll();
  }

  @Test
  public void test() {
    SearchResponse searchResponse =
        searchService.brandSearch(SearchRequest.newBuilder().setSearch("brand").build());

    assertNotNull(searchResponse);
    assertEquals(1, searchResponse.getResultsList().size());
    assertEquals("1", searchResponse.getResultsList().get(0).getId());

    searchResponse =
        searchService.categorySearch(SearchRequest.newBuilder().setSearch("category").build());

    assertNotNull(searchResponse);
    assertEquals(1, searchResponse.getResultsList().size());
    assertEquals("2", searchResponse.getResultsList().get(0).getId());

    searchResponse =
        searchService.productSearch(SearchRequest.newBuilder().setSearch("product").build());

    assertNotNull(searchResponse);
    assertEquals(1, searchResponse.getResultsList().size());
    assertEquals("3", searchResponse.getResultsList().get(0).getId());
  }
}
