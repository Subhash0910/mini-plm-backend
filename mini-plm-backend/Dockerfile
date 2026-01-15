# ============================================
# STAGE 1: Build Stage
# ============================================
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /build

# Install build dependencies
RUN apk add --no-cache ca-certificates tzdata

# Copy pom.xml first (layer caching optimization)
COPY mini-plm-backend/pom.xml .
COPY mini-plm-backend/.mvn ./.mvn
COPY mini-plm-backend/mvnw .

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies (layer caching)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY mini-plm-backend/src ./src

# Build application
RUN ./mvnw clean package -DskipTests -q

# ============================================
# STAGE 2: Runtime Stage
# ============================================
FROM eclipse-temurin:17-jre-alpine

# Install runtime dependencies
RUN apk add --no-cache ca-certificates tzdata dumb-init

# Create non-root user for security
RUN addgroup -g 1000 appgroup && \
    adduser -D -u 1000 -G appgroup appuser

WORKDIR /app

# Copy built jar from builder stage
COPY --from=builder /build/target/*.jar app.jar

# Set ownership
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health/liveness || exit 1

# Use dumb-init to handle signals properly
ENTRYPOINT ["/usr/bin/dumb-init", "--"]

# Run application with optimized JVM settings
CMD ["java", "-jar", "-XX:+UseG1GC", "-XX:MaxRAMPercentage=75.0", "-XX:InitialRAMPercentage=50.0", "app.jar"]
