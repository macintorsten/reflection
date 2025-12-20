package com.example.reflection.web.frontend.pages;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;

/**
 * Page Object template for frontend pages.
 * Copy and customize this template for each page you want to test.
 * 
 * Replace {{PAGE_NAME}} with your page name (e.g., V1Dashboard, ProductCatalog)
 * Replace {{page_url}} with the URL path (e.g., v1-ui.html, products.html)
 */
public class {{PAGE_NAME}}Page {
    
    private final Page page;
    private final String baseUrl;
    
    /**
     * Constructor creates new browser page.
     * @param browser Playwright browser instance
     * @param baseUrl Base URL (e.g., http://localhost:8080)
     */
    public {{PAGE_NAME}}Page(Browser browser, String baseUrl) {
        this.page = browser.newPage();
        this.baseUrl = baseUrl;
    }
    
    // ========== Navigation ==========
    
    /**
     * Navigate to this page and wait for it to load.
     */
    public void navigate() {
        page.navigate(baseUrl + "/{{page_url}}");
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }
    
    /**
     * Close the page when done (call in @AfterEach).
     */
    public void close() {
        page.close();
    }
    
    // ========== Locators (private) ==========
    
    // Example locators - customize for your page
    private Locator textInput() {
        return page.locator("#text");
    }
    
    private Locator numberInput() {
        return page.locator("#number");
    }
    
    private Locator submitButton() {
        return page.locator("button[type='submit']");
    }
    
    private Locator successMessage() {
        return page.locator(".message.success");
    }
    
    private Locator errorMessage() {
        return page.locator(".message.error");
    }
    
    // ========== Actions ==========
    
    /**
     * Fill form with provided data.
     * Customize parameters for your form fields.
     */
    public void fillForm(String text, int number) {
        textInput().fill(text);
        numberInput().fill(String.valueOf(number));
    }
    
    /**
     * Submit the form.
     */
    public void submitForm() {
        submitButton().click();
    }
    
    /**
     * Click refresh button (if applicable).
     */
    public void clickRefresh() {
        page.locator("#refreshBtn").click();
    }
    
    // ========== Queries (for assertions) ==========
    
    /**
     * Get success message text.
     * @return Message text or null if not displayed
     */
    public String getSuccessMessage() {
        if (successMessage().count() > 0) {
            return successMessage().textContent();
        }
        return null;
    }
    
    /**
     * Get error message text.
     * @return Error message or null if not displayed
     */
    public String getErrorMessage() {
        if (errorMessage().count() > 0) {
            return errorMessage().textContent();
        }
        return null;
    }
    
    /**
     * Wait for specific element to be visible.
     * Use this for dynamic content that loads via API.
     */
    public void waitForElementVisible(String selector) {
        page.locator(selector).waitFor();
    }
    
    /**
     * Get count of items in a list.
     * @param listSelector CSS selector for list container
     * @return Number of items
     */
    public int getListItemCount(String listSelector) {
        return page.locator(listSelector + " > *").count();
    }
    
    // ========== Advanced ==========
    
    /**
     * Get page title.
     */
    public String getTitle() {
        return page.title();
    }
    
    /**
     * Check if element is visible.
     */
    public boolean isVisible(String selector) {
        return page.locator(selector).isVisible();
    }
    
    /**
     * Get text content of element.
     */
    public String getText(String selector) {
        return page.locator(selector).textContent();
    }
}
