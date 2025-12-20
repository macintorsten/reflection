package com.example.reflection.web.frontend;

import com.example.reflection.web.frontend.pages.{{PAGE_NAME}}Page;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Frontend integration test template.
 * Tests user workflows with real browser automation using Playwright.
 * 
 * Replace {{PAGE_NAME}} with your page name (e.g., V1Dashboard, ProductCatalog)
 * 
 * Test Pattern: Given-When-Then
 * - Given: Setup test data and navigate to page
 * - When: Perform user action (click, fill form, etc.)
 * - Then: Verify expected outcome (assertions)
 */
@DisplayName("{{PAGE_NAME}} Frontend Tests")
class {{PAGE_NAME}}FrontendTest extends AbstractFrontendTest {
    
    private {{PAGE_NAME}}Page page;
    
    @BeforeEach
    void setupPage() {
        page = new {{PAGE_NAME}}Page(browser, baseUrl);
    }
    
    @AfterEach
    void teardownPage() {
        page.close();
    }
    
    // ========== Basic Tests ==========
    
    @Test
    @DisplayName("Should load page successfully")
    void shouldLoadPageSuccessfully() {
        // Given + When
        page.navigate();
        
        // Then
        assertThat(page.getTitle()).isNotEmpty();
    }
    
    // ========== Form Tests ==========
    
    @Test
    @DisplayName("Should submit form when valid data provided")
    void shouldSubmitFormWhenValidDataProvided() {
        // Given
        page.navigate();
        
        // When
        page.fillForm("Test Item", 42);
        page.submitForm();
        
        // Then
        assertThat(page.getSuccessMessage()).contains("Success");
    }
    
    @Test
    @DisplayName("Should show error when invalid data provided")
    void shouldShowErrorWhenInvalidDataProvided() {
        // Given
        page.navigate();
        
        // When
        page.fillForm("", -1); // Invalid: empty text, negative number
        page.submitForm();
        
        // Then
        assertThat(page.getErrorMessage()).isNotNull();
    }
    
    // ========== Dynamic Content Tests ==========
    
    @Test
    @DisplayName("Should load data when refresh clicked")
    void shouldLoadDataWhenRefreshClicked() {
        // Given
        page.navigate();
        
        // When
        page.clickRefresh();
        
        // Then
        page.waitForElementVisible("#dataList");
        assertThat(page.getListItemCount("#dataList")).isGreaterThanOrEqualTo(0);
    }
    
    // ========== API Integration Tests ==========
    
    @Test
    @DisplayName("Should display items fetched from API")
    void shouldDisplayItemsFetchedFromAPI() {
        // Given
        // Create test data via API (if needed)
        // var item = createItemViaAPI("Test", 10);
        
        // When
        page.navigate();
        
        // Then
        // Verify item appears in UI
        // assertThat(page.getItemById(item.id())).isNotNull();
    }
    
    // ========== Edge Cases ==========
    
    @Test
    @DisplayName("Should handle empty list gracefully")
    void shouldHandleEmptyListGracefully() {
        // Given
        // Ensure database is empty (or use clean test data)
        
        // When
        page.navigate();
        
        // Then
        assertThat(page.isVisible(".empty-state")).isTrue();
    }
    
    @Test
    @DisplayName("Should validate required fields")
    void shouldValidateRequiredFields() {
        // Given
        page.navigate();
        
        // When
        page.submitForm(); // Submit without filling form
        
        // Then
        // HTML5 validation prevents submission
        // Or check for custom validation message
        assertThat(page.getErrorMessage()).isNotNull();
    }
    
    // ========== User Workflows ==========
    
    @Test
    @DisplayName("Should complete full create-read workflow")
    void shouldCompleteFullCreateReadWorkflow() {
        // Given
        page.navigate();
        String testText = "Integration Test Item";
        
        // When - Create
        page.fillForm(testText, 100);
        page.submitForm();
        
        // Then - Read
        assertThat(page.getSuccessMessage()).contains("created");
        page.clickRefresh();
        assertThat(page.getText("#dataList")).contains(testText);
    }
}
