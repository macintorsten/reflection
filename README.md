# Reflection

[![CI](https://github.com/macintorsten/reflection/actions/workflows/ci.yml/badge.svg)](https://github.com/macintorsten/reflection/actions/workflows/ci.yml)

A Java Spring Boot REST API project with PostgreSQL backend, demonstrating:
- Spring Boot with Java 21
- JPA/Hibernate with PostgreSQL
- Testcontainers for integration testing
- OpenAPI/Swagger documentation
- GitHub Actions CI/CD
- Static API documentation on GitHub Pages

## Building and Testing

### Prerequisites
- Java 21
- Maven 3.x
- Docker

### Quick Start

```bash
# Build the project
mvn clean compile

# Run tests (uses Testcontainers - no manual database setup needed)
mvn test

# Run the application (requires database)
docker compose up -d
mvn spring-boot:run
```

The application will be available at http://localhost:8080

### Test Results

**View test results in multiple ways:**

| Method | Description | Link |
|--------|-------------|------|
| 🌐 **GitHub Pages** | Static overview page | [Test Results](https://macintorsten.github.io/reflection/tests.html) |
| 🔍 **Actions UI** | Live results with annotations | [CI Runs](https://github.com/macintorsten/reflection/actions) |
| � **Local Coverage** | Run `mvn verify` | Open `target/site/jacoco/index.html` |

**GitHub Actions Features:**
- Test results with line-level annotations on failures
- Test summary table in workflow run
- Coverage reports available as downloadable artifacts
- Playwright screenshots uploaded as artifacts on failures

### API Documentation

[![Deploy API Documentation](https://github.com/macintorsten/reflection/actions/workflows/deploy-docs.yml/badge.svg)](https://github.com/macintorsten/reflection/actions/workflows/deploy-docs.yml)

Static API documentation is available at: **https://macintorsten.github.io/reflection/**

The documentation is automatically generated and deployed:
- OpenAPI spec is extracted from the running application during GitHub Pages deployment
- No manual updates needed - documentation stays in sync with code changes
- "Try it out" functionality is disabled since there's no running server backend

## CI/CD

The project uses GitHub Actions for continuous integration:

- ✅ Runs on every push to `main` and all pull requests
- ✅ Executes full test suite (115+ tests) with Testcontainers and Playwright
- ✅ Uses [mikepenz/action-junit-report](https://github.com/mikepenz/action-junit-report) for test reporting
- ✅ Uploads test artifacts and screenshots on failures
- ✅ Generates code coverage reports with JaCoCo

**Test Reporting:**
- JUnit test results with annotations directly in Actions UI
- Detailed test summary in workflow runs (provided by action-junit-report)
- JaCoCo coverage reports available as downloadable artifacts
- Playwright screenshots captured on failures

See [.github/workflows/ci.yml](.github/workflows/ci.yml) for the complete configuration.

## GitHub Copilot Instructions

This repository includes comprehensive instructions for GitHub Copilot to provide better assistance with:
- Building and testing the application
- Understanding project architecture
- Following coding standards

The instructions are located in `.github/copilot-instructions.md` and `.github/instructions/`.

For more details, see [.github/copilot-instructions.md](.github/copilot-instructions.md).
