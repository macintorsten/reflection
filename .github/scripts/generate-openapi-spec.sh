#!/bin/bash
set -e

# Script to generate OpenAPI specification from a running Spring Boot application
# This script can be run locally or in CI/CD workflows

OUTPUT_DIR="${1:-docs}"
OUTPUT_FILE="${OUTPUT_DIR}/openapi.json"

echo "=== OpenAPI Spec Generation Script ==="
echo "Output directory: ${OUTPUT_DIR}"
echo "Output file: ${OUTPUT_FILE}"
echo ""

# 1. Build the application
echo "Step 1: Building application..."
mvn clean package -DskipTests
echo "✓ Build complete"
echo ""

# 2. Start database
echo "Step 2: Starting database..."
docker compose up -d
echo "✓ Database started"
echo ""

# 3. Start application
echo "Step 3: Starting application..."
java -jar target/*.jar &
APP_PID=$!
echo "Application started with PID: ${APP_PID}"

# Save PID to file for cleanup
echo $APP_PID > /tmp/app.pid
echo ""

# 4. Wait for application to be ready
echo "Step 4: Waiting for application to be ready..."
MAX_WAIT=60
for i in $(seq 1 $MAX_WAIT); do
  if curl -s http://localhost:8080/v3/api-docs > /dev/null 2>&1; then
    echo "✓ Application is ready! (${i}s)"
    break
  fi
  if [ $i -eq $MAX_WAIT ]; then
    echo "✗ Application failed to start within ${MAX_WAIT} seconds"
    # Cleanup
    kill $APP_PID 2>/dev/null || true
    docker compose down
    exit 1
  fi
  sleep 1
done
echo ""

# 5. Generate OpenAPI spec
echo "Step 5: Generating OpenAPI specification..."
mkdir -p "${OUTPUT_DIR}"
curl -f http://localhost:8080/v3/api-docs | python3 -m json.tool > "${OUTPUT_FILE}"
echo "✓ OpenAPI spec saved to: ${OUTPUT_FILE}"
echo ""

# 6. Cleanup
echo "Step 6: Cleaning up..."
echo "Stopping application (PID: ${APP_PID})..."
kill $APP_PID 2>/dev/null || true
echo "Stopping database..."
docker compose down
rm -f /tmp/app.pid
echo "✓ Cleanup complete"
echo ""

echo "=== Generation Complete ==="
echo "OpenAPI specification is available at: ${OUTPUT_FILE}"
echo ""
echo "To test the documentation locally, run:"
echo "  python3 -m http.server 8000 --directory ${OUTPUT_DIR}"
echo "Then open: http://localhost:8000"
