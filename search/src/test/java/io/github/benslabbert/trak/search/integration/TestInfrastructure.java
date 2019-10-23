package io.github.benslabbert.trak.search.integration;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.util.stream.Stream;

@Slf4j
class TestInfrastructure {

  static final GenericContainer SONIC =
      new GenericContainer<>("valeriansaliou/sonic:v1.2.3")
          .withClasspathResourceMapping("sonic.cfg", "/etc/sonic.cfg", BindMode.READ_ONLY)
          .withNetwork(Network.SHARED)
          .withNetworkAliases("sonic")
          .withLogConsumer(new Slf4jLogConsumer(log).withPrefix("sonic"))
          .withExposedPorts(1491);

  static final GenericContainer RABBITMQ =
      new GenericContainer<>("rabbitmq:3.8.0")
          .withNetwork(Network.SHARED)
          .withNetworkAliases("rabbit")
          .withLogConsumer(new Slf4jLogConsumer(log).withPrefix("rabbit"))
          .withEnv("RABBITMQ_DEFAULT_USER", "trak")
          .withEnv("RABBITMQ_DEFAULT_PASS", "password")
          .withEnv("RABBITMQ_DEFAULT_VHOST", "/")
          .withExposedPorts(5672);

  static {
    Stream.of(SONIC, RABBITMQ).parallel().forEach(GenericContainer::start);
  }
}
