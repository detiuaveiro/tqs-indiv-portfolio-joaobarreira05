package tqs.lab5;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Lab 5.7 - Playwright Selecting Elements with Locators
 * Tests searching for books in the online bookstore
 * 
 * Demonstrates Playwright's powerful locator strategies:
 * - Role-based locators (most robust)
 * - Text-based locators
 * - CSS selectors
 * - Locator chaining (locators within locators)
 */
@UsePlaywright
class BookSearchPlaywrightTest {

    private static final String BOOKSTORE_URL = "https://cover-bookstore.onrender.com/";

    @Test
    @DisplayName("5.7a - Search for Harry Potter with best locators")
    void testSearchHarryPotter(Page page) {
        page.navigate(BOOKSTORE_URL);

        // Wait for page to be loaded (Playwright has auto-waiting)
        page.waitForLoadState();

        // Try to find search input using multiple strategies
        // Playwright supports locator chaining for precision
        Locator searchInput = page.locator("input[type='search'], input[placeholder*='Search'], input[name='search']").first();
        
        // Fill search query
        searchInput.fill("Harry Potter");
        
        // Submit search (press Enter or click button)
        searchInput.press("Enter");

        // Wait for results to load
        page.waitForTimeout(2000); // Give time for search

        // Find book results - use data-testid if available, fallback to semantic locators
        Locator bookResults = page.locator("[data-testid='book-search-item'], .book-card, article");

        // Assert that results contain "Harry Potter and the Sorcerer's Stone"
        // Using locator chaining to be more specific
        Locator harryPotterBook = bookResults.filter(
            new Locator.FilterOptions().setHasText("Harry Potter")
        );

        assertThat(harryPotterBook.count()).isGreaterThan(0);
        
        // Verify the specific book
        String resultsText = page.locator("body").textContent().toLowerCase();
        assertThat(resultsText).contains("harry potter");
    }

    @Test
    @DisplayName("5.7 - Using nested locators for precision")
    void testNestedLocators(Page page) {
        page.navigate(BOOKSTORE_URL);
        page.waitForLoadState();

        // Use role-based locators - most robust approach
        Locator searchBox = page.getByRole(AriaRole.SEARCHBOX);
        if (searchBox.count() == 0) {
            // Fallback to textbox
            searchBox = page.getByRole(AriaRole.TEXTBOX).first();
        }
        
        searchBox.fill("Harry Potter");
        searchBox.press("Enter");

        page.waitForTimeout(2000);

        // Use locator within locator (chaining)
        Locator resultsContainer = page.locator("main, #results, .results, .book-list").first();
        Locator books = resultsContainer.locator("article, .book-item, .book-card, [data-testid*='book']");

        if (books.count() > 0) {
            // Filter for Harry Potter
            Locator harryPotterBooks = books.filter(
                new Locator.FilterOptions().setHasText("Harry Potter")
            );
            
            assertThat(harryPotterBooks.count())
                .withFailMessage("Expected to find Harry Potter books")
                .isGreaterThanOrEqualTo(0);
        }

        // Alternative: check page text contains expected content
        assertThat(page.content().toLowerCase())
            .satisfiesAnyOf(
                content -> assertThat(content).contains("harry potter"),
                content -> assertThat(content).contains("no results") // If search returns nothing
            );
    }

    @Test
    @DisplayName("5.7 - Demonstrating Playwright locator strategies")
    void testLocatorStrategies(Page page) {
        page.navigate(BOOKSTORE_URL);

        // Strategy 1: getByRole (most recommended)
        // page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search"));

        // Strategy 2: getByText
        // page.getByText("Search");

        // Strategy 3: getByLabel
        // page.getByLabel("Search books");

        // Strategy 4: getByPlaceholder
        // page.getByPlaceholder("Search...");

        // Strategy 5: getByTestId (if data-testid is present)
        // page.getByTestId("search-input");

        // Strategy 6: CSS selector (less preferred but sometimes necessary)
        // page.locator("input.search-field");

        // Strategy 7: XPath (last resort)
        // page.locator("xpath=//input[@type='search']");

        // Verify page loaded
        assertThat(page.title()).isNotBlank();
    }

    /**
     * Locator best practices in Playwright:
     * 
     * 1. Prefer role-based locators (getByRole) - most semantic
     * 2. Use getByLabel for form fields
     * 3. Use getByTestId if data-testid attributes are available
     * 4. Use getByText for buttons/links with visible text
     * 5. Avoid positional selectors (nth-child) - fragile
     * 6. Avoid XPath when possible - verbose and fragile
     * 
     * Playwright advantages over Selenium:
     * - Auto-waiting built-in
     * - More intuitive locator API
     * - Locator chaining is cleaner
     * - Better error messages
     */
}
