# Commands to Run Backend Tests

This file contains the essential commands to execute the automated test suite (JUnit/Mockito) and the newly added monitoring tool (Actuator).

---

## 1. Automated Java Tests (JUnit & Mockito)

Since your project uses the **Maven Wrapper**, you should use `./mvnw` (or `./mvnw.cmd` on Windows) to ensure the correct Maven version is used.

### 1.1 Run All Tests

This command executes every test across all layers (Repository, Service, Controller, Security).

```bash
./mvnw clean test
```

### 1.2 Run a Specific Test Category

If you want to focus on one specific part of the app:

- **Repository Tests only**:
  ```bash
  ./mvnw test -Dtest=UserRepositoryTest
  ```
- **Service Layer only**:
  ```bash
  ./mvnw test -Dtest=AuthServiceTest
  ```
- **Controller Layer only**:
  ```bash
  ./mvnw test -Dtest=AuthControllerTest
  ```
- **Security Logic only**:

  ````bash
  ```bash
  ./mvnw test -Dtest=JwtTokenProviderTest
  ````

- **Application Smoke Test (Context Load)**:
  ```bash
  ./mvnw test -Dtest=CoursesbackendApplicationTests
  ```

### 1.3 Run a Single Test Method

If you only want to run one specific test case inside a file:

```bash
# Example: Only test the signup logic in the controller
./mvnw test -Dtest=AuthControllerTest#givenValidSignUpRequest_whenSignUp_thenReturnCreatedStatus
```

---

## 2. Viewing Test Reports

After running the tests, Maven generates detailed reports. You can find them here:

- **Text Reports**: `target/surefire-reports/*.txt`
- **XML Reports**: `target/surefire-reports/*.xml`

---

## 3. Monitoring & Metrics (Spring Boot Actuator)

To verify that the **Actuator** is correctly installed and monitoring your app:

### 3.1 Health Check (Is the app alive?)

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method GET
```

### 3.2 List All Actuator Endpoints (Metrics, Info, etc.)

_Note: Some endpoints might be restricted by SecurityConfig._

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/actuator" -Method GET
```
