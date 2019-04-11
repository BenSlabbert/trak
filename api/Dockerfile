FROM openjdk:11-jre

VOLUME /tmp

EXPOSE 50051

COPY target/api.jar api.jar

ENV SPRING_DATASOURCE_URL="jdbc:mysql://rds/trak"

ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions \
               -XX:+UseG1GC \
               -XshowSettings:vm"

ENTRYPOINT echo $JAVA_OPTS && exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=compose -jar ./api.jar
