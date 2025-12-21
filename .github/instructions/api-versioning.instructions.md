# API Versioning & OpenAPI Spec Management

## Overview

The API uses checked-in OpenAPI specifications to enforce API contracts. When you make changes to the API:

1. **Tests validate responses** against the baseline spec using `OpenApiValidator`
2. **Tests verify the spec is up-to-date** by comparing generated spec to baseline
3. **Developers must explicitly update specs** when changing the API
4. **GitHub Pages deployment** copies the baseline spec (no generation needed)

## Baseline Spec Files

| Path | Purpose | Status |
|------|---------|--------|
| `docs/openapi.json` | Single source of truth - committed to repo | Checked in ✅ |
| `target/test-classes/openapi.json` | Copy for tests (generated during build) | Not committed |

## Making API Changes

### Scenario 1: Non-Breaking Change (Adding Optional Field)

Example: Add optional `description` field to `SampleResponse`

```java
// Add to SampleResponse.java
@Schema(description = "Optional description")
String description;
```

**Steps:**
1. Make code changes
2. Run tests: `mvn test`
3. Tests fail: `ApiSpecValidationTest` detects spec mismatch
4. Update baseline: `.github/scripts/update-openapi-spec.sh`
5. Run tests again: `mvn test` (should now pass)
6. Commit: `git add docs/openapi.json && git commit -m "feat: add description to Sample"`

### Scenario 2: Breaking Change (Removing Field)

Example: Remove `extras` field from `SampleResponse`

```java
// Remove from SampleResponse.java
@Schema(description = "Extra key-value pairs", example = "{\"foo\":\"1\"}")
Map<String, String> extras;
```

**Steps:**
1. Make code changes
2. Run tests: `mvn test`
3. Tests fail: Breaking change detected
4. **Create new API version instead:**
   ```
   src/main/java/.../web/controller/v2/SampleControllerV2.java
   src/main/java/.../web/dto/v2/request/CreateSampleRequest.java
   src/main/java/.../web/dto/v2/response/SampleResponse.java
   ```
5. Keep v1 API intact for backward compatibility
6. Update spec: `docs/openapi.json` will contain both v1 and v2 endpoints
7. Update tests for both v1 and v2

### Scenario 3: Intentional API Deprecation (Removing v1)

Only remove old API versions after deprecation period (documented in release notes).

**Steps:**
1. Create v2 API (see Scenario 2)
2. Mark v1 as deprecated in documentation
3. Release with deprecation warning
4. After deprecation period:
   - Delete `src/main/java/.../web/controller/v1/`
   - Update `docs/openapi.json` (will only contain v2 endpoints)
   - Remove v1 tests
   - Release as major version bump

## Regenerating Baseline Spec

When you make non-breaking changes to the API:

```bash
# 1. Make code changes
# 2. Run tests (they will fail if spec is out of date)
# 3. Update baseline spec:
.github/scripts/update-openapi-spec.sh

# 4. Verify changes
git diff docs/openapi.json

# 5. Run tests again (should pass)
mvn test

# 6. Commit
git add docs/openapi.json
git commit -m "chore: update OpenAPI spec"
```

## Understanding Test Failures

### ApiSpecValidationTest Fails

**Meaning:** The generated spec from the running app differs from the checked-in baseline.

**Cause:** You changed the API (endpoints, fields, types, validations, etc.)

**Solution:** 
- **Non-breaking?** Run `.github/scripts/update-openapi-spec.sh`
- **Breaking?** Create new API version (v2, v3, etc.)

### SampleControllerV1IntegrationTest Fails on OpenApiValidator Check

**Meaning:** Response doesn't match the OpenAPI schema defined in the baseline spec.

**Cause:** Response structure doesn't match spec definition.

**Solutions:**
1. Fix response to match spec
2. OR update spec if intentional change (run update script)

## API Versioning Examples

### Coexisting API Versions (V1 and V2)

The OpenAPI spec in `docs/openapi.json` contains **all active API versions**. When you run the application, Spring Boot automatically generates a unified spec with endpoints tagged by version:

```
docs/openapi.json                              # Contains both v1 and v2 endpoints
src/main/java/.../web/controller/v1/SampleControllerV1.java
src/main/java/.../web/controller/v2/SampleControllerV2.java
src/main/java/.../web/dto/v1/...
src/main/java/.../web/dto/v2/...
```

**Both can coexist** to maintain backward compatibility:
- `/api/v1/samples` - Original endpoints (tagged as "v1" in spec)
- `/api/v2/samples` - Updated endpoints (tagged as "v2" in spec)

## CI/CD Pipeline

### GitHub Pages Deployment
1. Push to `main` branch
2. GitHub Actions workflow runs
3. Deploys `docs/` directory (containing `openapi.json`) to GitHub Pages
4. Available at https://github.../reflection/

✅ **No spec generation or copying needed** - `docs/openapi.json` is already the source of truth

### Test Validation
1. `mvn test` runs all unit and integration tests
2. `ApiSpecValidationTest` ensures spec matches app code
3. Individual endpoint tests use `OpenApiValidator` to validate schema
4. Fails if API changes aren't reflected in baseline spec

## OpenApiValidator Usage

Add to any endpoint test to validate response schema:

```java
mockMvc.perform(post("/api/v1/samples")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
    .andExpect(status().isCreated())
    .andExpect(jsonPath("$.id").exists())
    // Validate response matches OpenAPI spec schema
    .andExpect(OpenApiValidator.matchesOpenApi(
        "openapi.json",        // Baseline spec file (from docs/openapi.json)
        "POST",                // HTTP method
        "/api/v1/samples",     // API path
        201));                 // Expected status code
```

## Troubleshooting

### ApiSpecValidationTest Can't Find Spec File

```
RuntimeException: Baseline OpenAPI spec not found in classpath: openapi.json
```

**Solution:** Ensure Maven correctly copies the spec during build:
```bash
# Rebuild with clean
mvn clean test

# Verify docs/openapi.json exists
ls -l docs/openapi.json
```

### Spec Generation Fails

```
Application failed to start within 60 seconds
```

**Solution:**
```bash
# 1. Check Docker is running
docker ps

# 2. Check logs
tail -f /tmp/app-spec-gen.log

# 3. Increase timeout in update-openapi-spec.sh (modify MAX_WAIT variable)
```

## Key Principles

✅ **Baseline spec is source of truth** - Checked in, versioned, always accurate
✅ **Tests enforce contract** - ApiSpecValidationTest validates spec matches code
✅ **Explicit updates required** - Can't accidentally deploy breaking changes
✅ **Clear versioning** - Multiple API versions can coexist
✅ **Fast GitHub Pages** - Deploys baseline spec, no generation overhead

## Quick Reference

| Task | Command |
|------|---------|
| Update baseline spec | `.github/scripts/update-openapi-spec.sh` |
| Run all tests | `mvn test` |
| Rebuild after API change | `mvn clean test` |
| View spec on docs site | https://macintorsten.github.io/reflection/ |
| Compare local spec changes | `git diff docs/openapi.json` |
