# Held Dependencies - Decision Log

**Last Updated:** November 2, 2025

## Dependencies NOT Updated

The following dependencies have newer versions available but are intentionally held at current versions due to stability and compatibility concerns.

### 1. Spring Boot (3.2.12) → 4.0.0-RC1
**Current Version:** 3.2.12 (LTS)  
**Available Version:** 4.0.0-RC1 (Pre-release)  
**Status:** ⛔ HELD - Waiting for stable release

**Reason:**
- 4.0.0-RC1 is still in Release Candidate status (pre-release)
- Major version change (3.x → 4.x) with breaking changes expected
- Affects ALL Spring Boot starters:
  - `spring-boot-starter-web`
  - `spring-boot-starter-data-jpa`
  - `spring-boot-starter-validation`
  - `spring-boot-starter-test`

**Recommendation:**
- Wait for official 4.0.0 GA (General Availability) release
- Plan separate major version upgrade with full regression testing
- Expected stable release: Check https://spring.io/projects/spring-boot for official timeline
- Estimated safe date: Q1 2026 after community feedback and stabilization

**Migration Checklist (for future upgrade):**
- [ ] Review Spring Boot 4.0 Migration Guide: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
- [ ] Check compatibility with all current dependencies
- [ ] Review deprecations from Spring 3.5.x branch
- [ ] Plan for breaking changes in test configurations
- [ ] Schedule comprehensive regression testing

---

### 2. Testcontainers (1.19.8) → 2.0.1
**Current Version:** 1.19.8 (Stable)  
**Available Version:** 2.0.1 (Major version)  
**Status:** ⛔ HELD - Waiting for wider adoption and stabilization

**Reason:**
- Major version change with breaking API changes
- Significant changes include:
  - Module namespace changes (`org.testcontainers:mysql` → `org.testcontainers:testcontainers-mysql`)
  - Container class package relocations
  - Removed JUnit 4 support
  - Docker image specification changes

**Current Dependencies Affected:**
- `org.testcontainers:testcontainers` (1.21.3 - staying at 1.x)
- `org.testcontainers:junit-jupiter` (1.21.3 - staying at 1.x)
- `org.testcontainers:postgresql` (1.21.3 - staying at 1.x)

**Recommendation:**
- Stay on 1.21.3 for now (latest stable 1.x release)
- Monitor Testcontainers 2.x adoption in the community
- Plan 2.0.x migration for Q2/Q3 2026 after:
  - [ ] More projects have successfully upgraded
  - [ ] Documentation is mature
  - [ ] Edge cases are well-documented
  - [ ] Community feedback has been addressed

**Breaking Changes to Review (for future upgrade):**
- Module naming conventions change
- Class package relocations
- Gradle/Maven coordinate changes
- JUnit 4 support completely removed
- Docker image configuration approach changes

---

## Successfully Updated Dependencies ✅

These dependencies were updated in the recent upgrade cycle:

| Dependency | From | To | Status |
|-----------|------|-----|--------|
| PostgreSQL JDBC Driver | 42.5.0 | 42.7.8 | ✅ Deployed |
| JUnit Jupiter | 5.10.2 | 6.0.1 | ✅ Deployed |
| Testcontainers (junit-jupiter) | 1.19.8 | 1.21.3 | ✅ Deployed |
| Testcontainers (postgresql) | 1.19.8 | 1.21.3 | ✅ Deployed |
| Testcontainers (core) | 1.19.8 | 1.21.3 | ✅ Deployed |

---

## Monitoring Plan

### Weekly Checks
- Monitor Spring Boot release announcements for GA release
- Check Testcontainers GitHub releases for 2.0.x stabilization

### Quarterly Reviews
- Review held dependencies in business context
- Evaluate if migration windows are available
- Assess community adoption metrics

### Trigger for Immediate Evaluation
- Security vulnerability in held dependency versions
- Breaking change affecting our implementation
- End-of-life announcement for current versions

---

## Related Documentation

- See `DEPENDENCY_UPDATES.md` for full dependency analysis
- See git commit history for detailed upgrade decisions
- See `pom.xml` for current versions

### Useful Links
- Spring Boot 4.0 Release Notes: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes
- Spring Boot 4.0 Migration Guide: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
- Testcontainers 2.0.0 Breaking Changes: https://github.com/testcontainers/testcontainers-java/releases/tag/2.0.0
- Testcontainers Documentation: https://java.testcontainers.org/
