# Mini PLM - Production Deployment Guide

## Overview
This guide covers production-ready deployment of the Mini PLM application using Docker and docker-compose.

## Prerequisites
- Docker (v20.10+)
- Docker Compose (v2.0+)
- Git
- 2GB+ RAM available
- PostgreSQL 13+

## Quick Start - Local Development

### 1. Clone and Setup
```bash
# Clone repositories
git clone https://github.com/Subhash0910/mini-plm-backend.git
git clone https://github.com/Subhash0910/mini-plm-frontend.git

# Navigate to frontend (docker-compose is in frontend root)
cd mini-plm-frontend
```

### 2. Environment Configuration
```bash
# Copy environment templates
cp .env.example .env.local
cd ../mini-plm-backend
cp .env.example .env
cd ../mini-plm-frontend
```

### 3. Update Environment Variables
**Frontend (.env.local):**
```bash
REACT_APP_API_BASE_URL=http://localhost:8080/api
REACT_APP_ENV=development
REACT_APP_DEBUG_MODE=true
```

**Backend (.env):**
```bash
DB_HOST=postgres
DB_PORT=5432
DB_NAME=miniplm_db
DB_USER=miniplm_user
DB_PASSWORD=your-secure-password
JWT_SECRET=your-super-secret-key-minimum-32-characters
SPRING_PROFILE=dev
```

### 4. Start Services
```bash
# Start all services (from frontend directory)
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### 5. Access Applications
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **API Documentation**: http://localhost:8080/swagger-ui/index.html
- **Database**: localhost:5432

## Production Deployment

### Option 1: Docker Compose (Recommended)

#### 1. Server Setup
```bash
# SSH into your production server
ssh user@your-server.com

# Install Docker & Docker Compose
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Add user to docker group
sudo usermod -aG docker $USER
```

#### 2. Application Deployment
```bash
# Clone repositories
git clone https://github.com/Subhash0910/mini-plm-backend.git
git clone https://github.com/Subhash0910/mini-plm-frontend.git

# Navigate to frontend
cd mini-plm-frontend

# Create production .env
cat > .env << EOF
DB_HOST=postgres
DB_PORT=5432
DB_NAME=miniplm_db
DB_USER=miniplm_user
DB_PASSWORD=$(openssl rand -base64 32)
JWT_SECRET=$(openssl rand -base64 48)
SPRING_PROFILE=prod
REACT_APP_API_BASE_URL=https://api.yourdomain.com
REACT_APP_ENV=production
REACT_APP_DEBUG_MODE=false
CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com
EOF
```

#### 3. Build and Deploy
```bash
# Build images
docker-compose build

# Start services
docker-compose up -d

# Verify services
docker-compose ps

# View logs
docker-compose logs -f backend
```

#### 4. Database Migrations
```bash
# Flyway migrations run automatically on startup
# To verify:
docker-compose exec backend mysql -u root -p$DB_PASSWORD -e "SHOW TABLES;"
```

### Option 2: Kubernetes Deployment

#### 1. Create Namespace
```bash
kubectl create namespace miniplm
```

#### 2. Create Secrets
```bash
kubectl create secret generic miniplm-secrets \
  --from-literal=db-password=$(openssl rand -base64 32) \
  --from-literal=jwt-secret=$(openssl rand -base64 48) \
  -n miniplm
```

#### 3. Deploy Services
```bash
# Backend
kubectl apply -f k8s/backend-deployment.yaml -n miniplm
kubectl apply -f k8s/backend-service.yaml -n miniplm

# Frontend
kubectl apply -f k8s/frontend-deployment.yaml -n miniplm
kubectl apply -f k8s/frontend-service.yaml -n miniplm

# Database
kubectl apply -f k8s/postgres-statefulset.yaml -n miniplm
kubectl apply -f k8s/postgres-service.yaml -n miniplm
```

#### 4. Verify Deployment
```bash
kubectl get deployments -n miniplm
kubectl get services -n miniplm
kubectl get pods -n miniplm
```

## SSL/TLS Configuration

### Using Let's Encrypt with Nginx

```bash
# Install Certbot
sudo apt-get install certbot python3-certbot-nginx

# Get certificate
sudo certbot certonly --standalone -d yourdomain.com -d www.yourdomain.com

# Configure Nginx
sudo nano /etc/nginx/sites-available/default
```

**Nginx Configuration:**
```nginx
upstream backend {
    server localhost:8080;
}

upstream frontend {
    server localhost:3000;
}

server {
    listen 80;
    server_name yourdomain.com www.yourdomain.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name yourdomain.com www.yourdomain.com;

    ssl_certificate /etc/letsencrypt/live/yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/yourdomain.com/privkey.pem;

    # API requests
    location /api {
        proxy_pass http://backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Frontend
    location / {
        proxy_pass http://frontend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## Monitoring and Maintenance

### Health Checks
```bash
# Backend health
curl https://yourdomain.com/api/actuator/health

# Frontend health
curl https://yourdomain.com/
```

### Logs
```bash
# Backend logs
docker-compose logs --tail=100 backend

# Frontend logs
docker-compose logs --tail=100 frontend

# Database logs
docker-compose logs --tail=100 postgres
```

### Backup Database
```bash
# Backup PostgreSQL
docker-compose exec postgres pg_dump -U miniplm_user miniplm_db > backup.sql

# Restore from backup
cat backup.sql | docker-compose exec -T postgres psql -U miniplm_user miniplm_db
```

### Scaling
```bash
# Scale backend replicas
docker-compose up --scale backend=3 -d
```

## Performance Tuning

### Database Optimization
- Configure connection pooling (Hikari CP)
- Enable query caching
- Index frequently queried fields
- Monitor slow queries

### Application Optimization
- Enable gzip compression
- Implement caching headers
- Use CDN for static assets
- Implement rate limiting

### Server Optimization
- Increase file descriptor limits
- Tune kernel parameters
- Monitor CPU and memory usage
- Use load balancing

## Troubleshooting

### Service Won't Start
```bash
# Check logs
docker-compose logs backend

# Verify configuration
cat .env

# Check port availability
sudo netstat -tulpn | grep 8080
```

### Database Connection Issues
```bash
# Test database connection
docker-compose exec backend nc -zv postgres 5432

# Check database status
docker-compose exec postgres psql -U miniplm_user -c "\l"
```

### API Errors
```bash
# Check API logs
docker-compose logs --tail=50 backend | grep ERROR

# Test API endpoint
curl -v https://yourdomain.com/api/auth/health
```

## Security Best Practices

1. **Secrets Management**
   - Use environment variables for sensitive data
   - Rotate secrets regularly
   - Never commit .env files

2. **Network Security**
   - Enable HTTPS/SSL
   - Configure firewall rules
   - Use VPN for internal communication

3. **Application Security**
   - Keep dependencies updated
   - Enable CORS properly
   - Implement rate limiting
   - Use strong passwords

4. **Database Security**
   - Enable SSL connections
   - Regular backups
   - Limit database user permissions
   - Monitor access logs

## Disaster Recovery

### Backup Strategy
```bash
# Daily backup script
#!/bin/bash
BACKUP_DATE=$(date +%Y%m%d)
docker-compose exec -T postgres pg_dump -U miniplm_user miniplm_db | \
  gzip > /backups/miniplm_$BACKUP_DATE.sql.gz
```

### Recovery Procedure
```bash
# Restore from backup
gunzip < /backups/miniplm_YYYYMMDD.sql.gz | \
  docker-compose exec -T postgres psql -U miniplm_user miniplm_db

# Restart services
docker-compose restart
```

## Support
- Documentation: https://docs.miniplm.com
- Issue Tracker: https://github.com/Subhash0910/mini-plm-backend/issues
- Email: support@miniplm.com
