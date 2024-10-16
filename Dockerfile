FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/target/doc-agent-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENV FILE_UPLOAD_DIR=/app/uploads
RUN mkdir -p /app/uploads
ENTRYPOINT ["java", "-jar", "app.jar"]
