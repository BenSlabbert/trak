#Trak

Tracking the prices of items on [Takealot](https://www.takealot.com/).

[Takealot](https://www.takealot.com/) is an online retailer based in South Africa.

This project was inspired by the site: [camelcamelcamel](https://camelcamelcamel.com/)

##Running Set Up

###VM Options

* -Dspring.profiles.active=dev

####MySQL

* SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/trak
* SPRING_DATASOURCE_USERNAME=root
* SPRING_DATASOURCE_PASSWORD=root


##Artifacts

* [Docker Images](https://hub.docker.com/u/benjaminslabbert)

##Builds

[Travis-CI](https://travis-ci.org/BenSlabbert/trak)

[![Build Status](https://travis-ci.org/BenSlabbert/trak.svg?branch=master)](https://travis-ci.org/BenSlabbert/trak)

[SonarQube](https://sonarcloud.io/dashboard?id=BenSlabbert_trak)

[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=BenSlabbert_trak&metric=alert_status)](https://sonarcloud.io/dashboard?id=BenSlabbert_trak)

[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=BenSlabbert_trak&metric=coverage)](https://sonarcloud.io/dashboard?id=BenSlabbert_trak)

[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=BenSlabbert_trak&metric=sqale_rating)](https://sonarcloud.io/api/project_badges/measure?project=BenSlabbert_trak&metric=sqale_rating)

##TODO

* add spotiffy docker plugin

##USEFUL

###Minimal JRE

* [Why large base image OpenJDK 11](https://stackoverflow.com/questions/53375613/why-is-the-java-11-base-docker-image-so-large-openjdk11-jre-slim)
* [Minimal image with azul/zulu-openjdk-alpine:11](https://stackoverflow.com/questions/53669151/java-11-application-as-lightweight-docker-image/53669152#53669152)
