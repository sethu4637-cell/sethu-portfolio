# ==============================================================
# J. Sethuraman Portfolio - Production Multi-Stage Dockerfile
# ==============================================================
# 1. Build Stage: Compiles Java code & packages single unified JAR
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Cache dependencies
COPY backend/pom.xml ./backend/
WORKDIR /app/backend
RUN mvn dependency:go-offline -B

# Copy all project files (frontend + backend) so maven-resources-plugin connects everything
WORKDIR /app
COPY index.html ./
COPY admin ./admin/
COPY css ./css/
COPY js ./js/
COPY assets ./assets/
COPY backend/src ./backend/src/

WORKDIR /app/backend
RUN mvn clean package -DskipTests

# 2. Runtime Stage: Lightweight, secure JRE container
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Create a non-root system user for security
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

COPY --from=builder /app/backend/target/portfolio-backend-1.0.0.jar app.jar
RUN chown -R appuser:appgroup /app

USER appuser
EXPOSE 8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
