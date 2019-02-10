# Trak

Tracking the prices of items on [Takealot](https://www.takealot.com/).

[Takealot](https://www.takealot.com/) is an online retailer based in South Africa.

This project was inspired by the site: [camelcamelcamel](https://camelcamelcamel.com/)

## Running Set Up

### VM Options

* -Dspring.profiles.active=dev

### Environment Variables

#### H2

* SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MYSQL

#### MySQL

* SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/trak
* SPRING_DATASOURCE_USERNAME=root
* SPRING_DATASOURCE_PASSWORD=root


## Artifacts

* [Docker Images](https://hub.docker.com/u/benjaminslabbert)

## Builds

[Travis-CI](https://travis-ci.org/BenSlabbert/trak)

[![Build Status](https://travis-ci.org/BenSlabbert/trak.svg?branch=master)](https://travis-ci.org/BenSlabbert/trak)

[SonarQube](https://sonarcloud.io/dashboard?id=BenSlabbert_trak)

[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=BenSlabbert_trak&metric=alert_status)](https://sonarcloud.io/dashboard?id=BenSlabbert_trak)

[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=BenSlabbert_trak&metric=coverage)](https://sonarcloud.io/dashboard?id=BenSlabbert_trak)

[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=BenSlabbert_trak&metric=sqale_rating)](https://sonarcloud.io/api/project_badges/measure?project=BenSlabbert_trak&metric=sqale_rating)
