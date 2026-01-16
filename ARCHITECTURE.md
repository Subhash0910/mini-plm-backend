# Mini PLM Backend - Architecture Guide

## Overview

This document describes the clean, layered architecture of the Mini PLM Backend following Windchill-like patterns.

## Directory Structure

```
src/main/java/com/sam/mini_plm_backend/
├── common/                          # Shared components across application
│   ├── constant/                    # Global constants
│   ├── dto/                         # Common DTOs (ApiResponse, ErrorResponse)
│   ├── exception/                   # Global exception handling
│   └── util/                        # Shared utilities
│
├── config/                          # Application configuration
│   ├── SecurityConfig.java
│   ├── CorsConfig.java
│   └── JwtConfig.java
│
├── features/                        # Feature-based modules
│   ├── document/                    # Document Management
│   │   ├── controller/              # REST endpoints
│   │   ├── service/                 # Business logic (interface + implementation)
│   │   ├── repository/              # Data access layer
│   │   ├── entity/                  # JPA entities
│   │   ├── dto/                     # Data transfer objects
│   │   └── mapper/                  # Entity-DTO conversions
│   │
│   ├── change/                      # Change Management
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── dto/
│   │   └── mapper/
│   │
│   ├── user/                        # User Management
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   └── dto/
│   │
│   └── audit/                       # Audit Logging
│       ├── controller/
│       ├── service/
│       ├── repository/
│       ├── entity/
│       └── dto/
│
└── security/                        # Security components
    ├── JwtTokenProvider.java
    ├── JwtAuthenticationFilter.java
    ├── CustomUserDetailsService.java
    └── SecurityConstants.java
```

## Architecture Layers

### 1. **Controller Layer** (REST API)
- Handles HTTP requests/responses
- Path: `features/{feature}/controller/`
- Validates input, delegates to service
- Returns standardized `ApiResponse`

```java
@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController { ... }
```

### 2. **Service Layer** (Business Logic)
- Contains all business rules
- Path: `features/{feature}/service/`
- Interface + Implementation pattern
- `@Transactional` for data consistency

```java
public interface DocumentService { ... }

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService { ... }
```

### 3. **Repository Layer** (Data Access)
- Database operations using Spring Data JPA
- Path: `features/{feature}/repository/`
- Query methods and custom queries

```java
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> { ... }
```

### 4. **Entity Layer** (Domain Models)
- JPA entities representing database tables
- Path: `features/{feature}/entity/`
- Contains business rules at entity level

```java
@Entity
@Table(name = "documents")
public class Document { ... }
```

### 5. **DTO Layer** (Data Transfer)
- Transfer data between layers
- Path: `features/{feature}/dto/`
- Separate DTOs for Create, Update, Response

```java
DocumentCreateDTO     // For POST requests
DocumentUpdateDTO     // For PUT requests
DocumentResponseDTO   // For responses
```

### 6. **Mapper Layer** (Conversion)
- Converts between Entity and DTO
- Path: `features/{feature}/mapper/`
- Can use MapStruct for complex mappings

```java
@Component
public class DocumentMapper {
    public DocumentResponseDTO toResponseDTO(Document document) { ... }
}
```

## Common Components

### Constants (`common/constant/AppConstants.java`)
```java
public static final String API_PREFIX = "/api/v1";
public static final String DOC_STATE_DRAFT = "DRAFT";
public static final String DOC_STATE_RELEASED = "RELEASED";
```

### Exception Handling
- `GlobalExceptionHandler`: Central exception handling
- `ResourceNotFoundException`: When resource not found
- `ValidationException`: When validation fails

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(...) { ... }
}
```

### API Response Format
```json
{
  "success": true,
  "message": "Document created successfully",
  "data": { "id": 1, "title": "Doc1" },
  "statusCode": 201,
  "timestamp": 1705428600000
}
```

## Data Flow

```
HTTP Request
    ↓
[Controller] - Validates input, calls service
    ↓
[Service] - Executes business logic
    ↓
[Repository] - Queries database
    ↓
[Entity] - Database operations
    ↓
[Mapper] - Converts to DTO
    ↓
[Controller] - Returns ApiResponse
    ↓
HTTP Response (JSON)
```

## Feature Development Pattern

When adding a new feature (e.g., "BOM"):

1. **Create directory structure**
   ```
   features/bom/
   ├── controller/
   ├── service/
   ├── repository/
   ├── entity/
   ├── dto/
   └── mapper/
   ```

2. **Create Entity**
   ```java
   @Entity
   public class BOM { ... }
   ```

3. **Create Repository**
   ```java
   @Repository
   public interface BOMRepository extends JpaRepository<BOM, Long> { ... }
   ```

4. **Create DTOs**
   ```java
   BOMCreateDTO, BOMUpdateDTO, BOMResponseDTO
   ```

5. **Create Service Interface & Implementation**
   ```java
   public interface BOMService { ... }
   @Service
   public class BOMServiceImpl implements BOMService { ... }
   ```

6. **Create Controller**
   ```java
   @RestController
   @RequestMapping("/api/v1/boms")
   public class BOMController { ... }
   ```

7. **Create Mapper**
   ```java
   @Component
   public class BOMMapper { ... }
   ```

## Best Practices

1. **Single Responsibility Principle**: Each class has one reason to change
2. **Dependency Injection**: Use constructor injection with `@RequiredArgsConstructor`
3. **Transactions**: Use `@Transactional` on service methods modifying data
4. **Logging**: Use `@Slf4j` and log important operations
5. **Validation**: Validate inputs in controller and service
6. **Error Handling**: Always throw meaningful exceptions
7. **Pagination**: Implement pagination for list endpoints
8. **DTOs**: Never expose entities directly in API responses

## Typical Endpoint Examples

### Create Resource (POST)
```
POST /api/v1/documents
Body: DocumentCreateDTO
Response: ApiResponse<DocumentResponseDTO>
```

### Read Resource (GET)
```
GET /api/v1/documents/{id}
Response: ApiResponse<DocumentResponseDTO>
```

### Update Resource (PUT)
```
PUT /api/v1/documents/{id}
Body: DocumentUpdateDTO
Response: ApiResponse<DocumentResponseDTO>
```

### Delete Resource (DELETE)
```
DELETE /api/v1/documents/{id}
Response: ApiResponse<null>
```

### List Resources with Pagination (GET)
```
GET /api/v1/documents?page=0&size=20
Response: ApiResponse<Page<DocumentResponseDTO>>
```

## Configuration Files

Ensure these in `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

## Next Steps

1. Follow this structure for Change feature
2. Follow this structure for User feature
3. Follow this structure for Audit feature
4. Implement feature endpoints
5. Add unit tests for each layer
6. Add integration tests

## Questions?

Refer to individual feature implementations or GlobalExceptionHandler for error handling patterns.
