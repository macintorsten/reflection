---
name: playwright-frontend-testing
description: End-to-end frontend testing with Playwright Java and Chromium. Use for UI validation, form testing, API interaction verification, and screenshot capture on failures.
---

# Playwright Frontend Testing

End-to-end testing for vanilla JavaScript frontends using Playwright Java with Chromium.

## Quick Start

```bash
# Ensure Playwright installed (one-time setup)
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps chromium"

# Run all frontend tests
mvn test -Dtest="*FrontendTest"

# Run specific test
mvn test -Dtest="V1DashboardFrontendTest"

# Debug with visible browser
HEADED=true mvn test -Dtest="V1DashboardFrontendTest"
```

## Test Structure

**Test class:**
- Extends: `AbstractFrontendTest` (provides browser, page, baseUrl)
- Location: `src/test/java/.../web/frontend/{Feature}FrontendTest.java`
- Uses: `@SpringBootTest(webEnvironment = RANDOM_PORT)` with real backend

**Page Object:**
- Constructor: `public PageObject(Page page, String baseUrl)`
- Location: `src/test/java/.../web/frontend/pages/{Feature}Page.java`
- Pattern: One Page Object per HTML page

**Example test:**
```java
@Test
void shouldCreateSampleWhenFormSubmitted() {
    // Given
    DashboardPage page = new DashboardPage(this.page, baseUrl);
    page.navigate();
    
    // When
    page.fillForm("Test", 42, "active");
    page.submitForm();
    
    // Then
    assertThat(page.getSuccessMessage()).contains("created");
}
```

## Page Object Pattern

```java
public class DashboardPage {
    private final Page page;
    private final String baseUrl;
    
    public DashboardPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }
    
    public void navigate() {
        page.navigate(baseUrl + "/dashboard.html");
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }
    
    public void fillForm(String text, int number, String status) {
        page.locator("#text").fill(text);
        page.locator("#number").fill(String.valueOf(number));
        page.locator("#status").selectOption(status);
    }
    
    public String getSuccessMessage() {
        Locator msg = page.locator(".message.success");
        return msg.count() > 0 ? msg.textContent() : null;
    }
}
```

## Key Patterns

**Waiting:** Use Playwright's auto-waiting (built-in). For specific needs:
```java
page.locator("#result").waitFor();  // Wait for element
page.waitForLoadState(LoadState.NETWORKIDLE);  // Wait for network
```

**Screenshots on failure:** Automatic via `AbstractFrontendTest.captureScreenshotOnFailure()`. Files saved to `target/playwright-screenshots/`.

**Test isolation:** Each test gets fresh `BrowserContext` and `Page` (per-test cleanup in base class).

## Adding New Frontend Tests

1. Create HTML in `src/main/resources/static/`
2. Create Page Object in `src/test/java/.../web/frontend/pages/`
3. Create test class extending `AbstractFrontendTest`
4. Write tests following Given-When-Then pattern
5. Run: `mvn test -Dtest="YourNewFrontendTest"`

## Configuration

**Base test class:**
- `@TestInstance(PER_CLASS)` - Browser shared across tests (performance)
- `@BeforeEach` - Fresh context/page per test (isolation)
- `@AfterEach` - Screenshot on failure, close context

**Environment variables:**
- `HEADED=true` - Run with visible browser (debugging)
- `SLOWMO=1000` - Slow down actions by 1000ms (debugging)

## CI Integration

Frontend tests run automatically in CI. Screenshots uploaded as artifacts on failure:

```yaml
- name: Run frontend tests
  run: mvn test -Dtest="*FrontendTest"

- name: Upload screenshots on failure
  if: failure()
  uses: actions/upload-artifact@v4
  with:
    name: playwright-failure-screenshots
    path: target/playwright-screenshots/
```

## Reference

**Official docs:** https://playwright.dev/java/docs/test-runners  
**Test examples:** See `src/test/java/.../web/frontend/V1DashboardFrontendTest.java`  
**Base class:** See `src/test/java/.../web/frontend/AbstractFrontendTest.java`
