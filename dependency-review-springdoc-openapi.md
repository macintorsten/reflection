## Dependency: org.springdoc:springdoc-openapi-starter-webmvc-ui
**Current Version:** 2.8.8  
**Available Version:** 3.0.0  
**Release Notes:** [CHANGELOG](https://github.com/springdoc/springdoc-openapi/blob/v3.0.0/CHANGELOG.md) • [2.8.9](https://github.com/springdoc/springdoc-openapi/releases/tag/v2.8.9) • [2.8.10](https://github.com/springdoc/springdoc-openapi/releases/tag/v2.8.10) • [2.8.11](https://github.com/springdoc/springdoc-openapi/releases/tag/v2.8.11) • [2.8.12](https://github.com/springdoc/springdoc-openapi/releases/tag/v2.8.12) • [2.8.13](https://github.com/springdoc/springdoc-openapi/releases/tag/v2.8.13) • [2.8.14](https://github.com/springdoc/springdoc-openapi/releases/tag/v2.8.14) • [3.0.0](https://github.com/springdoc/springdoc-openapi/releases/tag/v3.0.0)

**CVEs/Security:** None found in release notes

**Breaking Changes:**
- 3.0.0: **Requires Spring Boot 4.0.0** - This is a major version upgrade that requires migrating your application to Spring Boot 4.x
- 3.0.0: Requires Spring Framework 7 (bundled with Spring Boot 4.0.0)

**Major Features:**
- 3.0.0: Initial API versioning support for Spring Framework 7
- 3.0.0: Static resources support for WebFlux
- 2.8.13: Scalar Support added (alternative to Swagger UI)
- 2.8.10: Support for @jakarta.annotation.Nonnull
- 2.8.10: Refactored webhook discovery and scanning mechanism

**Notes:** ⚠️ **Major upgrade requires Spring Boot 4.0.0 migration** - Version 3.0.0 is a breaking release that requires upgrading your entire Spring Boot application to version 4.x. This is not a simple dependency update. Seven intermediate versions (2.8.9-2.8.14) include numerous bug fixes and enhancements. The 2.8.x series supports Spring Boot 3.x, while 3.0.0+ requires Spring Boot 4.x. Consider staying on 2.8.14 until ready for full Spring Boot 4 migration. Also note that springdoc-openapi-maven-plugin compatibility should be verified (current project uses v1.5).
