# ==========================================
# BlueTide Backend — Multi-stage Dockerfile
# ==========================================
# Build:  docker build -t bluetide-back .
# Run:    docker run -p 8080:8080 -e MONGODB_URI=... -e JWT_SECRET=... -e SPRING_PROFILES_ACTIVE=prod bluetide-back

# --- Stage 1: Build ---
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B
COPY src src
RUN ./mvnw clean package -DskipTests -B

# --- Stage 2: Runtime ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Non-root user for security
RUN addgroup -S bluetide && adduser -S bluetide -G bluetide
USER bluetide

COPY --from=builder /build/target/services-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=15s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]

