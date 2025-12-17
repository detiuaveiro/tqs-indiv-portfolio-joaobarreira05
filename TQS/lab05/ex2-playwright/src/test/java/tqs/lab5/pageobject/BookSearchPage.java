package tqs.lab5.pageobject;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Lab 5.8 - Page Object for Book Search with Playwright
 * Demonstrates Page Object Pattern with Playwright API
 */
public class BookSearchPage {

    private final Page page;
    private static final String URL = "https://cover-bookstore.onrender.com/";

    // Locators (defined as strings for flexibility)
    private static final String SEARCH_INPUT = "input[type='search'], input[placeholder*='Search'], input[name='search']";
    private static final String BOOK_RESULTS = "[data-testid='book-search-item'], .book-card, article, .book-item";

    public BookSearchPage(Page page) {
        this.page = page;
    }

    public BookSearchPage navigate() {
        page.navigate(URL);
        page.waitForLoadState();
        return this;
    }

    public BookSearchPage searchFor(String query) {
        Locator searchInput = page.locator(SEARCH_INPUT).first();
        searchInput.fill(query);
        searchInput.press("Enter");
        // Wait for results
        page.waitForTimeout(2000);
        return this;
    }

    public Locator getBookResults() {
        return page.locator(BOOK_RESULTS);
    }

    public Locator getBooksWithText(String text) {
        return getBookResults().filter(
            new Locator.FilterOptions().setHasText(text)
        );
    }

    public boolean hasResultsContaining(String text) {
        return getBooksWithText(text).count() > 0;
    }

    public String getPageContent() {
        return page.content();
    }

    public String getTitle() {
        return page.title();
    }
}
