package io.github.benslabbert.trak.search.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
public class ESTransportClientConfig {

  @Value("${elasticsearch.host}")
  private String esHost;

  @Value("${elasticsearch.port}")
  private int esPort;

  @Value("${elasticsearch.clustername}")
  private String esClusterName;

  @Bean
  @Profile("compose")
  public Client composeClient() throws UnknownHostException {
    return getClient(InetAddress.getByName(esHost));
  }

  @Bean
  @Profile("dev")
  public Client devClient() throws UnknownHostException {
    return getClient(InetAddress.getLocalHost());
  }

  private Client getClient(InetAddress localHost) {

    Settings settings = Settings.builder().put("cluster.name", esClusterName).build();

    TransportClient client = new PreBuiltTransportClient(settings);

    try {
      client.addTransportAddress(new TransportAddress(localHost, esPort));
      return client;
    } catch (Exception e) {
      log.warn("Failed to get es client!", e);
      throw new RuntimeException(e);
    }
  }
}
