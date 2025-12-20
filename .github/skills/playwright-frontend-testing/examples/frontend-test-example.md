# Example: Running a Frontend Test

This example demonstrates the full workflow of creating and running a frontend test using the Playwright Frontend Testing skill.

## Scenario: Testing the V1 Dashboard Create Form

### 1. Discover Testable Components

```bash
cd .github/skills/playwright-frontend-testing
./scripts/discover-frontend-tests.sh
```

**Output:**
```
=== Frontend Test Discovery ===

📄 HTML Pages Found:
  - v1-ui.html
    Title: Sample API V1 - Dashboard
    Forms: 2
    Buttons: 3
    API: const API_BASE = '/api/v1/samples'

  - v2-ui.html
    Title: Sample API V2 - Dashboard with Analytics
    Forms: 1
    Buttons: 2
    API: const API_BASE = '/api/v2/samples'

📊 Testable Components:
  Forms:
    - createForm
    - getByIdForm

  JavaScript Functions:
    - loadSamples
    - loadStatistics
    - renderSampleDetail
    - getPriorityBadge

💡 Suggested Test Scenarios:
  - Form submission with valid data
  - Form validation with invalid data
  - Dynamic content loading (API calls)
  - Error message display
  - Success message display
```

### 2. Review Existing Test (Already Created)

The V1Dashboard tests are already implemented in:
- Page Object: `src/test/java/com/example/reflection/web/frontend/pages/V1DashboardPage.java`
- Test Class: `src/test/java/com/example/reflection/web/frontend/V1DashboardFrontendTest.java`

### 3. Run the Frontend Tests

```bash
# Run all frontend tests
mvn test -Dtest="*FrontendTest"

# Run only V1 tests
mvn test -Dtest="V1DashboardFrontendTest"

# Run specific test
mvn test -Dtest="V1DashboardFrontendTest#shouldCreateSampleWhenValidDataProvided"
```

### 4. Test Output

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.example.reflection.web.frontend.V1DashboardFrontendTest
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 8.234 s

Results:
Tests run: 11, Failures: 0, Errors: 0, Skipped: 0

✅ No screenshots (tests passed)
```

### 5. Test Failure with Screenshot

If a test fails, a screenshot is automatically captured:

```bash
mvn test -Dtest="V1DashboardFrontendTest#shouldCreateSampleWhenValidDataProvided"
```

**Output when test fails:**
```
[ERROR] Failures: 
[ERROR]   V1DashboardFrontendTest.shouldCreateSampleWhenValidDataProvided:67
  Expecting actual:
    null
  to contain:
    "created"
   
📸 Screenshot saved: target/playwright-screenshots/shouldCreateSampleWhenValidDataProvided().png

[INFO] 
[ERROR] Tests run: 1, Failures: 1, Errors: 0, Skipped: 0
```

### 6. View Screenshot

```bash
# List screenshots
ls -lh target/playwright-screenshots/

# View in browser (on local machine)
open target/playwright-screenshots/shouldCreateSampleWhenValidDataProvided().png
```

### 7. Debug with Headed Mode

To see the browser during test execution:

```bash
HEADED=true mvn test -Dtest="V1DashboardFrontendTest#shouldCreateSampleWhenValidDataProvided"
```

This opens Chromium browser visibly so you can watch the test execute.

### 8. Slow Motion Debug

To slow down test execution for debugging:

```bash
HEADED=true SLOWMO=1000 mvn test -Dtest="V1DashboardFrontendTest"
```

This adds a 1-second delay between each action.

## Test Code Example

Here's the actual test from `V1DashboardFrontendTest.java`:

```java
@Test
@DisplayName("Should create sample when valid data provided")
void shouldCreateSampleWhenValidDataProvided() {
    // Given
    page.navigate();
    String sampleText = "Frontend Test Sample";
    
    // When
    page.fillCreateForm(sampleText, 42, "active", null);
    page.submitCreateForm();
    
    // Then
    page.waitForSamplesLoaded();
    String successMessage = page.getCreateSuccessMessage();
    assertThat(successMessage).isNotNull();
    assertThat(successMessage).contains("created");
    
    // Verify sample appears in list after refresh
    page.clickRefresh();
    page.waitForSamplesLoaded();
    assertThat(page.getSamplesListText()).contains(sampleText);
}
```

## Page Object Pattern

The test uses a Page Object (`V1DashboardPage`) to encapsulate UI interactions:

```java
public class V1DashboardPage {
    private final Page page;
    private final String baseUrl;
    
    // Navigation
    public void navigate() {
        page.navigate(baseUrl + "/v1-ui.html");
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }
    
    // Actions
    public void fillCreateForm(String text, int number, String status, String extras) {
        page.locator("#text").fill(text);
        page.locator("#number").fill(String.valueOf(number));
        page.locator("#status").selectOption(status);
        if (extras != null) page.locator("#extras").fill(extras);
    }
    
    // Queries
    public String getCreateSuccessMessage() {
        Locator success = page.locator("#createMessage .message.success");
        return success.count() > 0 ? success.textContent() : null;
    }
}
```

## CI Integration

In GitHub Actions, frontend tests run automatically:

```yaml
- name: Set up Node.js
  uses: actions/setup-node@v4
  with:
    node-version: '20'

- name: Install Playwright browsers
  run: mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps chromium"

- name: Run tests with Maven
  run: mvn clean verify

- name: Upload Playwright screenshots on failure
  if: failure()
  uses: actions/upload-artifact@v4
  with:
    name: playwright-failure-screenshots
    path: target/playwright-screenshots/
```

When tests fail in CI, screenshots are uploaded as artifacts for debugging.

## Summary

The Playwright Frontend Testing skill provides:
- **Automated browser testing** with Chromium
- **Page Object Model** for maintainable tests
- **Screenshot capture** on test failures
- **CI/CD integration** with GitHub Actions
- **Easy debugging** with headed and slow-motion modes
- **Maven integration** for consistent test execution

All frontend tests follow the same patterns as backend tests (Given-When-Then, descriptive names, independent tests).
