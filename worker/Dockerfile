FROM azul/zulu-openjdk-alpine:11.0.3-jre

ENV JAVA_OPTS="-Xmx128m -Xms128m -XX:+TieredCompilation -Xss256k -Xverify:none -XX:+UseG1GC -XX:+UseStringDeduplication -XX:+CMSClassUnloadingEnabled -XshowSettings:vm -XX:NativeMemoryTracking=summary -XX:+UnlockDiagnosticVMOptions -XX:+PrintNMTStatistics -XX:+HeapDumpOnOutOfMemoryError -noverify"

COPY target/lib lib
ARG JAR_FILE
COPY target/$JAR_FILE app.jar

# Expose for actuator/prometheus
EXPOSE 8082

ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=compose -jar /app.jar
