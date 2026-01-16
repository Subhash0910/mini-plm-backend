# Mini PLM Backend - Testing Guide

## Quick Start Testing

### 1. Start the Backend

```bash
cd mini-plm-backend
docker-compose up -d
```

### 2. Verify Services are Running

```bash
# Check backend
curl http://localhost:8080/api/health/status

# Check database
docker exec mini-plm-backend-db psql -U neondb_owner -d neondb -c "SELECT 1;"
```

---

## Testing Authentication

### Default Test Credentials

| Role | Username | Password | Email |
|------|----------|----------|-------|
| Admin | `admin` | `admin123` | admin@company.com |
| Manager | `manager` | `manager123` | manager@company.com |
| User | `user` | `user123` | user@company.com |

### 1. Login Request

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTY5MzU5MTUwMiwiZXhwIjoxNjkzNjc3OTAyfQ.xxx",
  "type": "Bearer",
  "id": 1,
  "username": "admin",
  "email": "admin@company.com",
  "role": "ADMIN"
}
```

**Save the token for subsequent requests:**
```bash
TOKEN="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTY5MzU5MTUwMiwiZXhwIjoxNjkzNjc3OTAyfQ.xxx"
```

---

### 2. Get Current User (Authenticated)

```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Response:**
```json
{
  "id": 1,
  "username": "admin",
  "password": "$2a$10$YZODXTFp15iMq6HWyJ3L3eH0OQbHYVYYRf6pLVuFJONqOYVH8NM5u",
  "email": "admin@company.com",
  "firstName": "Admin",
  "lastName": "User",
  "role": "ADMIN",
  "isActive": true,
  "createdAt": "2026-01-16T08:41:05",
  "updatedAt": "2026-01-16T08:41:05"
}
```

---

### 3. Register New User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "securepassword123"
  }'
```

**Expected Response:**
```json
{
  "message": "User registered successfully"
}
```

---

### 4. Auth Health Check

```bash
curl http://localhost:8080/api/auth/health
```

**Expected Response:**
```json
{
  "message": "Authentication service is healthy"
}
```

---

## Testing Health Endpoints

### 1. Basic Health Status

```bash
curl http://localhost:8080/api/health/status
```

**Expected Response:**
```json
{
  "status": "UP",
  "timestamp": "2026-01-16T08:42:00",
  "service": "Mini PLM Backend",
  "version": "1.0.0"
}
```

### 2. Detailed Health Check

```bash
curl http://localhost:8080/api/health/detailed
```

**Expected Response:**
```json
{
  "status": "UP",
  "timestamp": "2026-01-16T08:42:00",
  "uptime": "Running",
  "database": "Connected",
  "message": "System is healthy and operational"
}
```

### 3. Kubernetes Readiness Probe

```bash
curl http://localhost:8080/api/health/ready
```

**Expected Response:**
```
Ready
```

### 4. Kubernetes Liveness Probe

```bash
curl http://localhost:8080/api/health/live
```

**Expected Response:**
```
Live
```

---

## Testing Actuator Endpoints

### 1. Health Endpoint (Spring Boot Actuator)

```bash
curl http://localhost:8080/api/actuator/health
```

**Expected Response:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "result": 1
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 1099511627776,
        "free": 549755813888,
        "threshold": 10485760
      }
    }
  }
}
```

### 2. Metrics Endpoint

```bash
curl http://localhost:8080/api/actuator/metrics
```

---

## Testing API Documentation (Swagger)

### 1. Open Swagger UI in Browser

```
http://localhost:8080/api/swagger-ui/index.html
```

### 2. API Documentation (JSON)

```bash
curl http://localhost:8080/api/v3/api-docs
```

### 3. API Documentation (YAML)

```bash
curl http://localhost:8080/api/v3/api-docs.yaml
```

---

## Testing Error Scenarios

### 1. Invalid Credentials

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "wrongpassword"
  }'
```

**Expected Response (401 Unauthorized):**
```json
{
  "message": "Invalid username or password",
  "code": "INVALID_CREDENTIALS"
}
```

### 2. Missing Credentials

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{}'
```

**Expected Response (400 Bad Request):**
```json
{
  "timestamp": "2026-01-16T08:42:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed"
}
```

### 3. Access Endpoint Without Token

```bash
curl -X GET http://localhost:8080/api/auth/me
```

**Expected Response (403 Forbidden):**
```json
{
  "error": "Forbidden"
}
```

### 4. Invalid Token

```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer invalidsecrettoken"
```

**Expected Response (403 Forbidden):**
```json
{
  "error": "Forbidden"
}
```

---

## Database Testing

### Connect to PostgreSQL Database

```bash
# Using Docker
docker exec -it mini-plm-backend-db psql -U neondb_owner -d neondb

# Then run SQL commands
SELECT * FROM users;
SELECT * FROM flyway_schema_history;
```

### Check User Records

```sql
SELECT id, username, email, role, is_active, created_at FROM users;
```

### Check Migration History

```sql
SELECT version, description, type, installed_on FROM flyway_schema_history;
```

---

## Performance Testing

### 1. Load Test Login Endpoint

```bash
# Using Apache Bench
ab -n 100 -c 10 -p login.json -T application/json http://localhost:8080/api/auth/login

# Using hey
go install github.com/rakyll/hey@latest
hey -n 100 -c 10 -m POST -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  http://localhost:8080/api/auth/login
```

### 2. Monitor Application Metrics

```bash
# Response time
curl -w "\n%{time_total}\n" http://localhost:8080/api/auth/health

# View logs
docker logs -f mini-plm-backend
```

---

## Docker Compose Validation

### Check Service Status

```bash
docker-compose ps
```

### View Logs

```bash
# Backend logs
docker-compose logs backend

# Database logs
docker-compose logs db

# All logs
docker-compose logs -f
```

### Stop Services

```bash
docker-compose down
```

### Remove All Data (Fresh Start)

```bash
docker-compose down -v
```

---

## Postman Collection

Import this into Postman for easy testing:

```json
{
  "info": {
    "name": "Mini PLM Backend",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Login",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"username\": \"admin\",\n  \"password\": \"admin123\"\n}"
        },
        "url": {
          "raw": "http://localhost:8080/api/auth/login",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "auth", "login"]
        }
      }
    },
    {
      "name": "Get Current User",
      "request": {
        "method": "GET",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{token}}",
            "type": "text"
          }
        ],
        "url": {
          "raw": "http://localhost:8080/api/auth/me",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "auth", "me"]
        }
      }
    },
    {
      "name": "Health Check",
      "request": {
        "method": "GET",
        "url": {
          "raw": "http://localhost:8080/api/health/status",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "health", "status"]
        }
      }
    }
  ]
}
```

---

## Troubleshooting

### Connection Refused
```bash
# Backend not running
docker-compose up -d backend
```

### Database Connection Error
```bash
# Check database logs
docker-compose logs db

# Restart database
docker-compose restart db
```

### Token Expired
```bash
# Get new token by logging in again
curl -X POST http://localhost:8080/api/auth/login ...
```

### Migration Failed
```bash
# Check migration history
docker exec mini-plm-backend-db psql -U neondb_owner -d neondb \
  -c "SELECT * FROM flyway_schema_history;"

# Repair Flyway
# Add to application.properties: spring.flyway.outOfOrder=true
```

---

## Production Readiness Checklist

- [ ] Change JWT secret in environment variables
- [ ] Update CORS origins
- [ ] Enable HTTPS
- [ ] Set logging level to INFO (not DEBUG)
- [ ] Configure database backups
- [ ] Setup monitoring and alerting
- [ ] Change default user passwords
- [ ] Enable rate limiting
- [ ] Setup API versioning
- [ ] Document custom business logic

---

## Contact & Support

For issues or questions:
- Check logs: `docker-compose logs -f`
- Review API docs: http://localhost:8080/api/swagger-ui/index.html
- Test endpoints: Use provided cURL commands

**Created:** 2026-01-16
**Version:** 1.0.0
**Status:** Production Ready
