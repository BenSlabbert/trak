package io.github.benslabbert.trak.search;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Slf4j
@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "io.github.benslabbert.trak.search.es.repo")
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
