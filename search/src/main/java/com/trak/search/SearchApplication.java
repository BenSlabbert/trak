package com.trak.search;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.net.InetAddress;

@Slf4j
@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.trak.search.es.repo")
public class SearchApplication {



  public static void main(String[] args) {

    try {
      SpringApplication.run(SearchApplication.class, args);
      log.info("Init done!");
    } catch (Exception e) {
      log.error("Failed to run application!", e);
    }
  }


}
