# SkillSync — Docker Setup & Testing Guide

## What's in This Package

```
skillsync/
├── docker-compose.yml          ← Main orchestration file (FIXED)
├── init-db.sql                 ← Auto-creates all 6 PostgreSQL databases
├── authservice/
│   ├── Dockerfile.authservice  ← FIXED: multi-stage build (no mvn needed locally)
│   └── src/main/resources/application.properties  ← FIXED
├── sessionservice/             ← FIXED (RabbitMQ port, Zipkin URL)
├── skillservice/               ← FIXED
├── reviewservice/              ← FIXED
├── groupservice/               ← FIXED
├── notificationservice/        ← FIXED (RabbitMQ port, Zipkin URL)
├── eurekaserver/               ← FIXED
└── apigateway/                 ← FIXED
```

---

## What Was Fixed

| Problem | Fix Applied |
|---|---|
| Dockerfiles needed pre-built JARs | Multi-stage Dockerfiles — Docker builds the JAR itself using Maven inside the container. You don't need Maven installed. |
| Zipkin URL hardcoded to `localhost` | Each service gets `MANAGEMENT_ZIPKIN_TRACING_ENDPOINT: http://zipkin:9411/api/v2/spans` via docker-compose env |
| RabbitMQ port `5673` in properties | Changed to `5672` (internal Docker port). The host-side `5673:5672` mapping is kept in compose. |
| No database creation step | `init-db.sql` mounted into postgres container — creates all 6 DBs automatically on first start |
| Services start before infra is ready | Added `healthcheck` + `condition: service_healthy` on all `depends_on` entries |
| `groupservice` using hostname instead of IP | Changed to `eureka.instance.prefer-ip-address=true` |

---

## Prerequisites

You only need:
- **Docker Desktop** (includes Docker Compose) — https://www.docker.com/products/docker-desktop
- At least **8 GB RAM** allocated to Docker (all services + infra)
- Ports free: 5432, 5672/5673, 6379, 8080-8086, 8761, 9411, 15672

---

## Step 1 — Unzip and enter the folder

```bash
unzip skillsync.zip
cd skillsync
```

---

## Step 2 — Build and Start Everything

```bash
docker-compose up --build
```

This will:
1. Download base images (eclipse-temurin:17, postgres:15, rabbitmq, redis, zipkin)
2. Compile all 8 Spring Boot services inside Docker using Maven
3. Start everything in the right order

**First run takes 10–20 minutes** (Maven downloads dependencies inside Docker).
Subsequent runs are much faster because Docker caches the layers.

To run in background:
```bash
docker-compose up --build -d
```

---

## Step 3 — Verify Everything is Running

### Check all containers are up:
```bash
docker ps
```
You should see 12 containers: postgres, rabbitmq, redis, zipkin, eurekaserver, authservice, sessionservice, skillservice, reviewservice, groupservice, notificationservice, apigateway.

### Check logs if something fails:
```bash
docker logs authservice --tail=80
docker logs eurekaserver --tail=80
```

### Open these URLs in your browser:

| URL | What you see |
|---|---|
| http://localhost:8761 | Eureka Dashboard — all services registered |
| http://localhost:15672 | RabbitMQ UI (guest / guest) |
| http://localhost:9411 | Zipkin tracing UI |
| http://localhost:8080/swagger-ui.html | Aggregated Swagger — all services |

---

## Step 4 — End-to-End API Testing

All requests go through the **API Gateway on port 8080**.

### 4.1 — Register a User

```bash
curl -X POST http://localhost:8080/authservice/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@test.com",
    "password": "password123"
  }'
```

### 4.2 — Login and Get JWT Token

```bash
curl -X POST http://localhost:8080/authservice/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@test.com",
    "password": "password123"
  }'
```

Copy the `token` from the response. Use it in all subsequent requests as:
`Authorization: Bearer <your-token-here>`

### 4.3 — Test Skill Service

```bash
# Get all skills
curl http://localhost:8080/skillservice/api/skills \
  -H "Authorization: Bearer <your-token>"

# Add a skill (admin)
curl -X POST http://localhost:8080/skillservice/api/admin/skills \
  -H "Authorization: Bearer <your-token>" \
  -H "Content-Type: application/json" \
  -d '{"name": "Java", "category": "Programming"}'
```

### 4.4 — Test Session Service

```bash
curl http://localhost:8080/sessionservice/api/sessions \
  -H "Authorization: Bearer <your-token>"
```

### 4.5 — Test Review Service

```bash
curl http://localhost:8080/reviewservice/api/reviews \
  -H "Authorization: Bearer <your-token>"
```

### 4.6 — Test Group Service

```bash
curl http://localhost:8080/groupservice/api/groups \
  -H "Authorization: Bearer <your-token>"
```

### 4.7 — Test via Swagger UI (Easier!)

1. Open http://localhost:8080/swagger-ui.html
2. Select a service from the dropdown (top right)
3. Click "Authorize" and enter: `Bearer <your-token>`
4. Try any endpoint directly from the browser

---

## Step 5 — Check Distributed Tracing

After making a few API calls:
1. Open http://localhost:9411
2. Click "Run Query"
3. You'll see traces spanning multiple services

---

## Stopping Everything

```bash
# Stop all containers (keeps data)
docker-compose down

# Stop and DELETE all data (fresh start)
docker-compose down -v
```

---

## Restarting a Single Service (after a code change)

```bash
docker-compose up --build authservice -d
```

---

## Pushing Images to Docker Hub (Optional)

```bash
# First build
docker-compose build

# Tag and push each image
docker login

docker tag skillsync-authservice yourusername/skillsync-authservice:latest
docker push yourusername/skillsync-authservice:latest

# Repeat for: sessionservice, skillservice, reviewservice,
# groupservice, notificationservice, eurekaserver, apigateway
```

---

## Troubleshooting

**Service crashes immediately on startup:**
It probably started before Eureka or Postgres was ready. Just restart it:
```bash
docker-compose restart authservice
```

**Port already in use:**
```bash
# Find what's using the port (example: 8080)
lsof -i :8080       # Mac/Linux
netstat -ano | findstr :8080   # Windows
```

**Out of memory:**
Open Docker Desktop → Settings → Resources → increase Memory to at least 8 GB.

**Want to see logs live:**
```bash
docker-compose logs -f authservice sessionservice
```
