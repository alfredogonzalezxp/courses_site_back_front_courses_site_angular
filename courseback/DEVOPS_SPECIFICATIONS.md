# DevOps & CI/CD Lifecycle Specifications

This document outlines the complete automation and containerization strategy for the Devcourses backend.

---

## 1. Containerization (Docker)

### 1.1 The Dockerfile

**Path**: `Dockerfile`
Our build follows a **Multi-Stage** pattern to ensure the final image is small and secure.

- **Build Stage**: Uses `maven:3.9.6-eclipse-temurin-21` to compile the source code and package the JAR.
- **Run Stage**: Uses `eclipse-temurin:21-jre-jammy`. It only contains the pre-compiled JAR, making it lightweight for production.

**Build Command**:

```bash
docker build -t courses-backend .
```

### 1.2 Local Orchestration (Docker Compose)

**Path**: `docker-compose.yml`
This file allows you to start the entire backend stack (App + Database) with a single command.

- **Service: `db`**: Runs PostgreSQL 15 on an internal network. It has a **health check** to ensure the DB is ready before the app starts.
- **Service: `app`**: Builds the current directory and connects to the `db` service using the internal network name.

**Running the Stack**:

```bash
# Start everything in the background
docker-compose up -d

# Stop everything
docker-compose down
```

---

## 2. CI/CD Pipeline (GitHub Actions)

### 2.1 The Workflow

**Path**: [main.yml](file:///c:/Users/Alfrredo/Documents/Angular/devcourses/.github/workflows/main.yml)
This is the **Unified Full-Stack Chain**. It is triggered automatically on every `push` or `pull_request` to the `main`/`master` branches.

**Key Stages**:

1.  **Backend Stage**:
    - Spins up PostgreSQL 15.
    - Sets up JDK 21.
    - Runs `./mvnw clean test` (JUnit + Mockito).
2.  **Frontend Stage**:
    - Sets up Node 22.
    - Installs dependencies (`--legacy-peer-deps`).
    - Runs Vitest component tests.
3.  **Docker Validation**:
    - Validates that the entire full-stack project builds successfully using `docker compose build`.

---

## 3. Environment & Configuration Strategy

### 3.1 Dynamic Properties

**Path**: `src/main/resources/application.properties`
We use the `${VARIABLE:DEFAULT}` syntax. This allows the app to adapt to its environment automatically:

- **In Docker/Cloud**: It uses values like `${DB_URL}` provided by the container engine.
- **Local Development**: It falls back to your local `localhost:5433` if no variables are set.

### 3.2 List of Key Environment Variables

| Variable      | Description            | Default Value                               |
| :------------ | :--------------------- | :------------------------------------------ |
| `DB_URL`      | JDBC Connection String | `jdbc:postgresql://localhost:5433/appdbang` |
| `DB_USER`     | Database Username      | `adminbckang`                               |
| `DB_PASSWORD` | Database Password      | `StrongPassw0rd-2029!`                      |

---

## 4. Operational Commands Summary

### 4.1 Running Tests

```bash
./mvnw clean test
```

### 4.2 Building & Running with Docker

```bash
# Force a clean build and start
docker-compose up --build
```

### 4.3 Checking Logs

```bash
docker logs -f courses-app
```

---

## 5. How it Works (The Workflow)

When you run `docker-compose up --build`, the following happens:

1.  **Isolation**: Docker creates two virtual "mini-computers" (containers).
2.  **Database**: It downloads **PostgreSQL 15** into the first container.
3.  **App Build**: It copies your project into the second container, downloads Maven, and compiles your code into a JAR.
4.  **Networking**: It connects them via a private internal network so the app can reach the database.

---

## 6. Transitioning to Production (Security & Best Practices)

While this setup is **architecturally ready** for production, there are critical steps you must take before deploying to a live server:

### 6.1 Security Secrets

Currently, sensitive data like `jwt.secret` and database passwords have "default" values in the code.

- **NEVER** commit real production secrets to GitHub.
- **Production Step**: Use **GitHub Secrets** or a **Secret Manager** to inject these variables at runtime.

### 5.2 Database

Our `docker-compose.yml` includes a local PostgreSQL container.

- **Production Step**: Often, you will use a **Managed Database** (like AWS RDS or DigitalOcean Managed DB) for better backups and performance. In that case, you only need the `Dockerfile` and the correct `DB_URL`.

### 5.3 HTTPS/SSL

Web applications must run over HTTPS.

- **Production Step**: Use a **Reverse Proxy** (like Nginx, Traefik, or Caddy) or a Cloud Load Balancer to handle SSL certificates (Let's Encrypt).

### 5.4 Summary: Local vs Production

| Feature      | Local (Current)        | Production (Future)                |
| :----------- | :--------------------- | :--------------------------------- |
| **Secrets**  | Hardcoded Defaults     | Environment Variables / Vault      |
| **Database** | Containerized (Docker) | Managed Service (Persistent)       |
| **SSL**      | HTTP (Port 8080)       | HTTPS (Port 443)                   |
| **Scale**    | Single Instance        | Scalable Clusters (Kubernetes/ECS) |

---

> [!TIP]
> **Your Structure is Ready**: Because we used **Environment Variables**, you don't need to change any code to go to production! You just need to provide the REAL values at the moment you deploy.

Option A: Skip the local hook (fast push)

bash
git add .
git commit -m "your message"
git push --no-verify
--no-verify skips the local pre-push hook. Your code goes straight to GitHub, and the CI/CD pipeline runs in the cloud instead of on your machine.

Option B: Remove the hook entirely (recommended — the cloud CI already does the same thing)

bash
del .git\hooks\pre-push
After this, every normal git push will go straight to GitHub and trigger the workflow without the local delay.

Would you like me to remove the pre-push hook so that a regular git push always works instantly and triggers the GitHub workflow?
