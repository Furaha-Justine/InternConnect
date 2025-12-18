# Docker Quick Start Guide

## 🚀 Quick Commands

### Build and Run Everything
```bash
docker-compose up --build
```

### Run in Background
```bash
docker-compose up -d
```

### View Logs
```bash
docker-compose logs -f app
```

### Stop Services
```bash
docker-compose down
```

### Stop and Remove Data
```bash
docker-compose down -v
```

---

## 📋 Environment Variables

Create a `.env` file (optional):
```bash
POSTGRES_DB=internconnect
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
APP_JWT_SECRET=your-secret-key-minimum-32-characters-long
```

---

## ✅ Verify It's Working

1. **Check containers are running:**
   ```bash
   docker-compose ps
   ```

2. **Access application:**
   - Open: http://localhost:8080
   - Test endpoint: http://localhost:8080/api/internships

3. **Check logs:**
   ```bash
   docker-compose logs app
   ```

---

## 📁 Files Created

- `Dockerfile` - Multi-stage production build
- `docker-compose.yml` - Service orchestration
- `.dockerignore` - Build exclusions
- `DOCKER_GUIDE.md` - Full documentation
- `DOCKER_EXAM_SUMMARY.md` - Exam defense notes

---

## 🎓 For Exam Defense

**Key Points:**
1. Multi-stage build reduces image size
2. Non-root user for security
3. Health checks for monitoring
4. Environment variables for configuration
5. Docker Compose for easy orchestration

**Architecture:**
- PostgreSQL database container
- Spring Boot application container
- Isolated Docker network
- Persistent data volumes

