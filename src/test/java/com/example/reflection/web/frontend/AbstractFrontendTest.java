package com.example.reflection.web.frontend;

import com.example.reflection.AbstractIntegrationTest;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Base class for frontend integration tests using Playwright.
 * Follows official Playwright Java best practices for test structure.
 * 
 * Features:
 * - Starts Spring Boot on random port (RANDOM_PORT) for reliable test execution
 * - Shares Playwright and Browser instances across all tests in class (performance)
 * - Creates isolated BrowserContext and Page per test (proper isolation)
 * - Automatically captures screenshots on test failures
 * - Extends AbstractIntegrationTest for shared PostgreSQL database
 * 
 * Pattern (from https://playwright.dev/java/docs/test-runners):
 * - @TestInstance(PER_CLASS) allows sharing Playwright/Browser across tests
 * - Each test gets fresh BrowserContext and Page for isolation
 * - Browser state is NOT shared between tests
 * 
 * Usage:
 * <pre>
 * class MyPageFrontendTest extends AbstractFrontendTest {
 *     {@literal @}Test
 *     void shouldDoSomething() {
 *         MyPage myPage = new MyPage(page);
 *         myPage.navigate();
 *         // ... test logic
 *     }
 * }
 * </pre>
 * 
 * Environment Variables:
 * - HEADED=true : Run browser in headed mode (visible window)
 * - SLOWMO=1000 : Slow down operations by N milliseconds (for debugging)
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractFrontendTest extends AbstractIntegrationTest {
    
    // Shared between all tests in this class (PER_CLASS lifecycle)
    @LocalServerPort
    protected int port;
    
    protected String baseUrl;
    protected Playwright playwright;
    protected Browser browser;
    
    // New instance for each test method (proper isolation)
    protected BrowserContext context;
    protected Page page;
    
    /**
     * Launch Playwright and browser once before all tests in the class.
     * Browser is reused for performance (starting browser takes ~1 second).
     */
    @BeforeAll
    void launchBrowser() {
        playwright = Playwright.create();
        
        // Check for HEADED environment variable
        boolean headless = System.getenv("HEADED") == null;
        
        // Check for SLOWMO environment variable (for debugging)
        String slowMoStr = System.getenv("SLOWMO");
        double slowMo = slowMoStr != null ? Double.parseDouble(slowMoStr) : 0;
        
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
            .setHeadless(headless);
        
        if (slowMo > 0) {
            options.setSlowMo(slowMo);
        }
        
        browser = playwright.chromium().launch(options);
        baseUrl = "http://localhost:" + port;
    }
    
    /**
     * Close browser and Playwright after all tests complete.
     */
    @AfterAll
    void closeBrowser() {
        if (playwright != null) {
            playwright.close();
        }
    }
    
    /**
     * Create fresh BrowserContext and Page before each test.
     * This ensures complete isolation between tests (no shared state).
     */
    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext();
        page = context.newPage();
    }
    
    /**
     * Close BrowserContext after each test.
     * Captures screenshot on test failure before closing.
     * 
     * @param testInfo JUnit TestInfo for test metadata
     */
    @AfterEach
    void closeContext(TestInfo testInfo) {
        context.close();
    }
    
    /**
     * Capture screenshot on test failure.
     * Screenshots are saved to target/playwright-screenshots/ directory.
     * This directory is uploaded as CI artifacts when tests fail.
     */
    private void captureScreenshot(TestInfo testInfo) {
        try {
            Path screenshotDir = Paths.get("target/playwright-screenshots");
            Files.createDirectories(screenshotDir);
            
            String testName = testInfo.getDisplayName()
                .replaceAll("[^a-zA-Z0-9.-]", "_");
            Path screenshotPath = screenshotDir.resolve(testName + ".png");
            
            page.screenshot(new Page.ScreenshotOptions()
                .setPath(screenshotPath)
                .setFullPage(true));
            
            System.out.println("📸 Screenshot saved: " + screenshotPath);
        } catch (Exception e) {
            System.err.println("⚠️ Failed to capture screenshot: " + e.getMessage());
        }
    }
}
