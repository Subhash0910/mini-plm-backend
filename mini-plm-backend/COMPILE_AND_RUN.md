# Backend - Compile & Run Guide

## 🎯 Quick Start (3 Commands)

### Windows (CMD/PowerShell):
```bash
cd C:\mini-plm\mini-plm-backend\mini-plm-backend
mvn clean
mvn clean install -DskipTests && mvn spring-boot:run
```

### Mac/Linux:
```bash
cd ~/mini-plm/mini-plm-backend/mini-plm-backend
mvn clean
mvn clean install -DskipTests && mvn spring-boot:run
```

---

## 🚀 What Happens

### Step 1: Clean Maven Cache
```bash
mvn clean
```
**Action**: Removes all cached compiled files and triggers fresh compilation

### Step 2: Build & Install
```bash
mvn clean install -DskipTests
```
**Action**: 
- Downloads all Maven dependencies
- Runs Lombok annotation processor (generates getters/setters)
- Compiles Java source code
- Creates JAR file: `target/mini-plm-backend-1.0.0.jar`
- Skips tests (`-DskipTests` flag)

### Step 3: Run Application
```bash
mvn spring-boot:run
```
**Action**:
- Starts Spring Boot embedded Tomcat
- Initializes database
- Loads all beans and services
- Application available at: **http://localhost:8080**

---

## ✅ Successful Build Indicators

### BUILD SUCCESS Output:
```
[INFO] BUILD SUCCESS
[INFO] Total time: 45.123 s
[INFO] Finished at: 2026-01-16T15:40:00+05:30
[INFO] Final Memory: 89M/256M
```

### Application Started Output:
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_|\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.x.x)

2026-01-16T15:40:10+05:30  INFO Started MiniPlmBackendApplication
```

**Backend is RUNNING on: http://localhost:8080** ✅

---

## 🧪 Verify Backend is Running

### Option 1: Browser
Open: http://localhost:8080/health

**Expected Response:**
```json
{
  "status": "UP",
  "timestamp": "2026-01-16T15:40:10+05:30"
}
```

### Option 2: cURL
```bash
curl http://localhost:8080/health
```

### Option 3: Postman
- GET http://localhost:8080/health
- Click Send
- Should see 200 OK with status: UP

---

## 🔌 Test API Endpoints

### Login Endpoint
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
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "user": {
    "id": 1,
    "username": "admin",
    "role": "ADMIN"
  }
}
```

---

## 🛠️ Troubleshooting

### Issue: "Cannot find symbol: method getId()"
**Solution**: Run `mvn clean` to clear cache

### Issue: "BUILD FAILURE"
**Solution**: 
```bash
rmdir /s /q target         (Windows)
rm -rf target              (Mac/Linux)
mvn clean install -DskipTests
```

### Issue: "Port 8080 already in use"
**Solution**:
```bash
# Change port in application.properties
server.port=8081
```

### Issue: "Java 17 or higher required"
**Solution**:
```bash
java -version
# Update Java to JDK 17+ from https://adoptium.net/
```

---

## 📦 Maven Commands Reference

| Command | Purpose |
|---------|----------|
| `mvn clean` | Remove target directory and cached files |
| `mvn compile` | Compile source code only |
| `mvn package` | Create JAR file |
| `mvn install` | Install JAR to local repository |
| `mvn clean install` | Clean + Compile + Package + Install |
| `mvn clean install -DskipTests` | Same but skip test execution |
| `mvn spring-boot:run` | Run embedded server |
| `mvn clean install -U` | Force update all dependencies |

---

## 🎯 Complete Workflow

```bash
# Step 1: Clone/Update
git pull origin main

# Step 2: Navigate
cd C:\mini-plm\mini-plm-backend\mini-plm-backend

# Step 3: Clean
mvn clean

# Step 4: Build
mvn install -DskipTests

# Step 5: Run
mvn spring-boot:run

# Step 6: Verify
curl http://localhost:8080/health
```

---

## 📝 Environment Configuration

Create `.env` file in backend directory:
```
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/mini_plm
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=password
JWT_SECRET=your-secret-key-here
```

---

## ✅ Final Verification Checklist

- [ ] `mvn clean` completes successfully
- [ ] `mvn clean install -DskipTests` shows BUILD SUCCESS
- [ ] Application starts without errors
- [ ] http://localhost:8080/health returns 200 OK
- [ ] Login endpoint works with demo credentials
- [ ] Database connected successfully
- [ ] All logs show no ERROR messages
- [ ] Frontend connects to backend successfully

---

## 📞 Still Having Issues?

Check these files in GitHub:
- [BACKEND_SETUP_FIX.md](BACKEND_SETUP_FIX.md) - Detailed troubleshooting
- [FIXES_APPLIED.md](FIXES_APPLIED.md) - Technical details of fixes
- [BUILD_VERIFICATION.md](BUILD_VERIFICATION.md) - Verification checklist

---

**Status**: Ready to compile and run ✅  
**Last Updated**: January 16, 2026
