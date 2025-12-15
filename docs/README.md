# API Documentation

This directory contains static API documentation for the Reflection API.

## Files

- `index.html` - Swagger UI interface for browsing the API documentation
- `openapi.json` - Generated during deployment (not checked into version control)

## How It Works

The OpenAPI specification is automatically generated during the GitHub Pages deployment:

1. The GitHub Actions workflow builds the application
2. Starts the application temporarily with Docker Compose
3. Fetches the OpenAPI spec from the running application at `/v3/api-docs`
4. Deploys the static files to GitHub Pages

This ensures the documentation always reflects the current state of the API without manual intervention.

## Viewing Locally

To preview the documentation locally:

1. Start the application:
   ```bash
   docker compose up -d
   mvn spring-boot:run
   ```

2. In a separate terminal, generate the spec and serve the docs:
   ```bash
   curl http://localhost:8080/v3/api-docs -o docs/openapi.json
   python3 -m http.server 8000 --directory docs
   ```

3. Visit http://localhost:8000

## GitHub Pages

The documentation is automatically published to GitHub Pages when changes are pushed to the `main` branch:
https://macintorsten.github.io/reflection/

The deployment is triggered by changes to:
- Source code (`src/**`)
- Maven configuration (`pom.xml`)
- Documentation files (`docs/**`)
- Deployment workflow (`.github/workflows/deploy-docs.yml`)

Note: The "Try it out" functionality is disabled in the static version since there's no running server.
