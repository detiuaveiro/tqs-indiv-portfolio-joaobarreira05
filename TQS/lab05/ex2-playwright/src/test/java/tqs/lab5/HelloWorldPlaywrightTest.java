package tqs.lab5;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Lab 5.5a - Playwright "Hello World" (basic approach)
 * Manual browser/context/page management
 */
class HelloWorldPlaywrightTest {

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        // headless: false to see the browser; slowMo to slow down execution
        browser = playwright.chromium().launch(
            new BrowserType.LaunchOptions()
                .setHeadless(true)  // Set to false to see the browser
                .setSlowMo(0)       // Increase to slow down (e.g., 100ms)
        );
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void tearDown() {
        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @Test
    @DisplayName("5.5a - Hello World Playwright test")
    void testBoniGarciaHomepage() {
        // Navigate to the website
        page.navigate("https://bonigarcia.dev/selenium-webdriver-java/");

        // Get and assert the page title
        String title = page.title();
        System.out.println("Page title: " + title);
        
        assertThat(title).contains("Selenium WebDriver");
    }

    @Test
    @DisplayName("5.5b - Navigate to Slow Calculator and assert URL")
    void testNavigateToSlowCalculator() {
        // Navigate to the main page
        page.navigate("https://bonigarcia.dev/selenium-webdriver-java/");

        // Click the "Slow calculator" link
        page.click("text=Slow calculator");

        // Assert we arrived at the correct page
        String currentUrl = page.url();
        assertThat(currentUrl).contains("slow-calculator");
    }

    @Test
    @DisplayName("5.5a - Custom scenario: BlazeDemo homepage")
    void testBlazeDemoHomepage() {
        page.navigate("https://blazedemo.com/");

        String title = page.title();
        assertThat(title).isEqualTo("BlazeDemo");

        // Verify key elements are present
        assertThat(page.locator("select[name='fromPort']").isVisible()).isTrue();
        assertThat(page.locator("select[name='toPort']").isVisible()).isTrue();
    }
}
