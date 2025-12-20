package com.example.reflection;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Base class for integration tests that need a PostgreSQL database.
 * 
 * The database container is started ONCE and shared across ALL test classes
 * that extend this base class. This dramatically reduces test execution time.
 * 
 * WHY STATIC IS CRITICAL:
 * 
 * With STATIC (current - correct):
 * ┌──────────────────────────────────────┐
 * │ AbstractIntegrationTest (class)      │
 * │  - static postgres ─────────┐        │
 * └──────────────────────────────────────┘
 *                                │
 *         ┌──────────────────────┘
 *         ▼
 *    [ONE PostgreSQL Container]    ← All test classes share THIS
 *         ▲           ▲
 *         │           │
 *    TestClass1   TestClass2
 * 
 * Without static (wrong):
 * ┌──────────────────────────────────────┐
 * │ TestClass1 instance                  │
 * │  - postgres ──►[Container A]         │
 * └──────────────────────────────────────┘
 * 
 * ┌──────────────────────────────────────┐
 * │ TestClass2 instance                  │
 * │  - postgres ──►[Container B]         │  ← DIFFERENT container!
 * └──────────────────────────────────────┘
 * 
 * Startup overhead:
 * - Without sharing: 3 seconds × N test classes
 * - With sharing: 3 seconds total (one-time cost)
 * 
 * Usage:
 * <pre>
 * class UserControllerIntegrationTest extends AbstractIntegrationTest {
 *     // Tests automatically use shared database
 * }
 * </pre>
 */
@SpringBootTest
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

    /**
     * Singleton container shared across all test classes.
     * Started once when first test class loads, reused by all others.
     */
    private static final PostgreSQLContainer<?> postgres;

    static {
        postgres = new PostgreSQLContainer<>("postgres:15")
                .withDatabaseName("testdb")
                .withUsername("test")
                .withPassword("test");
        
        // Start container once for all test classes
        postgres.start();
        
        // JVM shutdown hook ensures container stops after all tests
        Runtime.getRuntime().addShutdownHook(new Thread(postgres::stop));
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // Ensure Hibernate creates schema in the ephemeral Testcontainers database.
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }
}
