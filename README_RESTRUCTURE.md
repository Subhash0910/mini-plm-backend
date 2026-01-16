# Mini PLM Backend - Clean Architecture Restructuring

## 🎯 Overview

This is a **production-ready, clean architecture restructuring** of the Mini PLM Backend following **Windchill-like patterns**.

## 📁 New Structure

```
✨ RESTRUCTURED LAYOUT (Feature-Based)

src/main/java/com/sam/mini_plm_backend/
├── 🔧 config/                    # Application configuration
├── 🎯 common/                    # Shared components
│   ├── constant/                 # AppConstants.java
│   ├── dto/                      # ApiResponse.java, ErrorResponse.java
│   ├── exception/                # Global exception handling
│   └── util/                     # ValidationUtil.java
├── 🏗️ features/                  # Feature modules
│   ├── document/                 # Document Management Feature
│   │   ├── controller/           # REST API endpoints
│   │   ├── service/              # Business logic (interface + impl)
│   │   ├── repository/           # Data access layer
│   │   ├── entity/               # Database entities
│   │   ├── dto/                  # Data transfer objects
│   │   └── mapper/               # Entity-DTO conversions
│   ├── change/                   # Change Management Feature
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── dto/
│   │   └── mapper/
│   ├── user/                     # User Management Feature
│   ├── audit/                    # Audit Logging Feature
│   └── (add more features here)
├── 🔐 security/                  # Security components
└── MiniPlmBackendApplication.java # Entry point
```

## ✨ Key Features

### 1. **Clean Architecture Pattern**
- ✅ Separation of concerns (Controller → Service → Repository)
- ✅ Independent, testable layers
- ✅ Easy to extend and maintain

### 2. **Feature-Based Organization**
- ✅ Each feature is self-contained
- ✅ Easy for team collaboration
- ✅ Scalable project structure

### 3. **Consistent API Response Format**
```json
{
  "success": true,
  "message": "Document created successfully",
  "data": { "id": 1, "title": "Doc1" },
  "statusCode": 201,
  "timestamp": 1705428600000
}
```

### 4. **Proper DTO Separation**
- `DocumentCreateDTO` - For POST requests
- `DocumentUpdateDTO` - For PUT requests
- `DocumentResponseDTO` - For API responses

### 5. **Global Exception Handling**
- ✅ Consistent error responses
- ✅ Proper HTTP status codes
- ✅ Validation error details

### 6. **Service Interface Pattern**
- ✅ Business logic clearly defined
- ✅ Easy to implement multiple implementations
- ✅ Better testability

## 🚀 Getting Started

### Prerequisites
- Java 11+
- Maven 3.6+
- Spring Boot 2.7+
- PostgreSQL/MySQL

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Subhash0910/mini-plm-backend.git
   cd mini-plm-backend
   ```

2. **Switch to restructured branch**
   ```bash
   git checkout main-restructure
   ```

3. **Update application.properties**
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/mini_plm
   spring.datasource.username=root
   spring.datasource.password=password
   ```

4. **Build the project**
   ```bash
   mvn clean install
   ```

5. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

### Access the API
- Base URL: `http://localhost:8080/api/v1`
- Swagger UI (if configured): `http://localhost:8080/swagger-ui.html`

## 📚 API Endpoints

### Document Management
```
POST   /api/v1/documents                    # Create document
GET    /api/v1/documents                    # List all documents
GET    /api/v1/documents/{id}               # Get document by ID
GET    /api/v1/documents/number/{number}    # Get document by number
GET    /api/v1/documents/state/{state}      # Get documents by state
PUT    /api/v1/documents/{id}               # Update document
POST   /api/v1/documents/{id}/release       # Release document
DELETE /api/v1/documents/{id}               # Delete document
```

### Pagination
All list endpoints support pagination:
```
GET /api/v1/documents?page=0&size=20
```

## 🛠️ Architecture Patterns

### MVC Pattern
```
Request → Controller → Service → Repository → Database
   ↓
 Response ← Mapper ← Entity
```

### Service Interface Pattern
```java
public interface DocumentService {
    DocumentResponseDTO createDocument(DocumentCreateDTO dto, String username);
    DocumentResponseDTO getDocumentById(Long id);
    // ... more methods
}

@Service
public class DocumentServiceImpl implements DocumentService {
    // Implementation
}
```

### DTOs for Different Purposes
```
CreateDTO  → Used in POST requests  → No ID, only create fields
UpdateDTO  → Used in PUT requests   → Optional fields
ResponseDTO → Used in responses     → All fields including ID, timestamps
```

## 📝 Adding a New Feature

Example: Adding a "BOM" (Bill of Materials) feature

1. **Create directory structure**
   ```bash
   mkdir -p src/main/java/com/sam/mini_plm_backend/features/bom/{controller,service,repository,entity,dto,mapper}
   ```

2. **Create Entity** (`BOMEntity.java`)
   ```java
   @Entity
   @Table(name = "bom")
   public class BOM { ... }
   ```

3. **Create Repository** (`BOMRepository.java`)
   ```java
   @Repository
   public interface BOMRepository extends JpaRepository<BOM, Long> { ... }
   ```

4. **Create DTOs**
   - `BOMCreateDTO.java`
   - `BOMUpdateDTO.java`
   - `BOMResponseDTO.java`

5. **Create Mapper** (`BOMMapper.java`)
   ```java
   @Component
   public class BOMMapper { ... }
   ```

6. **Create Service** (`BOMService.java`, `BOMServiceImpl.java`)
   ```java
   public interface BOMService { ... }
   
   @Service
   public class BOMServiceImpl implements BOMService { ... }
   ```

7. **Create Controller** (`BOMController.java`)
   ```java
   @RestController
   @RequestMapping("/api/v1/boms")
   public class BOMController { ... }
   ```

## ✅ Best Practices Implemented

- ✅ **Dependency Injection**: Constructor injection with `@RequiredArgsConstructor`
- ✅ **Logging**: SLF4J with `@Slf4j` annotation
- ✅ **Exception Handling**: Global `@RestControllerAdvice`
- ✅ **Validation**: Input validation in controllers and services
- ✅ **Transactions**: `@Transactional` for data consistency
- ✅ **Pagination**: Proper pagination for list endpoints
- ✅ **API Response**: Consistent response format
- ✅ **Code Reuse**: Common utilities in `common/` package
- ✅ **Separation of Concerns**: Clear layer boundaries

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=DocumentServiceTest

# Run with coverage
mvn test jacoco:report
```

## 📦 Building for Production

```bash
# Clean build
mvn clean package -DskipTests

# Run JAR
java -jar target/mini-plm-backend-1.0.0.jar

# Run with specific profile
java -Dspring.profiles.active=production -jar target/mini-plm-backend-1.0.0.jar
```

## 📚 Documentation

- **[ARCHITECTURE.md](./ARCHITECTURE.md)** - Detailed architecture guide
- **[MIGRATION_GUIDE.md](./MIGRATION_GUIDE.md)** - How to migrate from old structure
- **pom.xml** - Maven dependencies

## 🐛 Troubleshooting

### Issue: Database connection fails
**Solution**: Check `application.properties` database URL and credentials

### Issue: Tests failing
**Solution**: Ensure all dependencies are installed with `mvn clean install`

### Issue: Swagger not showing
**Solution**: Add Springfox dependency to `pom.xml`

## 📊 Project Statistics

- **Features**: Document, Change, User, Audit
- **APIs**: 30+ endpoints
- **Layers**: Controller, Service, Repository, Entity, DTO
- **Exception Handling**: Global and custom exceptions
- **Validation**: Input and business logic validation

## 🤝 Contributing

1. Create a feature branch: `git checkout -b feature/your-feature`
2. Follow the project structure
3. Write tests for new features
4. Submit a pull request

## 📄 License

MIT License - Feel free to use this project for learning and development.

## 📞 Support

For issues or questions:
1. Check the [ARCHITECTURE.md](./ARCHITECTURE.md)
2. Review existing code examples in features
3. Check Spring Boot documentation

---

**Happy Coding! 🚀**
