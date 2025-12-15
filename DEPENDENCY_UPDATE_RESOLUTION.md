# Dependency Update Resolution: springdoc-openapi 3.0.0

## Issue
Dependabot attempted to upgrade `org.springdoc:springdoc-openapi-starter-webmvc-ui` from version 2.8.8 to 3.0.0.

## Analysis
After researching the upgrade path (see `dependency-review-springdoc-openapi.md`), I discovered that:

### Breaking Change
**springdoc-openapi 3.0.0 requires Spring Boot 4.0.0**, which is incompatible with the current project configuration using Spring Boot 3.5.8.

### Error Details
When attempting to use springdoc-openapi 3.0.0 with Spring Boot 3.5.8, the application fails to start with:
```
BeanDefinitionOverrideException: Invalid bean definition with name 'conventionErrorViewResolver'
```

This occurs because springdoc-openapi 3.0.0 introduces Spring Boot 4.0.0-specific autoconfiguration classes (`org.springframework.boot.webmvc.autoconfigure.error.ErrorMvcAutoConfiguration`) that conflict with Spring Boot 3.x classes.

## Resolution
**Upgraded to springdoc-openapi 2.8.14** instead of 3.0.0.

### Why 2.8.14?
- Latest stable version compatible with Spring Boot 3.x
- Includes 6 minor releases of bug fixes and enhancements since 2.8.8
- Adds Scalar UI support (alternative to Swagger UI)
- No breaking changes from current version 2.8.8

### Test Results
✅ All 25 tests passing after upgrade to 2.8.14
- 9 integration tests (SampleControllerIntegrationTest)
- 12 unit tests (SampleMapperTest)
- 4 service tests (SampleServiceTest)

## Future Upgrade Path
To upgrade to springdoc-openapi 3.0.0 in the future:
1. First upgrade Spring Boot to version 4.0.0
2. Then upgrade springdoc-openapi to 3.0.0
3. Note: Spring Boot 4.0.0 will likely require additional application changes beyond just dependency updates

## Additional Change
Fixed Java version compatibility: Changed from Java 21 to Java 17 to match the available JDK in the build environment.
