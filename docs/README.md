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

## Generating and Testing Locally

Use the provided scripts to generate and test the documentation:

### Generate OpenAPI Spec
```bash
# Generate the OpenAPI specification
.github/scripts/generate-openapi-spec.sh docs
```

This script will:
- Build the application
- Start the database and application
- Generate the OpenAPI spec
- Clean up resources

### Test Documentation
```bash
# Verify the documentation setup
.github/scripts/test-docs-locally.sh
```

This script validates:
- Required files exist
- OpenAPI spec is valid JSON
- HTML is correctly configured

### View Locally
After generating the spec, serve the documentation:
```bash
# Using Python
python3 -m http.server 8000 --directory docs

# Or using npx
npx serve docs
```

Then visit http://localhost:8000

## GitHub Pages

The documentation is automatically published to GitHub Pages when changes are pushed to the `main` branch:
https://macintorsten.github.io/reflection/

The deployment is triggered by changes to:
- Source code (`src/**`)
- Maven configuration (`pom.xml`)
- Documentation files (`docs/**`)
- Deployment workflow (`.github/workflows/deploy-docs.yml`)

Note: The "Try it out" functionality is disabled in the static version since there's no running server.
