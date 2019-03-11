FROM openjdk:11-jre

VOLUME /tmp

EXPOSE 8080
EXPOSE 50051

COPY target/api.jar api.jar

ENV SPRING_DATASOURCE_URL="jdbc:mysql://rds/trak"
ENV SPRING_DATASOURCE_USERNAME=user
ENV SPRING_DATASOURCE_PASSWORD=password

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=compose","-jar","api.jar"]
