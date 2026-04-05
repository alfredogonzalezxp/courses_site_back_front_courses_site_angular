# DevOps Commands Reference

This document provides a guide for managing the Dockerized full-stack environment.

---

## Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running.
- Maven (for backend tests outside Docker).
- Node.js & NPM (for frontend tests outside Docker).

---

## Local Development (Docker Compose)

### 1. Build and Start All Services

```bash
docker-compose up --build
```

- **Purpose**: Full Environment Initialization.
- **Why**: Use this the first time you start the project or when you've modified a `Dockerfile` or `package.json`. It ensures everything is recompiled and synchronized.

### 2. Run in Detached Mode (Background)

```bash
docker-compose up -d
```

- **Purpose**: Low-Maintenance Execution.
- **Why**: Starts the services in the background, freeing up your terminal. Perfect when you're just using the app and don't need to see the logs constantly.

### 3. Stop and Remove All Containers

```bash
docker-compose down
```

- **Purpose**: Clean Shutdown.
- **Why**: Safely stops all running services and removes the containers. Always do this when you're finished working to save system resources.

### 4. Remove Containers and Volumes (Clean Reset)

```bash
docker-compose down -v
```

- **Purpose**: Factory Reset.
- **Why**: Not only stops services but also **deletes the database data**. Use this if your database state becomes corrupted or you want to start from a completely empty DB.

### 5. Check Logs

```bash
docker-compose logs -f         # All services
docker-compose logs -f backend # Backend only
docker-compose logs -f frontend # Frontend only
```

- **Purpose**: Direct Troubleshooting.
- **Why**: Streams the console output of your containers. If the app isn't loading or an API call fails, the reason will be printed here in real-time.

---

## Service URLs

| Service               | Access URL                  |
| --------------------- | --------------------------- |
| Frontend (Angular)    | `http://localhost`          |
| Backend (Spring Boot) | `http://localhost:8080`     |
| API Base Path         | `http://localhost:8080/api` |
| Database (Postgres)   | `localhost:5433`            |

---

## Testing the Infrastructure Locally

### Validate individual Dockerfiles

- **Backend Build Test**:

  ```bash
  cd courseback
  docker build -t courses-backend-test .
  ```

  - **Purpose**: Image Validation.
  - **Why**: Verifies the `Dockerfile` works independently before trying to run the whole compose stack.

- **Frontend Build Test**:

  ```bash
  cd coursefront
  docker build -t courses-frontend-test .
  ```

  - **Purpose**: Production Build Simulation.
  - **Why**: Ensures the Angular build and Nginx configuration are correct for a production-like environment.

### Verify CI/CD (GitHub Actions Simulation)

1. **Backend Tests**:

   ```bash
   cd courseback
   ./mvnw clean test
   ```

   - **Purpose**: Logic Verification (Backend).
   - **Why**: Runs all JUnit/Mocking tests to ensure the Java code is stable and bug-free.

2. **Frontend Tests**:

   ```bash
   cd coursefront
   npm test -- --reporters=verbose --watch=false
   ```

   - **Purpose**: Logic Verification (Frontend).
   - **Why**: Runs the Vitest suite in a single pass to ensure Angular components and services work as expected before deployment.

---

## PostgreSQL Credentials (for pgAdmin/DBeaver)

- **Host**: `localhost`
- **Port**: `5433`
- **Database**: `appdbang`
- **Username**: `adminbckang`
- **Password**: `StrongPassw0rd-2029!`

---

## Local CI/CD Pipeline (Simulation)

This section provides a way to run the entire project's validation process with a single command, mirroring exactly what occurs in [GitHub Actions](.github/workflows/main.yml).

### Run the Automated Pipeline

```powershell
./local-ci.ps1
```

- **Purpose**: Absolute Verification.
- **Why**: Instead of running backend, then frontend, then docker tests manually, this script orchestrates all three stages:
  1. **Stage 1 (Backend)**: Cleans and runs Maven JUnit tests.
  2. **Stage 2 (Frontend)**: Runs Vitest component/logic tests in a single pass.
  3. **Stage 3 (Docker)**: Validates that `docker-compose.yml` can still build everything from scratch.
