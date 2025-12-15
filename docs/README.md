# API Documentation

This directory contains static API documentation generated from the OpenAPI specification.

## Files

- `index.html` - Swagger UI interface for browsing the API documentation
- `openapi.json` - OpenAPI 3.1.0 specification for the Reflection API

## Viewing Locally

You can open `index.html` directly in a browser, or serve it with a simple HTTP server:

```bash
# Using Python
python3 -m http.server 8000 --directory docs

# Using Node.js (if npx is available)
npx serve docs
```

Then visit http://localhost:8000

## Updating the Specification

The OpenAPI specification is generated from the application's annotations. To update it:

1. Start the application:
   ```bash
   docker compose up -d
   mvn spring-boot:run
   ```

2. Generate the spec (in a separate terminal):
   ```bash
   curl http://localhost:8080/v3/api-docs -o docs/openapi.json
   ```

3. Format the JSON (optional):
   ```bash
   python3 -m json.tool docs/openapi.json > docs/openapi-formatted.json
   mv docs/openapi-formatted.json docs/openapi.json
   ```

4. Commit the changes:
   ```bash
   git add docs/openapi.json
   git commit -m "Update OpenAPI specification"
   ```

## GitHub Pages

This documentation is published to GitHub Pages and can be accessed at:
https://macintorsten.github.io/reflection/

Note: The "Try it out" functionality is disabled in the static version since there's no running server.
