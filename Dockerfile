FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven && mvn package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /build/target/banking-transaction-system-1.0.0.jar app.jar
COPY src/main/resources/logback.xml /app/logback.xml
RUN mkdir -p /app/logs
ENTRYPOINT ["java", "-Dlogback.configurationFile=/app/logback.xml", "-jar", "app.jar"]
