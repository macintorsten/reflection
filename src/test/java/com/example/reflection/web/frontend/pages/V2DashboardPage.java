package com.example.reflection.web.frontend.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;

/**
 * Page Object for V2 Dashboard (v2-ui.html).
 * Provides methods for interacting with the API V2 user interface,
 * including priority slider and statistics dashboard.
 * 
 * Follows official Playwright pattern: receives Page instance from test.
 */
public class V2DashboardPage {
    
    private final Page page;
    private final String baseUrl;
    
    public V2DashboardPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }
    
    // ========== Navigation ==========
    
    /**
     * Navigate to V2 dashboard and wait for page to load.
     */
    public void navigate() {
        page.navigate(baseUrl + "/v2-ui.html");
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }
    
    // ========== Create Form Actions ==========
    
    /**
     * Fill the create form with sample data.
     * 
     * Note: Metadata is COMPUTED server-side, not user-provided.
     * The metadata parameter exists for API consistency but is ignored.
     * Backend computes metadata from internal mapField structure:
     * - totalValue: sum of mapField values (excluding priority)
     * - itemCount: count of mapField entries (excluding priority)  
     * - tags: mapField keys (excluding priority)
     * 
     * @param text Sample text (3-100 chars)
     * @param number Sample number (0-1000)
     * @param priority Priority (1-10)
     * @param status Status ("active" or "inactive")
     * @param metadata Ignored - metadata is computed server-side (kept for method signature compatibility)
     */
    public void fillCreateForm(String text, int number, int priority, String status, String metadata) {
        page.locator("#text").fill(text);
        page.locator("#number").fill(String.valueOf(number));
        page.locator("#priority").fill(String.valueOf(priority));
        page.locator("#status").selectOption(status);
        
        // Note: No metadata input field - metadata is computed by backend from mapField
    }
    
    /**
     * Set priority using the slider.
     * 
     * @param priority Priority value (1-10)
     */
    public void setPrioritySlider(int priority) {
        page.locator("#priority").fill(String.valueOf(priority));
    }
    
    /**
     * Get current priority slider value.
     * 
     * @return Current priority (1-10)
     */
    public int getPriorityValue() {
        String value = page.locator("#priority").inputValue();
        return Integer.parseInt(value);
    }
    
    /**
     * Get the displayed priority value (next to slider).
     * 
     * @return Priority display text
     */
    public String getPriorityDisplay() {
        return page.locator("#priorityValue").textContent();
    }
    
    /**
     * Submit the create form.
     */
    public void submitCreateForm() {
        page.locator("#createForm button[type='submit']").click();
    }
    
    // ========== Statistics Actions ==========
    
    /**
     * Click the refresh button to reload samples and statistics.
     */
    public void clickRefresh() {
        page.locator("button:has-text('Refresh')").click();
        // Wait for refresh to complete
        page.waitForTimeout(1000);
    }
    
    /**
     * Wait for statistics to load and update.
     */
    public void waitForStatisticsLoaded() {
        page.locator(".stats-grid").waitFor();
        // Wait for stats to update (they start at 0 on page load)
        page.waitForTimeout(500);
    }
    
    // ========== Statistics Queries ==========
    
    /**
     * Get total count from statistics.
     * @return Total samples count
     */
    public String getTotalCount() {
        return page.locator("#statTotal").textContent();
    }
    
    /**
     * Get average number from statistics.
     * @return Average number value
     */
    public String getAverageNumber() {
        return page.locator("#statAvgNum").textContent();
    }
    
    /**
     * Get average priority from statistics.
     * @return Average priority value
     */
    public String getAveragePriority() {
        return page.locator("#statAvgPri").textContent();
    }
    
    /**
     * Get active count from statistics.
     * @return Active samples count
     */
    public String getActiveCount() {
        return page.locator("#statActive").textContent();
    }
    
    /**
     * Get inactive count from statistics.
     * @return Inactive samples count
     */
    public String getInactiveCount() {
        return page.locator("#statInactive").textContent();
    }
    
    /**
     * Check if statistics are displayed.
     * @return true if stats grid is visible
     */
    public boolean areStatisticsVisible() {
        Locator stats = page.locator(".stats-grid");
        return stats.count() > 0 && stats.isVisible();
    }
    
    // ========== Message Queries ==========
    
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
    
    // ========== Samples List Queries ==========
    
    /**
     * Wait for samples list to finish loading.
     */
    public void waitForSamplesLoaded() {
        // Wait for loading to disappear
        try {
            page.locator("#samplesList:has-text('Loading')").waitFor(new Locator.WaitForOptions().setTimeout(1000));
            page.locator("#samplesList:has-text('Loading')").waitFor(new Locator.WaitForOptions().setState(com.microsoft.playwright.options.WaitForSelectorState.HIDDEN));
        } catch (Exception e) {
            // Loading may have already finished
        }
        page.waitForTimeout(500);
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
     * Check if priority badge is visible for a sample.
     * @param priority Priority level to check (high/medium/low)
     * @return true if badge is visible
     */
    public boolean isPriorityBadgeVisible(String priority) {
        Locator badge = page.locator(".priority-badge." + priority);
        return badge.count() > 0;
    }
    
    /**
     * Check if metadata section is visible in a sample.
     * @return true if any metadata is displayed
     */
    public boolean isMetadataVisible() {
        Locator metadata = page.locator(".metadata");
        return metadata.count() > 0;
    }
    
    // ========== Page Info ==========
    
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
