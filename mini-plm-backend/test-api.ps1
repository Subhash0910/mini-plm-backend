# Mini PLM Backend - PowerShell Testing Script
# Tests all critical API endpoints
# Usage:
#   powershell -ExecutionPolicy Bypass -File .\test-api.ps1

$BaseUrl = "http://localhost:8080/api"
$ErrorActionPreference = "Stop"

Write-Host ("=" * 60) -ForegroundColor Cyan
Write-Host "Mini PLM Backend - API Testing" -ForegroundColor Cyan
Write-Host ("=" * 60) -ForegroundColor Cyan

function Get-ErrorBody {
    param($err)
    try {
        return $err.Exception.Response.Content.ReadAsStringAsync().Result
    } catch {
        return $null
    }
}

# -----------------------------
# Test 1: Health Check
# -----------------------------
Write-Host "`n[1] Testing Health Endpoint" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri ("{0}/auth/health" -f $BaseUrl) -Method GET
    Write-Host "[OK] Health Check: SUCCESS" -ForegroundColor Green
    Write-Host ("Response: {0}" -f $response.Content) -ForegroundColor Green
} catch {
    Write-Host "[X] Health Check: FAILED" -ForegroundColor Red
    Write-Host ("Error: {0}" -f $_.Exception.Message) -ForegroundColor Red
}

# -----------------------------
# Test 2: Signup
# -----------------------------
Write-Host "`n[2] Testing Signup Endpoint" -ForegroundColor Yellow
$timestamp = Get-Date -Format "yyyyMMddHHmmss"
$testUsername = "testuser_{0}" -f $timestamp
$testEmail = "test_{0}@example.com" -f $timestamp

$signupBody = @{
    username = $testUsername
    email    = $testEmail
    password = "Test@1234567"
} | ConvertTo-Json

Write-Host "Signup Request:" -ForegroundColor Gray
Write-Host $signupBody -ForegroundColor Gray

$jwtToken = $null

try {
    $signupParams = @{
        Uri         = ("{0}/auth/signup" -f $BaseUrl)
        Method      = 'POST'
        ContentType = 'application/json'
        Body        = $signupBody
    }
    $signupResponse = Invoke-WebRequest @signupParams

    Write-Host "[OK] Signup: SUCCESS (201 Created)" -ForegroundColor Green

    $signupData = $signupResponse.Content | ConvertFrom-Json
    $jwtToken = $signupData.token

    Write-Host "Response:" -ForegroundColor Green
    Write-Host ($signupData | ConvertTo-Json) -ForegroundColor Green
    if ($jwtToken) {
        Write-Host ("Token: {0}..." -f $jwtToken.Substring(0, 20)) -ForegroundColor Cyan
    }
} catch {
    Write-Host "[X] Signup: FAILED" -ForegroundColor Red
    Write-Host ("Error: {0}" -f $_.Exception.Message) -ForegroundColor Red
    $body = Get-ErrorBody $_
    if ($body) { Write-Host ("Response: {0}" -f $body) -ForegroundColor Red }
    exit 1
}

# -----------------------------
# Test 3: Login
# -----------------------------
Write-Host "`n[3] Testing Login Endpoint" -ForegroundColor Yellow
$loginBody = @{
    username = $testUsername
    password = "Test@1234567"
} | ConvertTo-Json

Write-Host "Login Request:" -ForegroundColor Gray
Write-Host $loginBody -ForegroundColor Gray

try {
    $loginParams = @{
        Uri         = ("{0}/auth/login" -f $BaseUrl)
        Method      = 'POST'
        ContentType = 'application/json'
        Body        = $loginBody
    }
    $loginResponse = Invoke-WebRequest @loginParams

    Write-Host "[OK] Login: SUCCESS (200 OK)" -ForegroundColor Green

    $loginData = $loginResponse.Content | ConvertFrom-Json
    $jwtToken = $loginData.token

    Write-Host "Response:" -ForegroundColor Green
    Write-Host ($loginData | ConvertTo-Json) -ForegroundColor Green
    if ($jwtToken) {
        Write-Host ("Token: {0}..." -f $jwtToken.Substring(0, 20)) -ForegroundColor Cyan
    }
} catch {
    Write-Host "[X] Login: FAILED" -ForegroundColor Red
    Write-Host ("Error: {0}" -f $_.Exception.Message) -ForegroundColor Red
    $body = Get-ErrorBody $_
    if ($body) { Write-Host ("Response: {0}" -f $body) -ForegroundColor Red }
    exit 1
}

# -----------------------------
# Test 4: Protected endpoint - GET /api/parts
# -----------------------------
Write-Host "`n[4] Testing Protected Endpoint - GET /api/parts" -ForegroundColor Yellow

$headers = @{
    Authorization = "Bearer $jwtToken"
    'Content-Type' = 'application/json'
}

try {
    $partsResponse = Invoke-WebRequest -Uri ("{0}/parts" -f $BaseUrl) -Method GET -Headers $headers
    Write-Host "[OK] GET /api/parts: SUCCESS (200 OK)" -ForegroundColor Green
    if ($partsResponse.Content.Length -gt 200) {
        Write-Host ("Response (truncated): {0}..." -f $partsResponse.Content.Substring(0, 200)) -ForegroundColor Green
    } else {
        Write-Host ("Response: {0}" -f $partsResponse.Content) -ForegroundColor Green
    }
} catch {
    Write-Host "[X] GET /api/parts: FAILED" -ForegroundColor Red
    Write-Host ("Error: {0}" -f $_.Exception.Message) -ForegroundColor Red
    $body = Get-ErrorBody $_
    if ($body) { Write-Host ("Response: {0}" -f $body) -ForegroundColor Red }
}

Write-Host "`n" -NoNewline
Write-Host ("=" * 60) -ForegroundColor Cyan
Write-Host "All Tests Completed!" -ForegroundColor Cyan
Write-Host ("=" * 60) -ForegroundColor Cyan
