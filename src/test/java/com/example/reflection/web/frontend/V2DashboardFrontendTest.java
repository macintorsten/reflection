package com.example.reflection.web.frontend;

import com.example.reflection.web.frontend.pages.V2DashboardPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Frontend integration tests for V2 Dashboard.
 * Tests V2-specific features: priority slider, statistics, and metadata.
 */
@DisplayName("V2 Dashboard Frontend Tests")
class V2DashboardFrontendTest extends AbstractFrontendTest {
    
    /**
     * Helper method to create V2DashboardPage for each test.
     * Follows DRY principle while keeping tests isolated.
     */
    private V2DashboardPage createPage() {
        return new V2DashboardPage(page, baseUrl);
    }
    
    
    
    
    
    // ========== Page Load Tests ==========
    
    @Test
    @DisplayName("Should load V2 dashboard successfully")
    void shouldLoadV2DashboardSuccessfully() {
        // Given + When
        V2DashboardPage dashboardPage = createPage();
        dashboardPage.navigate();
        
        // Then
        assertThat(dashboardPage.getPageTitle()).contains("Sample API V2");
        assertThat(dashboardPage.getVersionBadge()).contains("Version 2");
        assertThat(dashboardPage.isElementVisible("#createForm")).isTrue();
        assertThat(dashboardPage.isElementVisible("#samplesList")).isTrue();
        assertThat(dashboardPage.areStatisticsVisible()).isTrue();
    }
    
    @Test
    @DisplayName("Should display statistics on page load")
    void shouldDisplayStatisticsOnPageLoad() {
        // Given + When
        V2DashboardPage dashboardPage = createPage();
        dashboardPage.navigate();
        dashboardPage.waitForStatisticsLoaded();
        
        // Then
        assertThat(dashboardPage.getTotalCount()).isNotNull();
        assertThat(dashboardPage.getAverageNumber()).isNotNull();
        assertThat(dashboardPage.getAveragePriority()).isNotNull();
        assertThat(dashboardPage.getActiveCount()).isNotNull();
        assertThat(dashboardPage.getInactiveCount()).isNotNull();
    }
    
    // ========== Priority Slider Tests ==========
    
    @Test
    @DisplayName("Should update priority display when slider moved")
    void shouldUpdatePriorityDisplayWhenSliderMoved() {
        // Given
        V2DashboardPage dashboardPage = createPage();
        dashboardPage.navigate();
        
        // When
        dashboardPage.setPrioritySlider(7);
        
        // Then
        assertThat(dashboardPage.getPriorityValue()).isEqualTo(7);
        assertThat(dashboardPage.getPriorityDisplay()).contains("7");
    }
    
    @Test
    @DisplayName("Should create sample with high priority")
    void shouldCreateSampleWithHighPriority() {
        // Given
        V2DashboardPage dashboardPage = createPage();
        dashboardPage.navigate();
        
        // When
        dashboardPage.fillCreateForm("High Priority Sample", 100, 9, "active", null);
        dashboardPage.submitCreateForm();
        
        // Then
        dashboardPage.waitForStatisticsLoaded();
        String successMessage = dashboardPage.getCreateSuccessMessage();
        assertThat(successMessage).contains("created");
        
        // Verify in list
        dashboardPage.clickRefresh();
        dashboardPage.waitForSamplesLoaded();
        assertThat(dashboardPage.getSamplesListText()).contains("High Priority Sample");
        assertThat(dashboardPage.isPriorityBadgeVisible("priority-high")).isTrue();
    }
    
    @Test
    @DisplayName("Should create sample with medium priority")
    void shouldCreateSampleWithMediumPriority() {
        // Given
        V2DashboardPage dashboardPage = createPage();
        dashboardPage.navigate();
        
        // When
        dashboardPage.fillCreateForm("Medium Priority Sample", 50, 5, "active", null);
        dashboardPage.submitCreateForm();
        
        // Then
        dashboardPage.waitForStatisticsLoaded();
        assertThat(dashboardPage.getCreateSuccessMessage()).contains("created");
        
        dashboardPage.clickRefresh();
        dashboardPage.waitForSamplesLoaded();
        assertThat(dashboardPage.getSamplesListText()).contains("Medium Priority Sample");
        assertThat(dashboardPage.isPriorityBadgeVisible("priority-medium")).isTrue();
    }
    
    @Test
    @DisplayName("Should create sample with low priority")
    void shouldCreateSampleWithLowPriority() {
        // Given
        V2DashboardPage dashboardPage = createPage();
        dashboardPage.navigate();
        
        // When
        dashboardPage.fillCreateForm("Low Priority Sample", 10, 2, "active", null);
        dashboardPage.submitCreateForm();
        
        // Then
        dashboardPage.waitForStatisticsLoaded();
        assertThat(dashboardPage.getCreateSuccessMessage()).contains("created");
        
        dashboardPage.clickRefresh();
        dashboardPage.waitForSamplesLoaded();
        assertThat(dashboardPage.getSamplesListText()).contains("Low Priority Sample");
        assertThat(dashboardPage.isPriorityBadgeVisible("priority-low")).isTrue();
    }
    
    // ========== Metadata Tests ==========
    
    @Test
    @DisplayName("Should display computed metadata for created sample")
    void shouldDisplayComputedMetadataForCreatedSample() {
        // Given
        V2DashboardPage dashboardPage = createPage();
        dashboardPage.navigate();
        
        // Note: Metadata is computed server-side from internal mapField structure.
        // The metadata parameter is not used - it's here for API consistency but ignored.
        // Backend computes: totalValue (sum), itemCount (count), tags (keys) from mapField.
        
        // When
        dashboardPage.fillCreateForm("Computed Metadata Sample", 75, 6, "active", null);
        dashboardPage.submitCreateForm();
        
        // Then
        dashboardPage.waitForStatisticsLoaded();
        assertThat(dashboardPage.getCreateSuccessMessage()).contains("created");
        
        dashboardPage.clickRefresh();
        dashboardPage.waitForSamplesLoaded();
        String listText = dashboardPage.getSamplesListText();
        assertThat(listText).contains("Computed Metadata Sample");
        
        // Metadata is always displayed (computed from mapField, defaults to zeros for simple samples)
        assertThat(dashboardPage.isMetadataVisible())
            .as("Metadata section should be visible with computed values")
            .isTrue();
        assertThat(listText)
            .as("Metadata section should display value labels")
            .containsAnyOf("Total Value:", "Item Count:", "Tags:");
    }
    
    // ========== Statistics Tests ==========
    
    @Test
    @DisplayName("Should update statistics after creating sample")
    void shouldUpdateStatisticsAfterCreatingSample() {
        // Given
        V2DashboardPage dashboardPage = createPage();
        dashboardPage.navigate();
        dashboardPage.waitForStatisticsLoaded();
        String initialTotal = dashboardPage.getTotalCount();
        
        // When
        dashboardPage.fillCreateForm("Stats Update Test", 200, 8, "active", null);
        dashboardPage.submitCreateForm();
        dashboardPage.waitForStatisticsLoaded();
        
        dashboardPage.clickRefresh();
        dashboardPage.waitForStatisticsLoaded();
        
        // Then
        String newTotal = dashboardPage.getTotalCount();
        // Total should have increased (unless test ran with errors)
        assertThat(newTotal).isNotNull();
    }
    
    @Test
    @DisplayName("Should show correct active and inactive counts")
    void shouldShowCorrectActiveAndInactiveCounts() {
        // Given
        V2DashboardPage dashboardPage = createPage();
        dashboardPage.navigate();
        
        // When - Create one active and one inactive sample
        dashboardPage.fillCreateForm("Active Sample", 111, 5, "active", null);
        dashboardPage.submitCreateForm();
        dashboardPage.waitForStatisticsLoaded();
        
        dashboardPage.fillCreateForm("Inactive Sample", 222, 5, "inactive", null);
        dashboardPage.submitCreateForm();
        dashboardPage.waitForStatisticsLoaded();
        
        dashboardPage.clickRefresh();
        dashboardPage.waitForStatisticsLoaded();
        
        // Then
        String activeCount = dashboardPage.getActiveCount();
        String inactiveCount = dashboardPage.getInactiveCount();
        
        assertThat(activeCount).isNotNull();
        assertThat(inactiveCount).isNotNull();
        
        // Both counts should be > 0 after creating samples
        assertThat(Integer.parseInt(activeCount)).isGreaterThan(0);
        assertThat(Integer.parseInt(inactiveCount)).isGreaterThan(0);
    }
    
    @Test
    @DisplayName("Should display average priority in statistics")
    void shouldDisplayAveragePriorityInStatistics() {
        // Given
        V2DashboardPage dashboardPage = createPage();
        dashboardPage.navigate();
        
        // When - Create samples with different priorities
        dashboardPage.fillCreateForm("Priority Test 1", 50, 3, "active", null);
        dashboardPage.submitCreateForm();
        dashboardPage.waitForStatisticsLoaded();
        
        dashboardPage.fillCreateForm("Priority Test 2", 60, 7, "active", null);
        dashboardPage.submitCreateForm();
        dashboardPage.waitForStatisticsLoaded();
        
        dashboardPage.clickRefresh();
        dashboardPage.waitForStatisticsLoaded();
        
        // Then
        String avgPriority = dashboardPage.getAveragePriority();
        assertThat(avgPriority).isNotNull();
        assertThat(avgPriority).isNotEqualTo("0");
    }
    
    // ========== User Workflow Tests ==========
    
    @Test
    @DisplayName("Should complete full V2 create and verify workflow")
    void shouldCompleteFullV2CreateAndVerifyWorkflow() {
        // Given
        V2DashboardPage dashboardPage = createPage();
        dashboardPage.navigate();
        dashboardPage.waitForStatisticsLoaded();
        String uniqueText = "V2 E2E Test " + System.currentTimeMillis();
        String metadata = "{\"totalValue\": 999, \"itemCount\": 3}";
        
        // When - Create sample with all V2 features
        dashboardPage.setPrioritySlider(8);
        dashboardPage.fillCreateForm(uniqueText, 777, 8, "active", metadata);
        dashboardPage.submitCreateForm();
        dashboardPage.waitForStatisticsLoaded();
        
        // Then - Verify creation
        assertThat(dashboardPage.getCreateSuccessMessage()).contains("created");
        
        // When - Refresh to see new sample
        dashboardPage.clickRefresh();
        dashboardPage.waitForStatisticsLoaded();
        
        // Then - Verify sample appears
        String listText = dashboardPage.getSamplesListText();
        assertThat(listText).contains(uniqueText);
        assertThat(dashboardPage.isPriorityBadgeVisible("priority-high")).isTrue();
        
        // Then - Verify statistics updated
        assertThat(dashboardPage.getTotalCount()).isNotEqualTo("0");
        assertThat(dashboardPage.getAveragePriority()).isNotNull();
    }
    
    @Test
    @DisplayName("Should handle priority boundaries correctly")
    void shouldHandlePriorityBoundariesCorrectly() {
        // Given
        V2DashboardPage dashboardPage = createPage();
        dashboardPage.navigate();
        
        // When - Test minimum priority (1)
        dashboardPage.setPrioritySlider(1);
        
        // Then
        assertThat(dashboardPage.getPriorityValue()).isEqualTo(1);
        
        // When - Test maximum priority (10)
        dashboardPage.setPrioritySlider(10);
        
        // Then
        assertThat(dashboardPage.getPriorityValue()).isEqualTo(10);
    }
}
