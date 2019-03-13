FROM openjdk:11-jre

VOLUME /tmp

EXPOSE 8080

COPY target/worker.jar worker.jar

ENV SPRING_DATASOURCE_URL="jdbc:mysql://rds/trak"

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=compose","-jar","worker.jar"]
