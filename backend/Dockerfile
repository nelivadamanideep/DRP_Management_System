# ---- Build stage ------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace

# Copy Maven descriptor first so the layer is cached across code changes.
COPY pom.xml .
RUN mvn -B -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -q -DskipTests package

# ---- Runtime stage ----------------------------------------------------------
FROM eclipse-temurin:21-jre

# Non-root user for a smaller attack surface.
RUN useradd -m -u 10001 erpms && mkdir -p /var/lib/erpms/storage && chown -R erpms:erpms /var/lib/erpms

WORKDIR /opt/erpms
COPY --from=build /workspace/target/erpms-backend-*.jar app.jar

USER erpms

EXPOSE 8080

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

HEALTHCHECK --interval=30s --timeout=5s --retries=6 --start-period=45s \
  CMD wget --spider -q http://localhost:8080/api/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
