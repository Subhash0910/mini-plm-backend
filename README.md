# Mini-PLM Backend

Spring Boot REST API for a lightweight Product Lifecycle Management (PLM) system inspired by PTC Windchill concepts.

Deployed on Render using Docker + PostgreSQL.

## Live API
Backend: https://mini-plm-backend.onrender.com

## Tech Stack
- Java • Spring Boot • Maven (mvnw)
- PostgreSQL • Spring Data JPA • Hibernate
- Docker • Render
- REST JSON API

## Run Locally

### 1) Clone
```bash
git clone https://github.com/Subhash0910/mini-plm-backend.git
cd mini-plm-backend/mini-plm-backend
```

### 2) Create local config (not committed)

Create:
```
src/main/resources/application-local.properties
```

Example:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/miniplm
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
```

### 3) Run with local profile
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

API → http://localhost:8080

## Roadmap
- BOM hierarchy
- Change Management (ECR / ECN)

## Author
Subhash
```
