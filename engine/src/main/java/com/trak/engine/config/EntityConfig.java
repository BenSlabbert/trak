package com.trak.engine.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.trak.entity.jpa.repo")
@EntityScan(basePackages = "com.trak.entity.jpa")
@ComponentScan(basePackages = "com.trak.entity.jpa.service")
public class EntityConfig {}
