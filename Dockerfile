# ====== STAGE 1: BUILD ======
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Cache de dependências
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -q -e -DskipTests dependency:go-offline

# Build
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -q -e -DskipTests clean package

# Descobre o jar gerado (spring-boot repackage)
RUN ls -l target && \
    cp target/*-SNAPSHOT.jar /app/app.jar || cp target/*.jar /app/app.jar

# ====== STAGE 2: RUNTIME ======
FROM eclipse-temurin:21-jre-alpine

# Usuário não-root
RUN addgroup -S app && adduser -S app -G app
USER app

WORKDIR /app
COPY --from=build /app/app.jar ./app.jar

# Variáveis padrão (podem ser sobrescritas no compose)
ENV JAVA_TOOL_OPTIONS="-XX:+UseG1GC -XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=50 -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8"
ENV SPRING_PROFILES_ACTIVE=compose

# Exponha a porta da app
EXPOSE 8080

# Healthcheck (usa wget do busybox do Alpine)
HEALTHCHECK --interval=15s --timeout=3s --retries=20 \
  CMD wget -qO- http://127.0.0.1:8080/actuator/health/liveness | grep -q '"status":"UP"' || exit 1

ENTRYPOINT ["java","-jar","/app/app.jar"]
