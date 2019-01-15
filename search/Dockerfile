FROM openjdk:11-jre

VOLUME /tmp

EXPOSE 8080

COPY target/search.jar search.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=compose","-jar","search.jar"]
