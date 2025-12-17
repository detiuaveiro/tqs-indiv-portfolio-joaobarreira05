package tqs.lab5;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Lab 5.1a - WebDriver "Hello World" (Example 2-1 style)
 * Basic Selenium test without any extensions
 */
class HelloWorldSeleniumTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        // WebDriverManager handles driver binary automatically via Selenium 4.6+
        driver = new ChromeDriver();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testBoniGarciaHomepage() {
        // Navigate to the website
        String url = "https://bonigarcia.dev/selenium-webdriver-java/";
        driver.get(url);

        // Get the page title
        String title = driver.getTitle();
        System.out.println("Page title: " + title);

        // Assert the title contains expected text
        assertThat(title).contains("Selenium WebDriver");
    }

    @Test
    @DisplayName("5.1b - Navigate to Slow Calculator and assert URL")
    void testNavigateToSlowCalculator() {
        // Navigate to the main page
        driver.get("https://bonigarcia.dev/selenium-webdriver-java/");

        // Find and click the "Slow calculator" link
        WebElement slowCalculatorLink = driver.findElement(By.linkText("Slow calculator"));
        slowCalculatorLink.click();

        // Assert we arrived at the correct page
        String currentUrl = driver.getCurrentUrl();
        assertThat(currentUrl).contains("slow-calculator");
        
        // Also verify title or heading
        String pageTitle = driver.getTitle();
        assertThat(pageTitle).contains("Selenium WebDriver");
    }
}
