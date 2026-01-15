# Mini PLM Backend - Setup & Deployment Guide

## 📋 Table of Contents

1. [Prerequisites](#prerequisites)
2. [Local Development](#local-development)
3. [Database Setup](#database-setup)
4. [Running the Application](#running-the-application)
5. [API Documentation](#api-documentation)
6. [Docker Deployment](#docker-deployment)
7. [Environment Variables](#environment-variables)
8. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### Required Software

- **Java 17** or higher
- **Maven 3.8+** or use included `mvnw`
- **PostgreSQL 14+**
- **Docker & Docker Compose** (optional, for containerized setup)
- **Git**

### Recommended Tools

- **IntelliJ IDEA** or **VS Code** with Spring Boot extensions
- **Postman** or **Insomnia** for API testing
- **DBeaver** or **pgAdmin** for database management

---

## Local Development

### 1. Clone the Repository

```bash
git clone https://github.com/Subhash0910/mini-plm-backend.git
cd mini-plm-backend
```

### 2. Setup Environment Variables

```bash
cp mini-plm-backend/.env.example mini-plm-backend/.env

# Edit .env with your configuration
# Windows PowerShell / Git Bash:
code mini-plm-backend/.env  # or use your editor
```

### 3. Quick Start with Docker Compose

**This is the easiest way to get started:**

```bash
cd mini-plm-backend
docker-compose up -d
```

This will start:
- **PostgreSQL** on `localhost:5432`
- **pgAdmin** on `localhost:5050`
- **Mini PLM Backend** on `localhost:8080`

### 4. Manual Setup (Without Docker)

#### Step 4.1: Install & Start PostgreSQL

```bash
# macOS (using Homebrew)
brew install postgresql@16
brew services start postgresql@16

# Ubuntu/Debian
sudo apt-get install postgresql postgresql-contrib
sudo systemctl start postgresql

# Windows
# Download from https://www.postgresql.org/download/windows/
```

#### Step 4.2: Create Database

```bash
# Connect to PostgreSQL
psql -U postgres

# Create database and user
CREATE USER plm_user WITH PASSWORD 'plm_password';
CREATE DATABASE mini_plm_db OWNER plm_user;
ALTER USER plm_user CREATEDB;
\q  # Exit psql
```

---

## Database Setup

### Flyway Migrations

Database migrations are automatically applied on startup via **Flyway**.

**Migration files location:** `src/main/resources/db/migration/`

To create a new migration:

```bash
# Create file: V2__Add_New_Table.sql
# Flyway will auto-execute on app startup
```

### Manual Migration (if needed)

```bash
cd mini-plm-backend
./mvnw flyway:migrate
```

---

## Running the Application

### Using Maven

```bash
cd mini-plm-backend

# Build and run
./mvnw clean spring-boot:run

# With specific profile
./mvnw clean spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=local
```

### Using IDE (IntelliJ/VS Code)

1. Open `mini-plm-backend` as Maven project
2. Right-click on `MiniPlmBackendApplication.java`
3. Select "Run"

### Using Built JAR

```bash
cd mini-plm-backend
./mvnw clean package
java -jar target/mini-plm-backend-1.0.0.jar
```

### Default Endpoints

Once running, access:

- **API Base:** `http://localhost:8080/api`
- **Swagger UI:** `http://localhost:8080/api/swagger-ui.html`
- **API Docs:** `http://localhost:8080/api/v3/api-docs`
- **Actuator Health:** `http://localhost:8080/actuator/health`
- **Metrics:** `http://localhost:8080/actuator/prometheus`

---

## API Documentation

### Swagger/OpenAPI

Navigate to: **`http://localhost:8080/api/swagger-ui.html`**

Features:
- ✅ Interactive API exploration
- ✅ Try-it-out functionality
- ✅ JWT authentication support
- ✅ Request/response examples

### Sample API Calls

```bash
# Get Health Status
curl http://localhost:8080/api/actuator/health

# List all parts (requires authentication)
curl -H "Authorization: Bearer <JWT_TOKEN>" \
  http://localhost:8080/api/v1/parts
```

---

## Docker Deployment

### Build Docker Image

```bash
cd mini-plm-backend
docker build -t mini-plm-backend:latest .
```

### Run Container

```bash
docker run -d \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/mini_plm_db \
  -e SPRING_DATASOURCE_USERNAME=plm_user \
  -e SPRING_DATASOURCE_PASSWORD=plm_password \
  --name mini-plm-backend \
  mini-plm-backend:latest
```

### Using Docker Compose (Recommended)

```bash
cd mini-plm-backend

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f backend

# Stop services
docker-compose down
```

### Health Check

```bash
docker-compose exec backend curl http://localhost:8080/actuator/health
```

---

## Environment Variables

### Required Variables

```properties
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/mini_plm_db
SPRING_DATASOURCE_USERNAME=plm_user
SPRING_DATASOURCE_PASSWORD=plm_password

# JWT Security (Change in production!)
APP_JWT_SECRET=your-256-bit-secret-key-change-this-in-production
APP_JWT_EXPIRATION=86400000

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173
```

### Optional Variables

```properties
# Spring
SPRING_PROFILE=local|prod
APP_PORT=8080

# Logging
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_COM_SAM=DEBUG

# PgAdmin (if using docker-compose)
PGADMIN_EMAIL=admin@miniplm.local
PGADMIN_PASSWORD=admin
```

See `.env.example` for complete list.

---

## Troubleshooting

### Database Connection Issues

**Error:** `Connection refused`

```bash
# Check PostgreSQL status
psql -U postgres -h localhost

# If docker-compose, check postgres service
docker-compose ps
docker-compose logs postgres
```

### Port Already in Use

```bash
# Find process using port 8080
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows

# Kill process or change port in application.properties
server.port=8081
```

### Maven Build Issues

```bash
# Clear Maven cache
./mvnw clean
rm -rf ~/.m2/repository/com/sam  # Clear local dependencies

# Rebuild
./mvnw clean install -DskipTests
```

### Flyway Migration Errors

```bash
# Reset migrations (WARNING: drops all data)
./mvnw flyway:clean
./mvnw flyway:migrate

# Or manually reset in PostgreSQL
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO plm_user;
```

### Logs Location

```bash
# Application logs
tail -f logs/application.log

# Error logs
tail -f logs/error.log
```

---

## Development Best Practices

### Code Style

- Use **Lombok** annotations to reduce boilerplate
- Follow **Spring Boot** conventions
- Use **DTOs** for API requests/responses
- Add **@Valid** annotation on input validation

### Testing

```bash
# Run all tests
./mvnw test

# Run specific test
./mvnw test -Dtest=UserServiceTest

# Run with coverage
./mvnw test jacoco:report
```

### Security

- ✅ Always use HTTPS in production
- ✅ Rotate JWT secret regularly
- ✅ Use environment variables for sensitive data
- ✅ Enable audit logging for changes
- ✅ Implement rate limiting

---

## Support & Resources

- **Spring Boot Documentation:** https://spring.io/projects/spring-boot
- **PostgreSQL Docs:** https://www.postgresql.org/docs/
- **JWT Handbook:** https://tools.ietf.org/html/rfc7519
- **GitHub Issues:** Report bugs on project issues

---

**Last Updated:** January 2026  
**Version:** 1.0.0
