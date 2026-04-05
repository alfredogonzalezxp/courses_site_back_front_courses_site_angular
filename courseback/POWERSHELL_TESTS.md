# PowerShell Endpoint Tests

This file contains readily executable PowerShell commands using `Invoke-RestMethod` to test all the endpoints of your Devcourses backend application.

You can copy and paste these blocks directly into a PowerShell window.

---

## 1. Health Checks
Testing to see if the server and the newly added actuator are running.

### 1.1 Basic API Health Check
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/health" -Method GET
```

### 1.2 Spring Boot Actuator Health Check
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method GET
```

---

## 2. Authentication Flow

### 2.1 Sign Up (Register a new user)
```powershell
$signupBody = @{
    nombre = "Test User"
    email = "test@user.com"
    password = "password123"
    rol = "USER"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/signup" `
                  -Method POST `
                  -ContentType "application/json" `
                  -Body $signupBody
```

### 2.2 Sign In (Get JWT Token)
```powershell
$signinBody = @{
    email = "test@user.com"
    password = "password123"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "http://localhost:8080/api/signin" `
                              -Method POST `
                              -ContentType "application/json" `
                              -Body $signinBody

# Extract and save the token to a variable for future requests
$token = $response.accessToken
Write-Host "JWT Token Received: $token"
```

---

## 3. User Management (CRUD)
*Note: Depending on your `SecurityConfig.java`, these might eventually require the `$token` variable generated in step 2.2.*

### 3.1 Get All Users
```powershell
# Currently configured as permitAll() in SecurityConfig
Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Method GET
```

*(If you later secure this endpoint, you will need to add the header like this):*
```powershell
# Example of authenticated request
$headers = @{ Authorization = "Bearer $token" }
Invoke-RestMethod -Uri "http://localhost:8080/api/users" -Method GET -Headers $headers
```

### 3.2 Update User (Requires knowing the User ID, e.g., 1)
```powershell
$updateBody = @{
    nombre = "Updated Name"
    email = "updated@user.com"
    password = "newpassword123"
    rol = "ADMIN"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/users/1" `
                  -Method PUT `
                  -ContentType "application/json" `
                  -Body $updateBody
```

### 3.3 Delete User (Requires knowing the User ID, e.g., 1)
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/users/1" -Method DELETE
```
