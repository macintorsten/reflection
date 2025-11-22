# Maven Dependency Review - 2025-11-22T13-32-24

## Dependencies to Update

| Dependency | Current Version | Available Version | Summary |
|------------|-----------------|-------------------|---------|
| com.fasterxml.jackson.core:jackson-annotations | 2.16.1 | 2.20 | **Release Notes:** https://github.com/FasterXML/jackson/wiki/Jackson-Release-2.17 • https://github.com/FasterXML/jackson/wiki/Jackson-Release-2.18 • https://github.com/FasterXML/jackson/wiki/Jackson-Release-2.19 • https://github.com/FasterXML/jackson/wiki/Jackson-Release-2.20<br><br>**CVEs/Security:** None<br><br>**Breaking Changes:**<br>• 2.20: Dropped Java 6 compatibility - Java 8 now minimum<br>• 2.20: Version numbering changed to 2.20 instead of 2.20.0<br>• 2.17: @JsonAnySetter expanded to ElementType.PARAMETER<br><br>**Major Features:**<br>• 2.20: Added optional property for @JacksonInject<br>• 2.20: Added @JsonProperty.isRequired with OptBoolean<br><br>**Notes:** Requires Java 8+. Spans 4 major versions. Version 2.18 is LTS. |
| com.fasterxml.jackson.core:jackson-core<br>com.fasterxml.jackson.core:jackson-databind | 2.16.1 | 2.20.1 | **Release Notes:** https://github.com/FasterXML/jackson/wiki/Jackson-Release-2.17 • https://github.com/FasterXML/jackson/wiki/Jackson-Release-2.18 • https://github.com/FasterXML/jackson/wiki/Jackson-Release-2.19 • https://github.com/FasterXML/jackson/wiki/Jackson-Release-2.20<br><br>**CVEs/Security:** None<br><br>**Breaking Changes:**<br>• 2.18: Complete rewrite of POJO Property Introspection (databind)<br>• 2.20: Dropped deprecated PropertyNamingStrategy (databind)<br>• 2.17: Changed default RecyclerPool (core)<br>• 2.19: JsonPointer parsing behavior change (core)<br><br>**Major Features:**<br>• 2.19: @JsonUnwrapped with @JsonCreator support (databind)<br>• 2.18: Max token count limit for DoS prevention (core)<br>• 2.18: Performance improvements for floating-point parsing (core)<br><br>**Notes:** Major update spanning 4 versions. 2.18 is LTS with 8+ years of introspection redesign. Java 8+ required in 2.20. Thorough testing recommended. |
| com.google.guava:guava | 33.0.0-jre | 33.5.0-jre | **Release Notes:** https://github.com/google/guava/releases/tag/v33.1.0 • https://github.com/google/guava/releases/tag/v33.2.0 • https://github.com/google/guava/releases/tag/v33.3.0 • https://github.com/google/guava/releases/tag/v33.4.0 • https://github.com/google/guava/releases/tag/v33.5.0<br><br>**CVEs/Security:** None<br><br>**Breaking Changes:**<br>• 33.4.1+: Replaced @ElementTypesAreNonnullByDefault with JSpecify @NullMarked (stricter nullness checking)<br>• 33.4.5+: Became Java module (JPMS) requiring module-info.java updates<br>• 33.2.1: IPv6 scope ID preservation change<br>• 33.5.0: Android minSdkVersion increased to 23<br><br>**Major Features:**<br>• 33.2.0: Collector APIs available in guava-android<br>• 33.4.0+: Extensive Java 8+ API exposure to Android<br><br>**Notes:** 15 versions behind. Nullness annotations may trigger new errors. Module system requires 'requires com.google.common;'. Skip 33.4.5-33.4.7 (bugs fixed in 33.4.8). |
| org.apache.commons:commons-lang3 | 3.14.0 | 3.20.0 | **Release Notes:** https://github.com/apache/commons-lang/blob/master/RELEASE-NOTES.txt<br><br>**CVEs/Security:** CVE-2025-48924: ClassUtils.getClass() StackOverflowError on very long inputs (fixed in 3.18.0)<br><br>**Breaking Changes:**<br>• 3.17.0: Deprecated static RandomUtils/RandomStringUtils methods for secure()/insecure() versions<br>• 3.16.0: StopWatch internals use java.time, deprecated getTime() for getDuration()<br><br>**Major Features:**<br>• 3.19.0: Null-safe mapping/chaining methods in Functions/Failable<br>• 3.18.0: New Strings and Predicates utility classes<br>• 3.17.0: Explicit secure()/insecure() random generation<br><br>**Notes:** 6 versions behind. Security fix in 3.18.0 for DoS vulnerability. RandomUtils API changes require explicit security choice. Java 8+ required. |
| org.apache.httpcomponents.client5:httpclient5 | 5.3.1 | 5.5.1 | **Release Notes:** https://raw.githubusercontent.com/apache/httpcomponents-client/master/RELEASE_NOTES.txt • https://hc.apache.org/news.html<br><br>**CVEs/Security:** None<br><br>**Breaking Changes:**<br>• 5.5: No auto-redirect with sensitive headers (prevents credential leakage)<br>• 5.4: Cache entry serialization format incompatible with pre-5.4 (must flush caches)<br>• 5.4: ConnectionSocketFactory deprecated for DefaultClientTlsStrategy<br><br>**Major Features:**<br>• 5.5: Experimental HTTP/2 request multiplexing<br>• 5.4: Complete HTTP caching redesign per RFC 9111<br>• 5.4: TLS SNI and endpoint identification improvements<br><br>**Notes:** Persistent caches must be flushed when upgrading from pre-5.4. Review redirect handling with Authorization headers. Java 8u251+ required. HTTP/2 features experimental. |
| org.apache.httpcomponents.core5:httpcore5<br>org.apache.httpcomponents.core5:httpcore5-h2 | 5.2.4 | 5.3.6 | **Release Notes:** https://github.com/apache/httpcomponents-core/blob/master/RELEASE_NOTES.txt<br><br>**CVEs/Security:** 5.3.5 fixes HTTP/2 DoS vulnerabilities: End Stream Headers exploit, CONTINUATION frame limits, resource exhaustion<br><br>**Breaking Changes:**<br>• 5.3: Stricter RFC 9110/9112 compliance (status codes, host validation, TLS enforcement)<br>• 5.3: TLS endpoints use JSSE Endpoint Identification by default<br>• 5.3: Replaced synchronized blocks with ReentrantLock (Virtual Threads)<br><br>**Major Features:**<br>• 5.3.5: HTTP/2 DoS protection and resource exhaustion fixes<br>• 5.3: RFC 9110/9112 conformance improvements<br>• 5.3: Java Virtual Threads compatibility<br><br>**Notes:** Critical HTTP/2 security fixes in 5.3.5. 8 releases behind. HPackDecoder OOM fix in 5.2.5. Multiple HTTP/2 stability improvements. Upgrade to 5.3.6 strongly recommended. |
| org.assertj:assertj-core | 3.24.2 | 3.27.6 | **Release Notes:** https://github.com/assertj/assertj/releases<br><br>**CVEs/Security:** None<br><br>**Breaking Changes:**<br>• 3.27.3: Reverted extracting() basetype propagation (from 3.27.0)<br>• 3.26.3: Replaced assertThat(Temporal) with assertThatTemporal() (binary incompatible with 3.26.0-3.26.2)<br>• 3.26.0: OptionalDouble uses Double.compare vs equality<br>• 3.26.0: Multiple deprecations for v4.0 prep<br><br>**Major Features:**<br>• 3.27.0: Added actual() method to access object under test<br>• 3.27.0: Duration support for temporal comparisons<br>• 3.26.0: Comprehensive YearMonth assertions<br><br>**Notes:** 14 versions behind. 3.26.3 binary incompatible with 3.26.0-3.26.2 but compatible with 3.24.x. Review deprecations for v4.0 prep. Test-only dependency. |
| org.mockito:mockito-core | 5.7.0 | 5.20.0 | **Release Notes:** https://github.com/mockito/mockito/releases<br><br>**CVEs/Security:** None<br><br>**Breaking Changes:**<br>• 5.16.0: Added module-info for Java Module System<br>• 5.13.0: Bumped Hamcrest 2.2 to 3.0<br><br>**Major Features:**<br>• 5.20.0: Mock construction of generic types<br>• 5.19.0/5.20.0: JDK 21 Sequenced Collections support<br>• 5.16.0: Full Java Module System support<br>• 5.14.0: Java agent self-install improvements<br><br>**Notes:** 13 versions behind. Byte Buddy updated 1.14.9→1.17.7. Java 8+ required. Module system support may require module-info adjustments. Test-only dependency. |
| org.projectlombok:lombok | 1.18.30 | 1.18.42 | **Release Notes:** https://projectlombok.org/changelog • https://github.com/projectlombok/lombok/releases<br><br>**CVEs/Security:** None<br><br>**Breaking Changes:**<br>• 1.18.40: No longer auto-copies Jackson annotations to accessors (use lombok.copyJacksonAnnotationsToAccessors=true to restore)<br>• 1.18.34: Adds @lombok.Generated by default (may increase test coverage %)<br>• 1.18.32: JSpecify package changed org.jspecify.nullness→org.jspecify.annotations<br><br>**Major Features:**<br>• 1.18.40: JDK25 support, @Jacksonized with fluent accessors<br>• 1.18.38: JDK24 support, JSpecify nullity out-of-box<br>• 1.18.36: JDK23 support<br>• 1.18.32: JDK22 support, new @Locked annotation<br><br>**Notes:** 6 versions behind covering ~2 years. Jackson annotation behavior change in 1.18.40 most significant. JDK22-25 support added. |
| org.slf4j:slf4j-api | 2.0.9 | 2.0.17 | **Release Notes:** https://www.slf4j.org/news.html • https://github.com/qos-ch/slf4j/releases<br><br>**CVEs/Security:** None<br><br>**Breaking Changes:**<br>• 2.0.10: Removed Util.report methods (restored in 2.0.12)<br><br>**Major Features:** None<br><br>**Notes:** 8 versions behind. Maintenance update with bug fixes only. Key fixes: MDC initialization race condition (2.0.17), module system support for Jigsaw (2.0.12), provider info logging (2.0.16). Low-risk update, fully backward compatible. No migration required. |

## Verification Status

**Verification Date:** 2025-11-22  
**Status:** ✅ VERIFIED

### Verification Results

#### 1. Count Verification
- Groups in `dependency_groups.jsonl`: **10**
- Table rows in report: **10**
- **✅ PASS**: Counts match perfectly

#### 2. URL Validation
- Total URLs checked: **19**
- Broken URLs: **0**
- **✅ PASS**: All URLs accessible (HTTP 200)

#### 3. Structural Validation
All 10 rows validated with required sections:
- ✅ Release Notes (with URLs)
- ✅ CVEs/Security (including "None" where applicable)
- ✅ Breaking Changes (clearly marked)
- ✅ Major Features
- ✅ Notes

#### 4. Content Quality
- Summaries: 50-88 words (concise, information-dense)
- All critical information present:
  - Version spans and compatibility
  - Security vulnerabilities (where applicable)
  - Breaking changes with version numbers
  - Key features by version
  - Migration considerations

#### 5. Groups Requiring Re-Research
**None** - All links valid and accessible.

### Overall Assessment
- **Structure**: ✅ EXCELLENT
- **Links**: ✅ 100% valid
- **Content**: ✅ Comprehensive
- **Completeness**: ✅ All sections present
- **Status**: **PRODUCTION READY**
