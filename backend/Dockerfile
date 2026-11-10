# ==========================================
# Stage 1: Build Spring Boot Application
# ==========================================
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Build application JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ==========================================
# Stage 2: Minimal Production JRE Runtime
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create unprivileged user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Create uploads directory with appropriate permissions
RUN mkdir -p /app/uploads/avatars && chown -R appuser:appgroup /app

COPY --from=builder /app/target/*.jar app.jar
RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
