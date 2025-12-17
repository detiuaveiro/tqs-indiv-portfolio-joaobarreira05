package tqs.lab5;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Lab 5.5c - Playwright with @UsePlaywright Extension
 * Uses built-in JUnit extension with dependency injection
 * 
 * Benefits:
 * - No explicit browser/context setup
 * - Automatic cleanup
 * - Cleaner test code
 */
@UsePlaywright
class HelloWorldPlaywrightExtensionTest {

    @Test
    @DisplayName("5.5c - Hello World with @UsePlaywright extension")
    void testBoniGarciaHomepage(Page page) {
        page.navigate("https://bonigarcia.dev/selenium-webdriver-java/");

        String title = page.title();
        assertThat(title).contains("Selenium WebDriver");
    }

    @Test
    @DisplayName("5.5c - Navigate to Slow Calculator")
    void testNavigateToSlowCalculator(Page page) {
        page.navigate("https://bonigarcia.dev/selenium-webdriver-java/");

        // Click using text locator
        page.click("text=Slow calculator");

        // Assert URL
        assertThat(page.url()).contains("slow-calculator");
    }

    @Test
    @DisplayName("5.5c - Alternative locator styles")
    void testAlternativeLocators(Page page) {
        page.navigate("https://bonigarcia.dev/selenium-webdriver-java/");

        // Different ways to locate elements in Playwright
        // 1. By text
        assertThat(page.locator("text=Slow calculator").isVisible()).isTrue();
        
        // 2. By CSS selector
        assertThat(page.locator("a[href='slow-calculator.html']").isVisible()).isTrue();
        
        // 3. By role
        assertThat(page.getByRole(com.microsoft.playwright.options.AriaRole.LINK, 
            new Page.GetByRoleOptions().setName("Slow calculator")).isVisible()).isTrue();
    }
}
