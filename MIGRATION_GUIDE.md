# Migration Guide - Restructuring Backend

## Overview

This guide helps you understand the restructuring from the old flat structure to the new feature-based architecture.

## Old Structure → New Structure

### Before (Flat)
```
src/main/java/com/sam/mini_plm_backend/
├── controller/
│   ├── DocumentController.java
│   ├── ChangeController.java
│   └── UserController.java
├── service/
│   ├── DocumentService.java
│   ├── ChangeService.java
│   └── UserService.java
├── repository/
│   ├── DocumentRepository.java
│   ├── ChangeRepository.java
│   └── UserRepository.java
├── entity/
│   ├── Document.java
│   ├── Change.java
│   └── User.java
└── dto/
    ├── DocumentDTO.java
    ├── ChangeDTO.java
    └── UserDTO.java
```

### After (Feature-Based)
```
src/main/java/com/sam/mini_plm_backend/
├── features/
│   ├── document/
│   │   ├── controller/DocumentController.java
│   │   ├── service/DocumentService.java
│   │   ├── repository/DocumentRepository.java
│   │   ├── entity/Document.java
│   │   ├── dto/DocumentCreateDTO.java
│   │   ├── dto/DocumentUpdateDTO.java
│   │   ├── dto/DocumentResponseDTO.java
│   │   └── mapper/DocumentMapper.java
│   ├── change/
│   │   ├── controller/ChangeController.java
│   │   ├── service/ChangeService.java
│   │   ├── repository/ChangeRepository.java
│   │   ├── entity/Change.java
│   │   ├── dto/ChangeCreateDTO.java
│   │   ├── dto/ChangeUpdateDTO.java
│   │   ├── dto/ChangeResponseDTO.java
│   │   └── mapper/ChangeMapper.java
│   └── user/
│       ├── controller/UserController.java
│       ├── service/UserService.java
│       ├── repository/UserRepository.java
│       ├── entity/User.java
│       └── dto/UserCreateDTO.java
└── common/
    ├── dto/ApiResponse.java
    ├── exception/GlobalExceptionHandler.java
    ├── constant/AppConstants.java
    └── util/ValidationUtil.java
```

## Key Changes

### 1. Import Statements

**Before:**
```java
import com.sam.mini_plm_backend.controller.DocumentController;
import com.sam.mini_plm_backend.service.DocumentService;
import com.sam.mini_plm_backend.repository.DocumentRepository;
```

**After:**
```java
import com.sam.mini_plm_backend.features.document.controller.DocumentController;
import com.sam.mini_plm_backend.features.document.service.DocumentService;
import com.sam.mini_plm_backend.features.document.repository.DocumentRepository;
```

### 2. API Response Format

**Before:**
```java
@GetMapping("/{id}")
public Document getDocument(@PathVariable Long id) {
    return documentService.getDocument(id);
}
```

**After:**
```java
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<DocumentResponseDTO>> getDocument(@PathVariable Long id) {
    DocumentResponseDTO result = documentService.getDocumentById(id);
    return ResponseEntity.ok(
        ApiResponse.success("Document retrieved successfully", result, HttpStatus.OK.value())
    );
}
```

### 3. DTOs Separation

**Before:**
```java
public class DocumentDTO {
    private Long id;
    private String title;
    // Mixed purposes - both request and response
}
```

**After:**
```java
// For creation requests
public class DocumentCreateDTO {
    @NotBlank
    private String title;
}

// For update requests
public class DocumentUpdateDTO {
    @Size(max = 200)
    private String title;
}

// For responses
public class DocumentResponseDTO {
    private Long id;
    private String title;
    private LocalDateTime createdDate;
}
```

### 4. Service Pattern

**Before:**
```java
public class DocumentService {
    public Document createDocument(DocumentDTO dto) { ... }
}
```

**After:**
```java
public interface DocumentService {
    DocumentResponseDTO createDocument(DocumentCreateDTO dto, String username);
}

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {
    @Override
    public DocumentResponseDTO createDocument(DocumentCreateDTO dto, String username) { ... }
}
```

### 5. Exception Handling

**Before:**
Scattered try-catch blocks

**After:**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }
}
```

## Step-by-Step Migration

### Step 1: Update pom.xml
```xml
<!-- Already included in new structure -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

### Step 2: Update Import Paths
Replace all old imports with new feature-based paths.

### Step 3: Update Response Format
Wrap all controller responses with `ApiResponse`.

### Step 4: Separate DTOs
Create separate Create, Update, Response DTOs for each feature.

### Step 5: Create Mappers
Implement mappers for Entity ↔ DTO conversions.

### Step 6: Test All Endpoints
```bash
mvn test
```

## Common Tasks

### Adding a New Feature

1. Create directory: `features/{featureName}/`
2. Create subdirectories: controller, service, repository, entity, dto, mapper
3. Implement in order: Entity → Repository → DTOs → Mapper → Service → Controller

### Running Tests
```bash
# All tests
mvn test

# Specific test class
mvn test -Dtest=DocumentServiceTest

# With coverage
mvn test jacoco:report
```

### Building for Production
```bash
# Clean build
mvn clean package -DskipTests

# Run JAR
java -jar target/mini-plm-backend-1.0.0.jar
```

## Troubleshooting

### Issue: Import errors after migration
**Solution:** Update all import statements to use feature-based paths.

### Issue: Tests failing
**Solution:** Update test imports and mock paths.

### Issue: API responses not working
**Solution:** Ensure all controllers wrap responses in `ApiResponse`.

## Benefits of New Structure

✅ **Scalability**: Easy to add new features without touching existing code  
✅ **Maintainability**: Related code grouped together  
✅ **Testability**: Clear boundaries for unit testing  
✅ **Code Reuse**: Common utilities easily shared  
✅ **Team Collaboration**: Multiple developers work on different features independently  
✅ **Performance**: Easier to implement lazy loading  
✅ **Documentation**: Clear structure makes onboarding easier  

## Quick Reference

| Component | Location | Naming Convention |
|-----------|----------|-------------------|
| Controller | `features/{feature}/controller/` | `{Feature}Controller` |
| Service Interface | `features/{feature}/service/` | `{Feature}Service` |
| Service Implementation | `features/{feature}/service/` | `{Feature}ServiceImpl` |
| Repository | `features/{feature}/repository/` | `{Feature}Repository` |
| Entity | `features/{feature}/entity/` | `{Feature}` |
| Create DTO | `features/{feature}/dto/` | `{Feature}CreateDTO` |
| Update DTO | `features/{feature}/dto/` | `{Feature}UpdateDTO` |
| Response DTO | `features/{feature}/dto/` | `{Feature}ResponseDTO` |
| Mapper | `features/{feature}/mapper/` | `{Feature}Mapper` |
| Exception | `common/exception/` | `{Name}Exception` |
| Constant | `common/constant/` | `AppConstants` |
| Utility | `common/util/` | `{Name}Util` |
