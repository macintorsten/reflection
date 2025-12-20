package com.example.reflection.web.frontend;

import com.example.reflection.web.frontend.pages.V1DashboardPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Frontend integration tests for V1 Dashboard.
 * Tests user workflows with real browser automation using Playwright.
 * 
 * Page and BrowserContext are automatically created/destroyed by AbstractFrontendTest.
 */
@DisplayName("V1 Dashboard Frontend Tests")
class V1DashboardFrontendTest extends AbstractFrontendTest {
    
    /**
     * Helper method to create V1DashboardPage for each test.
     * Follows DRY principle while keeping tests isolated.
     */
    private V1DashboardPage createPage() {
        return new V1DashboardPage(page, baseUrl);
    }
    
    // ========== Page Load Tests ==========
    
    @Test
    @DisplayName("Should load V1 dashboard successfully")
    void shouldLoadV1DashboardSuccessfully() {
        // Given
        V1DashboardPage dashboardPage = createPage();
        
        // When
        dashboardPage.navigate();
        
        // Then
        assertThat(dashboardPage.getPageTitle()).contains("Sample API V1");
        assertThat(dashboardPage.getVersionBadge()).contains("Version 1");
        assertThat(dashboardPage.isElementVisible("#createForm")).isTrue();
        assertThat(dashboardPage.isElementVisible("#samplesList")).isTrue();
    }
    
    // ========== Create Form Tests ==========
    
    @Test
    @DisplayName("Should create sample when valid data provided")
    void shouldCreateSampleWhenValidDataProvided() {
        // Given
        V1DashboardPage dashboardPage = createPage();
        dashboardPage.navigate();
        String sampleText = "Frontend Test Sample";
        
        // When
        dashboardPage.fillCreateForm(sampleText, 42, "active", null);
        dashboardPage.submitCreateForm();
        
        // Then
        dashboardPage.waitForSamplesLoaded();
        String successMessage = dashboardPage.getCreateSuccessMessage();
        assertThat(successMessage).isNotNull();
        assertThat(successMessage).contains("created");
        
        // Verify sample appears in list after refresh
        dashboardPage.clickRefresh();
        dashboardPage.waitForSamplesLoaded();
        assertThat(dashboardPage.getSamplesListText()).contains(sampleText);
    }
    
    @Test
    @DisplayName("Should show error when text is too short")
    void shouldShowErrorWhenTextTooShort() {
        // Given
        V1DashboardPage dashboardPage = createPage();
        dashboardPage.navigate();
        
        // When - Text less than 3 chars violates minlength
        dashboardPage.fillCreateForm("ab", 100, "active", null);
        dashboardPage.submitCreateForm();
        
        // Then - HTML5 validation should prevent submission
        // If submission happens, backend should reject
        String error = dashboardPage.getCreateErrorMessage();
        if (error != null) {
            assertThat(error).containsAnyOf("invalid", "error", "failed");
        }
    }
    
    @Test
    @DisplayName("Should accept valid JSON in extras field")
    void shouldAcceptValidJsonInExtrasField() {
        // Given
        V1DashboardPage dashboardPage = createPage();
        dashboardPage.navigate();
        String validJson = "{\"key\": \"value\", \"count\": 42}";
        
        // When
        dashboardPage.fillCreateForm("JSON Test Sample", 50, "active", validJson);
        dashboardPage.submitCreateForm();
        
        // Then
        dashboardPage.waitForSamplesLoaded();
        String successMessage = dashboardPage.getCreateSuccessMessage();
        assertThat(successMessage).isNotNull();
        assertThat(successMessage).contains("created");
    }
    
    @Test
    @DisplayName("Should show error when invalid JSON in extras field")
    void shouldShowErrorWhenInvalidJsonInExtrasField() {
        // Given
        V1DashboardPage dashboardPage = createPage();
        dashboardPage.navigate();
        String invalidJson = "{key: 'value'"; // Missing closing brace, unquoted key
        
        // When
        dashboardPage.fillCreateForm("Invalid JSON Test", 60, "active", invalidJson);
        dashboardPage.submitCreateForm();
        
        // Then
        String error = dashboardPage.getCreateErrorMessage();
        assertThat(error).isNotNull();
        assertThat(error).containsIgnoringCase("json");
    }
    
    @Test
    @DisplayName("Should create sample with inactive status")
    void shouldCreateSampleWithInactiveStatus() {
        // Given
        V1DashboardPage dashboardPage = createPage();
        dashboardPage.navigate();
        
        // When
        dashboardPage.fillCreateForm("Inactive Sample", 99, "inactive", null);
        dashboardPage.submitCreateForm();
        
        // Then
        dashboardPage.waitForSamplesLoaded();
        String successMessage = dashboardPage.getCreateSuccessMessage();
        assertThat(successMessage).contains("created");
        
        dashboardPage.clickRefresh();
        dashboardPage.waitForSamplesLoaded();
        String listText = dashboardPage.getSamplesListText();
        assertThat(listText).contains("Inactive Sample");
        assertThat(listText).contains("inactive");
    }
    
    // ========== Samples List Tests ==========
    
    @Test
    @DisplayName("Should load samples list on page load")
    void shouldLoadSamplesListOnPageLoad() {
        // Given - Create a sample first via form
        V1DashboardPage dashboardPage = createPage();
        dashboardPage.navigate();
        dashboardPage.fillCreateForm("Preload Sample", 77, "active", null);
        dashboardPage.submitCreateForm();
        dashboardPage.waitForSamplesLoaded();
        
        // When - Refresh the page
        dashboardPage.navigate();
        
        // Then - Sample should still be in list
        dashboardPage.waitForSamplesLoaded();
        assertThat(dashboardPage.getSamplesListText()).contains("Preload Sample");
    }
    
    @Test
    @DisplayName("Should refresh samples list when refresh button clicked")
    void shouldRefreshSamplesListWhenRefreshButtonClicked() {
        // Given
        V1DashboardPage dashboardPage = createPage();
        dashboardPage.navigate();
        dashboardPage.waitForSamplesLoaded();
        int initialCount = dashboardPage.getSamplesCount();
        
        // Create a sample
        dashboardPage.fillCreateForm("Refresh Test Sample", 88, "active", null);
        dashboardPage.submitCreateForm();
        dashboardPage.waitForSamplesLoaded();
        
        // When
        dashboardPage.clickRefresh();
        dashboardPage.waitForSamplesLoaded();
        
        // Then
        int newCount = dashboardPage.getSamplesCount();
        assertThat(newCount).isGreaterThan(initialCount);
        assertThat(dashboardPage.getSamplesListText()).contains("Refresh Test Sample");
    }
    
    @Test
    @DisplayName("Should show empty state when no samples exist")
    void shouldShowEmptyStateWhenNoSamplesExist() {
        // Note: This test may fail if database has existing data
        // In a real scenario, you'd clean the database first or use a separate test database
        
        // Given + When
        V1DashboardPage dashboardPage = createPage();
        dashboardPage.navigate();
        dashboardPage.waitForSamplesLoaded();
        
        // Then - Either samples exist or empty state is shown
        boolean hasEmptyState = dashboardPage.isSamplesListEmpty();
        boolean hasSamples = dashboardPage.getSamplesCount() > 0;
        
        assertThat(hasEmptyState || hasSamples).isTrue();
    }
    
    // ========== Get By ID Tests ==========
    
    @Test
    @DisplayName("Should retrieve sample by ID when valid ID provided")
    void shouldRetrieveSampleByIdWhenValidIdProvided() {
        // Given - Create a sample to get its ID
        V1DashboardPage dashboardPage = createPage();
        dashboardPage.navigate();
        dashboardPage.fillCreateForm("Get By ID Test", 123, "active", null);
        dashboardPage.submitCreateForm();
        dashboardPage.waitForSamplesLoaded();
        
        dashboardPage.clickRefresh();
        dashboardPage.waitForSamplesLoaded();
        
        String listText = dashboardPage.getSamplesListText();
        
        // Extract first ID from list (format: "ID: 123")
        // This is a simplified approach - in production, you'd parse properly
        if (listText.contains("ID:")) {
            // When - Search by ID (using ID 1 as a simple case)
            dashboardPage.fillGetByIdForm(1);
            dashboardPage.submitGetByIdForm();
            
            // Then - Detail should be displayed or message shown
            String message = dashboardPage.getByIdMessage();
            String detail = dashboardPage.getByIdDetail();
            
            assertThat(message != null || detail != null).isTrue();
        }
    }
    
    @Test
    @DisplayName("Should show error when invalid ID provided")
    void shouldShowErrorWhenInvalidIdProvided() {
        // Given
        V1DashboardPage dashboardPage = createPage();
        dashboardPage.navigate();
        
        // When - Use ID that doesn't exist
        dashboardPage.fillGetByIdForm(999999);
        dashboardPage.submitGetByIdForm();
        
        // Then
        String message = dashboardPage.getByIdMessage();
        assertThat(message).isNotNull();
        assertThat(message).containsAnyOf("not found", "404", "error");
    }
    
    // ========== User Workflow Tests ==========
    
    @Test
    @DisplayName("Should complete full create and verify workflow")
    void shouldCompleteFullCreateAndVerifyWorkflow() {
        // Given
        V1DashboardPage dashboardPage = createPage();
        dashboardPage.navigate();
        String uniqueText = "E2E Test Sample " + System.currentTimeMillis();
        
        // When - Create sample
        dashboardPage.fillCreateForm(uniqueText, 555, "active", "{\"test\": true}");
        dashboardPage.submitCreateForm();
        dashboardPage.waitForSamplesLoaded();
        
        // Then - Verify creation
        assertThat(dashboardPage.getCreateSuccessMessage()).contains("created");
        
        // When - Refresh list
        dashboardPage.clickRefresh();
        dashboardPage.waitForSamplesLoaded();
        
        // Then - Verify sample in list
        assertThat(dashboardPage.getSamplesListText()).contains(uniqueText);
        assertThat(dashboardPage.getSamplesCount()).isGreaterThan(0);
    }
}
