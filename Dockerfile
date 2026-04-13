FROM eclipse-temurin:17-jre-alpine

# 创建非root用户运行应用
RUN addgroup -S app && adduser -S app -G app

VOLUME /tmp
ARG JAR_FILE=cola-demo-start/target/*.jar
COPY ${JAR_FILE} app.jar

# 创建日志目录
RUN mkdir -p /app/logs && chown -R app:app /app/logs /tmp

USER app

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", \
    "-XX:+UseG1GC", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "/app.jar"]
