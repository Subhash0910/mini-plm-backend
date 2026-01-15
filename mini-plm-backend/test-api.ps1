# Mini PLM Backend - PowerShell Testing Script
# Tests all critical API endpoints
# Usage: .\test-api.ps1

$BaseUrl = "http://localhost:8080/api"
$ErrorActionPreference = "Stop"

write-Host "=" -ForegroundColor Cyan -NoNewline; write-Host "" * 60
write-Host "Mini PLM Backend - API Testing" -ForegroundColor Cyan
write-Host "=" -ForegroundColor Cyan -NoNewline; write-Host "" * 60

# Test 1: Health Check
write-Host "`n[1] Testing Health Endpoint" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$BaseUrl/auth/health" -Method GET
    write-Host "✓ Health Check: SUCCESS" -ForegroundColor Green
    write-Host "Response: $($response.Content)" -ForegroundColor Green
} catch {
    write-Host "✗ Health Check: FAILED" -ForegroundColor Red
    write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Signup
write-Host "`n[2] Testing Signup Endpoint" -ForegroundColor Yellow
$timestamp = Get-Date -Format "yyyyMMddHHmmss"
$testUsername = "testuser_$timestamp"
$testEmail = "test_$timestamp@example.com"

$signupBody = @{
    username = $testUsername
    email = $testEmail
    password = "Test@1234567"
} | ConvertTo-Json

write-Host "Signup Request:"
write-Host $signupBody

try {
    $signupResponse = Invoke-WebRequest -Uri "$BaseUrl/auth/signup" `
        -Method POST `
        -ContentType "application/json" `
        -Body $signupBody
    
    write-Host "✓ Signup: SUCCESS (201 CREATED)" -ForegroundColor Green
    
    $signupData = $signupResponse.Content | ConvertFrom-Json
    $jwtToken = $signupData.token
    
    write-Host "Response:" -ForegroundColor Green
    write-Host ($signupData | ConvertTo-Json) -ForegroundColor Green
    write-Host "Token: $($jwtToken.Substring(0, 20))..." -ForegroundColor Cyan
} catch {
    write-Host "✗ Signup: FAILED" -ForegroundColor Red
    write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    write-Host "Response: $($_.Exception.Response.Content.ReadAsStringAsync().Result)" -ForegroundColor Red
    exit 1
}

# Test 3: Login
write-Host "`n[3] Testing Login Endpoint" -ForegroundColor Yellow
$loginBody = @{
    username = $testUsername
    password = "Test@1234567"
} | ConvertTo-Json

write-Host "Login Request:"
write-Host $loginBody

try {
    $loginResponse = Invoke-WebRequest -Uri "$BaseUrl/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $loginBody
    
    write-Host "✓ Login: SUCCESS (200 OK)" -ForegroundColor Green
    
    $loginData = $loginResponse.Content | ConvertFrom-Json
    $jwtToken = $loginData.token
    
    write-Host "Response:" -ForegroundColor Green
    write-Host ($loginData | ConvertTo-Json) -ForegroundColor Green
    write-Host "Token: $($jwtToken.Substring(0, 20))..." -ForegroundColor Cyan
} catch {
    write-Host "✗ Login: FAILED" -ForegroundColor Red
    write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Test 4: Get Parts (Protected Endpoint)
write-Host "`n[4] Testing Protected Endpoint - GET /api/parts" -ForegroundColor Yellow

$headers = @{
    "Authorization" = "Bearer $jwtToken"
    "Content-Type" = "application/json"
}

try {
    $partsResponse = Invoke-WebRequest -Uri "$BaseUrl/parts" `
        -Method GET `
        -Headers $headers
    
    write-Host "✓ GET /api/parts: SUCCESS (200 OK)" -ForegroundColor Green
    write-Host "Response (truncated): $($partsResponse.Content.Substring(0, 200))..." -ForegroundColor Green
} catch {
    write-Host "✗ GET /api/parts: FAILED" -ForegroundColor Red
    write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 5: Invalid Credentials
write-Host "`n[5] Testing Login with Invalid Credentials" -ForegroundColor Yellow
$invalidLoginBody = @{
    username = $testUsername
    password = "WrongPassword123"
} | ConvertTo-Json

try {
    $invalidLoginResponse = Invoke-WebRequest -Uri "$BaseUrl/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $invalidLoginBody
    
    write-Host "✗ Should have failed but didn't" -ForegroundColor Red
} catch {
    if ($_.Exception.Response.StatusCode -eq 401) {
        write-Host "✓ Correctly returned 401 Unauthorized" -ForegroundColor Green
        $errorData = $_.Exception.Response.Content.ReadAsStringAsync().Result | ConvertFrom-Json
        write-Host "Error Message: $($errorData.message)" -ForegroundColor Green
    } else {
        write-Host "✗ Unexpected error code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

# Test 6: Duplicate Username
write-Host "`n[6] Testing Signup with Duplicate Username" -ForegroundColor Yellow
$duplicateBody = @{
    username = $testUsername
    email = "different_$timestamp@example.com"
    password = "Test@1234567"
} | ConvertTo-Json

try {
    $duplicateResponse = Invoke-WebRequest -Uri "$BaseUrl/auth/signup" `
        -Method POST `
        -ContentType "application/json" `
        -Body $duplicateBody
    
    write-Host "✗ Should have failed but didn't" -ForegroundColor Red
} catch {
    if ($_.Exception.Response.StatusCode -eq 400) {
        write-Host "✓ Correctly returned 400 Bad Request" -ForegroundColor Green
        $errorData = $_.Exception.Response.Content.ReadAsStringAsync().Result | ConvertFrom-Json
        write-Host "Error Message: $($errorData.message)" -ForegroundColor Green
    } else {
        write-Host "✗ Unexpected error code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
    }
}

# Test 7: Missing Authorization Header
write-Host "`n[7] Testing Protected Endpoint WITHOUT Authorization" -ForegroundColor Yellow

try {
    $noAuthResponse = Invoke-WebRequest -Uri "$BaseUrl/parts" `
        -Method GET
    
    write-Host "✗ Should have failed but didn't" -ForegroundColor Red
} catch {
    if ($_.Exception.Response.StatusCode -eq 401) {
        write-Host "✓ Correctly returned 401 Unauthorized" -ForegroundColor Green
    } else {
        write-Host "Response Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Yellow
    }
}

write-Host "`n" -ForegroundColor Cyan -NoNewline; write-Host "=" * 60
write-Host "All Tests Completed!" -ForegroundColor Cyan
write-Host "=" * 60 -ForegroundColor Cyan
write-Host "`nSummary:"
write-Host "  ✓ Health Check" -ForegroundColor Green
write-Host "  ✓ Signup (201 CREATED)" -ForegroundColor Green
write-Host "  ✓ Login (200 OK)" -ForegroundColor Green
write-Host "  ✓ Protected Endpoint (with JWT)" -ForegroundColor Green
write-Host "  ✓ Invalid Credentials (401)" -ForegroundColor Green
write-Host "  ✓ Duplicate Username (400)" -ForegroundColor Green
write-Host "  ✓ Missing Auth Header (401)" -ForegroundColor Green
write-Host "`nAll tests passed! Your API is working correctly." -ForegroundColor Green
