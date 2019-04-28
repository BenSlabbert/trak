FROM azul/zulu-openjdk-alpine:11.0.3 as MINIMAL_JRE

RUN { \
        java --version ; \
        echo "jlink version:" && \
        jlink --version ; \
    }

ENV JAVA_MINIMAL=/opt/jre

ADD worker.jar app.jar

# https://stackoverflow.com/questions/53375613/why-is-the-java-11-base-docker-image-so-large-openjdk11-jre-slim
# https://stackoverflow.com/questions/53669151/java-11-application-as-lightweight-docker-image/53669152#53669152
# build modules distribution
ENV BASE_MODULES=java.xml,jdk.unsupported,java.sql,java.naming,java.desktop,java.management,java.security.jgss,java.instrument

RUN jlink \
    --verbose \
    --add-modules \
        $(jdeps --print-module-deps app.jar),$BASE_MODULES \
    --compress 2 \
    --strip-debug \
    --no-header-files \
    --no-man-pages \
    --output "$JAVA_MINIMAL"
 
# Second stage, add only our minimal "JRE" distr and our app
FROM alpine:3.9.3

ENV JAVA_OPTS="-XX:MaxRAM=300M \
               -XX:InitialRAMPercentage=70.0 \
               -XX:MaxRAMPercentage=70.0 \
               -XX:MinRAMPercentage=25.0 \
               -XX:+TieredCompilation \
               -Xss512k \
               -Xverify:none \
               -XX:+UseG1GC \
               -XX:+UseStringDeduplication \
               -XX:+CMSClassUnloadingEnabled \
               -XX:MinHeapFreeRatio=10 \
               -XX:MaxHeapFreeRatio=40 \
               -XshowSettings:vm \
               -XX:NativeMemoryTracking=summary \
               -XX:+UnlockDiagnosticVMOptions \
               -XX:+PrintNMTStatistics \
               -noverify"

ENV JAVA_MINIMAL=/opt/jre
ENV PATH="$PATH:$JAVA_MINIMAL/bin"

COPY --from=MINIMAL_JRE "$JAVA_MINIMAL" "$JAVA_MINIMAL"
COPY lib lib
COPY worker.jar app.jar

# Expose for actuator/prometheus
EXPOSE 8082

ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=compose -jar /app.jar
