# 🔧 Mini PLM Backend - Build & Run Guide

**Production-Ready Backend Build Instructions**

---

## 💻 System Requirements

- **Java**: JDK 17 or higher
- **Maven**: 3.8.1 or higher
- **MySQL**: 8.0 or higher
- **RAM**: 2GB minimum
- **Disk**: 500MB free space

---

## 🚀 Quick Start (5 minutes)

### 1. Pull Latest Code
```bash
cd C:\mini-plm\mini-plm-backend\mini-plm-backend
git pull origin main
```

### 2. Clean Build
```bash
# Option 1: Skip tests (fastest)
mvn clean install -DskipTests

# Option 2: Include tests (comprehensive)
mvn clean install
```

### 3. Start Application
```bash
mvn spring-boot:run
```

### 4. Verify
```bash
# In another terminal
curl http://localhost:8080/api/health

# Expected Response:
# HTTP 200 OK
```

---

## 🔍 Detailed Build Process

### Step 1: Verify Java Installation
```bash
java -version
# Output: java version "17" or higher

javac -version
# Output: javac 17 or higher
```

### Step 2: Verify Maven Installation
```bash
mvn -version
# Output: Apache Maven 3.8.1 or higher
```

### Step 3: Navigate to Project
```bash
# From Windows PowerShell or Git Bash
cd C:\mini-plm\mini-plm-backend\mini-plm-backend

# Or use full path
cd "C:\Users\YourName\Projects\mini-plm-backend"
```

### Step 4: Clean Previous Build
```bash
# Remove old compiled files
mvn clean

# Verify clean was successful
ls target/  # Should be empty or not exist
```

### Step 5: Compile Code
```bash
# Compile and check for errors
mvn compile

# Expected output:
# [INFO] BUILD SUCCESS
# [INFO] Total time: XX.XXXs
```

### Step 6: Run Tests
```bash
# Option 1: Run all tests
mvn test

# Option 2: Skip tests (faster)
mvn install -DskipTests

# Expected output for tests:
# [INFO] Tests run: XX, Failures: 0, Errors: 0, Skipped: 0
```

### Step 7: Build Package
```bash
# Create JAR file
mvn package -DskipTests

# Verify JAR was created
ls target/*.jar
```

### Step 8: Run Application

**Option A: Using Maven**
```bash
mvn spring-boot:run

# Expected output:
# [INFO] Started Application in X.XXX seconds (JVM running for X.XXX)
# Application is running on: http://localhost:8080
```

**Option B: Using Java Command**
```bash
java -jar target/mini-plm-backend-1.0.0.jar

# Expected output:
# . ____ _ __ _ _
# /\\\ / ___'_ __ _ _(_)_ __ __ _ \ \ \ \\
# ( ( )___ | '_ | '_| | '_ \/ _` | \ \ \ \\
# \\\/  ___)| |_)| | | | | || (_| |  ) ) ) )
#  '  |____| .__|_| |_|_| |_\__, | / / / /
# =========|_|==============|___/=/_/_/_/
# :: Spring Boot ::                (vX.X.X)
# [INFO] Started Application in X.XXX seconds (JVM running for X.XXX)
```

### Step 9: Test API
```bash
# Open another terminal/PowerShell

# Test health endpoint
curl http://localhost:8080/api/health

# Test application is running
# Expected: 200 OK response
```

---

## 💾 Database Configuration

### 1. Create Database (MySQL)
```sql
-- Connect to MySQL
mysql -u root -p

-- Create database
CREATE DATABASE mini_plm_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user (optional)
CREATE USER 'plm_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON mini_plm_db.* TO 'plm_user'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Configure Application (application.properties or application.yml)
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/mini_plm_db
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# Server Configuration
server.port=8080
spring.application.name=Mini PLM Backend
```

---

## 🚠 Troubleshooting

### Issue 1: "Cannot find symbol: method builder()"
**Cause**: Lombok not properly installed  
**Fix**:
```bash
# Clear IDE cache and restart
mvn clean
mvn compile

# Rebuild IDE index (IntelliJ: File > Invalidate Caches)
```

### Issue 2: "Compilation failure"
**Cause**: Maven compiler not using Java 17  
**Fix**:
```bash
# Check Java version
java -version

# If not 17+, update JAVA_HOME
set JAVA_HOME=C:\Program Files\Java\jdk-17

# Verify
echo %JAVA_HOME%
```

### Issue 3: "Port 8080 already in use"
**Cause**: Application already running or port conflict  
**Fix**:
```bash
# Option 1: Kill process using port 8080
lsof -i :8080
kill -9 <PID>

# Option 2: Use different port
set SERVER_PORT=8081
mvn spring-boot:run

# Option 3: In application.properties
server.port=8081
```

### Issue 4: "Database connection failed"
**Cause**: MySQL not running or credentials wrong  
**Fix**:
```bash
# Start MySQL service
net start MySQL80  # Windows
sudo systemctl start mysql  # Linux

# Test connection
mysql -u root -p -h localhost

# Verify database exists
SHOW DATABASES;
```

### Issue 5: "BUILD FAILURE - dependency not found"
**Cause**: Maven cache corrupted  
**Fix**:
```bash
# Clear Maven cache
rmdir /s C:\Users\YourUsername\.m2\repository  # Windows
rm -rf ~/.m2/repository  # Linux/Mac

# Rebuild
mvn clean install -U
```

---

## ✅ Production Deployment Checklist

Before deploying to production:

- [ ] Run `mvn clean install` (with tests)
- [ ] All tests pass (0 failures)
- [ ] JAR file created in `target/` directory
- [ ] Application starts without errors
- [ ] Health endpoint responds with 200 OK
- [ ] Database migrations completed
- [ ] Environment variables configured
- [ ] Logging configured
- [ ] SSL/TLS configured (if needed)
- [ ] Security rules verified
- [ ] API endpoints tested
- [ ] Error handling verified

---

## 📚 Maven Commands Reference

| Command | Purpose |
|---------|----------|
| `mvn clean` | Remove old build files |
| `mvn compile` | Compile source code |
| `mvn test` | Run unit tests |
| `mvn package` | Create JAR/WAR file |
| `mvn install` | Install to local repository |
| `mvn clean install` | Clean + Install (full build) |
| `mvn spring-boot:run` | Run application |
| `mvn dependency:tree` | Show dependency tree |
| `mvn clean install -DskipTests` | Build without tests |
| `mvn clean install -U` | Update snapshots |
| `mvn help:describe -Dplugin=spring-boot` | Plugin help |

---

## 💁 Common Environment Variables

```bash
# Java
JAVA_HOME=C:\Program Files\Java\jdk-17
PATH=%PATH%;%JAVA_HOME%\bin

# Maven
M2_HOME=C:\apache-maven-3.8.1
PATH=%PATH%;%M2_HOME%\bin

# MySQL
MYSQL_HOME=C:\Program Files\MySQL\MySQL Server 8.0
PATH=%PATH%;%MYSQL_HOME%\bin
```

---

## 🚀 Next Steps

1. ✅ **Build Application**
   ```bash
   mvn clean install -DskipTests
   ```

2. ✅ **Start Application**
   ```bash
   mvn spring-boot:run
   ```

3. ✅ **Test API**
   ```bash
   curl http://localhost:8080/api/health
   ```

4. ✅ **Deploy**
   - Docker: `docker build -t mini-plm-backend:1.0.0 .`
   - Cloud: Push JAR to cloud platform
   - Server: Copy JAR and run with `java -jar`

---

## 📞 Help & Support

For issues or questions:
1. Check troubleshooting section above
2. Review logs: `mvn spring-boot:run` console output
3. Check `application.properties` configuration
4. Verify database connection
5. Review error stack traces

---

**Status**: Production Ready ✅  
**Last Updated**: January 16, 2026  
**Maintainer**: Your Team

