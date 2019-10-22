package io.github.benslabbert.trak.search.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class SonicConfig {

  @Value("${sonic.host}")
  private String address;

  @Value("${sonic.port}")
  private Integer port;

  @Value("${sonic.password}")
  private String password;

  @Value("${sonic.timeout.connection}")
  private Integer connectionTimeout;

  @Value("${sonic.timeout.read}")
  private Integer readTimeout;

  @Value("${sonic.pool.size}")
  private Integer poolSize;
}
