FROM azul/zulu-openjdk-alpine:11.0.3-jre

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
               -XX:+HeapDumpOnOutOfMemoryError \
               -noverify"

COPY lib lib
COPY engine.jar app.jar

# Expose for actuator/prometheus
EXPOSE 8083

ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=compose -jar /app.jar
