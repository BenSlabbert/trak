package io.github.benslabbert.trak.worker.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "io.github.benslabbert.trak.entity.jpa")
@EnableJpaRepositories(basePackages = "io.github.benslabbert.trak.entity.jpa.repo")
@ComponentScan(basePackages = "io.github.benslabbert.trak.entity.jpa.service")
public class EntityConfig {}
