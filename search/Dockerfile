FROM openjdk:11-jre

VOLUME /tmp

EXPOSE 50052

COPY target/search.jar search.jar

ENV JAVA_OPTS="-XX:+UnlockExperimentalVMOptions \
               -XX:+UseG1GC \
               -XshowSettings:vm"

ENTRYPOINT echo $JAVA_OPTS && exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=compose -jar ./search.jar
