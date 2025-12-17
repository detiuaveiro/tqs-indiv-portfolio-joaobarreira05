package tqs.lab5;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Lab 5.1c - WebDriver with Selenium-Jupiter Extension (Example 2-2 style)
 * Uses parameter-level dependency injection for WebDriver
 * 
 * Benefits:
 * - No explicit driver setup/teardown
 * - WebDriver binaries resolved automatically
 * - Cleaner test code
 */
@ExtendWith(SeleniumJupiter.class)
class HelloWorldSeleniumJupiterTest {

    @Test
    @DisplayName("5.1c - Hello World with Selenium-Jupiter extension")
    void testBoniGarciaHomepage(ChromeDriver driver) {
        // Navigate to the website
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/");

        // Get and assert the page title
        String title = driver.getTitle();
        assertThat(title).contains("Selenium WebDriver");
    }

    @Test
    @DisplayName("5.1c - Navigate to Slow Calculator with Selenium-Jupiter")
    void testNavigateToSlowCalculator(ChromeDriver driver) {
        // Navigate to the main page
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/");

        // Find and click the "Slow calculator" link
        WebElement slowCalculatorLink = driver.findElement(By.linkText("Slow calculator"));
        slowCalculatorLink.click();

        // Assert we arrived at the correct page
        String currentUrl = driver.getCurrentUrl();
        assertThat(currentUrl).contains("slow-calculator");
    }

    @Test
    @DisplayName("5.1c - Alternative: Using generic WebDriver interface")
    void testWithWebDriverInterface(WebDriver driver) {
        // This also works - Selenium-Jupiter injects Chrome by default
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/");
        
        assertThat(driver.getTitle()).isNotBlank();
    }
}
