package com.example.reflection.web.frontend.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;

/**
 * Page Object for V1 Dashboard (v1-ui.html).
 * Provides methods for interacting with the API V1 user interface.
 * 
 * Follows official Playwright pattern: receives Page instance from test.
 */
public class V1DashboardPage {
    
    private final Page page;
    private final String baseUrl;
    
    public V1DashboardPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }
    
    // ========== Navigation ==========
    
    /**
     * Navigate to V1 dashboard and wait for page to load.
     */
    public void navigate() {
        page.navigate(baseUrl + "/v1-ui.html");
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }
    
    // ========== Create Form Actions ==========
    
    /**
     * Fill the create form with sample data.
     * 
     * @param text Sample text (3-100 chars)
     * @param number Sample number (0-1000)
     * @param status Status ("active" or "inactive")
     * @param extras Optional JSON extras (can be null)
     */
    public void fillCreateForm(String text, int number, String status, String extras) {
        page.locator("#text").fill(text);
        page.locator("#number").fill(String.valueOf(number));
        page.locator("#status").selectOption(status);
        
        if (extras != null && !extras.isEmpty()) {
            page.locator("#extras").fill(extras);
        }
    }
    
    /**
     * Submit the create form.
     */
    public void submitCreateForm() {
        page.locator("#createForm button[type='submit']").click();
    }
    
    /**
     * Clear the create form.
     */
    public void clearCreateForm() {
        page.locator("#text").clear();
        page.locator("#number").clear();
        page.locator("#extras").clear();
    }
    
    // ========== Get By ID Actions ==========
    
    /**
     * Fill the "Get by ID" form.
     * 
     * @param id Sample ID to retrieve
     */
    public void fillGetByIdForm(long id) {
        page.locator("#sampleId").fill(String.valueOf(id));
    }
    
    /**
     * Submit the "Get by ID" form.
     */
    public void submitGetByIdForm() {
        page.locator("#getByIdForm button[type='submit']").click();
    }
    
    // ========== List Actions ==========
    
    /**
     * Click the refresh button to reload samples list.
     */
    public void clickRefresh() {
        page.locator("button:has-text('Refresh')").click();
        // Wait a moment for the refresh to trigger
        page.waitForTimeout(500);
    }
    
    /**
     * Wait for samples list to finish loading.
     */
    public void waitForSamplesLoaded() {
        // Wait for loading to disappear and content to appear
        try {
            page.locator("#samplesList p:has-text('Loading')").waitFor(new Locator.WaitForOptions().setTimeout(1000));
            page.locator("#samplesList p:has-text('Loading')").waitFor(new Locator.WaitForOptions().setState(com.microsoft.playwright.options.WaitForSelectorState.HIDDEN));
        } catch (Exception e) {
            // Loading may have already finished
        }
        // Give a small amount of time for content to render
        page.waitForTimeout(500);
    }
    
    // ========== Queries ==========
    
    /**
     * Get success message text from create form.
     * @return Success message or null if not displayed
     */
    public String getCreateSuccessMessage() {
        Locator success = page.locator("#createMessage .message.success");
        try {
            success.waitFor(new Locator.WaitForOptions().setTimeout(5000));
            return success.textContent().trim();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Get error message text from create form.
     * @return Error message or null if not displayed
     */
    public String getCreateErrorMessage() {
        Locator error = page.locator("#createMessage .message.error");
        try {
            error.waitFor(new Locator.WaitForOptions().setTimeout(5000));
            return error.textContent().trim();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Get success/error message from "Get by ID" section.
     * @return Message text or null
     */
    public String getByIdMessage() {
        Locator messageArea = page.locator("#getByIdMessage");
        // Wait for message to appear (async fetch completes)
        try {
            Locator message = page.locator("#getByIdMessage .message");
            message.waitFor(new Locator.WaitForOptions().setTimeout(5000));
            return message.textContent().trim();
        } catch (Exception e) {
            // If no message appears within timeout
            if (messageArea.count() > 0) {
                String text = messageArea.textContent().trim();
                return text.isEmpty() ? null : text;
            }
            return null;
        }
    }
    
    /**
     * Get the detail text from "Get by ID" result.
     * @return Detail text or null
     */
    public String getByIdDetail() {
        Locator detail = page.locator("#sampleDetail");
        if (detail.count() > 0 && detail.isVisible()) {
            return detail.textContent().trim();
        }
        return null;
    }
    
    /**
     * Check if samples list is empty.
     * @return true if empty state is shown
     */
    public boolean isSamplesListEmpty() {
        Locator emptyState = page.locator("#samplesList .empty-state");
        return emptyState.count() > 0 && emptyState.isVisible();
    }
    
    /**
     * Check if samples list is showing loading state.
     * @return true if loading indicator is visible
     */
    public boolean isSamplesListLoading() {
        Locator loading = page.locator("#samplesList p:has-text('Loading')");
        return loading.count() > 0 && loading.isVisible();
    }
    
    /**
     * Get count of samples in the list.
     * @return Number of sample items displayed
     */
    public int getSamplesCount() {
        return page.locator("#samplesList .sample-item").count();
    }
    
    /**
     * Get text content of all samples (for assertions).
     * @return Full text of samples list
     */
    public String getSamplesListText() {
        return page.locator("#samplesList").textContent();
    }
    
    /**
     * Check if a specific sample ID appears in the list.
     * @param id Sample ID to look for
     * @return true if sample is visible in list
     */
    public boolean isSampleInList(long id) {
        String listText = getSamplesListText();
        return listText.contains("ID: " + id);
    }
    
    /**
     * Get page title.
     */
    public String getPageTitle() {
        return page.title();
    }
    
    /**
     * Get the version badge text.
     */
    public String getVersionBadge() {
        return page.locator(".version-badge").textContent();
    }
    
    /**
     * Check if specific element is visible.
     */
    public boolean isElementVisible(String selector) {
        Locator element = page.locator(selector);
        return element.count() > 0 && element.isVisible();
    }
}
