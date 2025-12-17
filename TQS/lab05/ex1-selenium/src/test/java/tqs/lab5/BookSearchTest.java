package tqs.lab5;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Lab 5.3 - Selecting Elements with Locators
 * Tests searching for books in the online bookstore
 * 
 * Demonstrates:
 * - Different locator strategies (id, css, xpath, data-testid)
 * - Explicit waits for robustness
 * - Locator refactoring best practices
 */
@ExtendWith(SeleniumJupiter.class)
class BookSearchTest {

    private static final String BOOKSTORE_URL = "https://cover-bookstore.onrender.com/";

    @Test
    @DisplayName("5.3a - Search for Harry Potter (basic)")
    void testSearchHarryPotterBasic(ChromeDriver driver) {
        driver.get(BOOKSTORE_URL);

        // Wait for the page to load
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // Find the search input (using various possible locators)
        WebElement searchInput = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[type='search'], input[name='search'], input[placeholder*='Search']"))
        );
        
        // Type the search query
        searchInput.sendKeys("Harry Potter");
        searchInput.submit();

        // Wait for results
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='book-search-item'], .book-item, .search-result")));

        // Find Harry Potter and the Sorcerer's Stone
        List<WebElement> results = driver.findElements(By.cssSelector("[data-testid='book-search-item'], .book-item, .search-result"));
        
        boolean foundSorcerersStone = results.stream()
            .anyMatch(el -> el.getText().contains("Sorcerer's Stone") || el.getText().contains("Philosopher's Stone"));
        
        assertThat(foundSorcerersStone).isTrue();
    }

    @Test
    @DisplayName("5.3b/c - Search with robust locators and explicit waits")
    void testSearchWithRobustLocators(ChromeDriver driver) {
        driver.get(BOOKSTORE_URL);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // 5.3c: Use well-defined selectors (data-testid preferred)
        // Fallback chain: data-testid > id > name > css class
        WebElement searchInput = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.cssSelector("[data-testid='book-search-input'], #search-input, input[name='search'], input[type='search']")
            )
        );

        searchInput.clear();
        searchInput.sendKeys("Harry Potter");
        
        // Find and click search button or submit
        try {
            WebElement searchButton = driver.findElement(
                By.cssSelector("[data-testid='search-button'], button[type='submit'], input[type='submit']")
            );
            searchButton.click();
        } catch (Exception e) {
            // If no button, try submitting the form
            searchInput.submit();
        }

        // 5.3c: Explicit wait for search results (handles latency)
        List<WebElement> bookItems = wait.until(
            ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.cssSelector("[data-testid='book-search-item'], .book-card, .book-item")
            )
        );

        // Verify at least one result contains "Harry Potter"
        assertThat(bookItems).isNotEmpty();
        
        // Find the specific book
        boolean foundBook = bookItems.stream()
            .anyMatch(item -> {
                String text = item.getText().toLowerCase();
                return text.contains("harry potter") && 
                       (text.contains("sorcerer") || text.contains("philosopher")) &&
                       text.contains("rowling");
            });

        assertThat(foundBook)
            .withFailMessage("Expected to find 'Harry Potter and the Sorcerer's Stone' by J.K. Rowling")
            .isTrue();
    }

    /**
     * 5.3b - Analysis of Locator Strategies:
     * 
     * XPath locators: Can be fragile if page structure changes
     *   - e.g., //div[@class='book-list']/div[1]/h3 - breaks if div order changes
     * 
     * ID-based locators: Most robust but not always available
     *   - e.g., By.id("book-title") - stable if IDs don't change
     * 
     * Data-testid locators: Best practice for test automation
     *   - e.g., By.cssSelector("[data-testid='book-search-item']")
     *   - Purpose-built for testing, unlikely to change during refactoring
     * 
     * Robustness ranking (most to least):
     * 1. data-testid attributes
     * 2. ID attributes  
     * 3. Name attributes
     * 4. CSS selectors (semantic, not positional)
     * 5. Link text / partial link text
     * 6. XPath (positional) - LEAST robust
     */
}
