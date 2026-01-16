#!/bin/bash
# Quick Fix Script for Mini PLM Backend Build
# Date: January 16, 2026
# Usage: bash QUICK_FIX.sh

echo "======================================"
echo "Mini PLM Backend - Quick Fix Script"
echo "======================================"
echo ""

# Navigate to backend directory
echo "[1/4] Navigating to backend directory..."
cd "$(dirname "$0")/mini-plm-backend" || exit 1
echo "    ✓ Location: $(pwd)"
echo ""

# Clean Maven cache
echo "[2/4] Cleaning Maven cache..."
mvn clean -q
echo "    ✓ Maven cache cleaned"
echo ""

# Build with annotation processing
echo "[3/4] Building with annotation processing..."
mvn clean install -DskipTests -q
if [ $? -eq 0 ]; then
    echo "    ✓ Build successful!"
else
    echo "    ✗ Build failed. Please check errors above."
    exit 1
fi
echo ""

# Start application
echo "[4/4] Starting Spring Boot application..."
echo "    ✓ Starting on http://localhost:8080"
echo ""
mvn spring-boot:run
