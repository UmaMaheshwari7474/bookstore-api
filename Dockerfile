# Build stage using Maven and Eclipse Temurin Java 21
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build production executable JAR (skipping unit tests during Docker image build)
RUN mvn clean package -DskipTests

# Runtime stage using lightweight Eclipse Temurin Java 21 JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Install curl for health checks
RUN apk --no-cache add curl

# Copy the built artifact from builder stage
COPY --from=builder /app/target/bookstore-api-0.0.1-SNAPSHOT.jar app.jar

# Dynamic port assignment for cloud platforms (Render, Railway, Fly.io)
ENV PORT=8080
EXPOSE 8080

# Start Spring Boot application with production profile and dynamic port
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -Dspring.profiles.active=prod -jar app.jar"]
