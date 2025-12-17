package tqs.lab5.pageobject;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Lab 5.8 [Optional] - Page Object Pattern with Playwright
 * Uses Page Objects for cleaner, more maintainable tests
 */
@UsePlaywright
class BookSearchPageObjectTest {

    @Test
    @DisplayName("5.8 - Search Harry Potter using Page Object")
    void testSearchHarryPotterWithPageObject(Page page) {
        BookSearchPage bookSearchPage = new BookSearchPage(page);

        bookSearchPage
            .navigate()
            .searchFor("Harry Potter");

        // Check for results
        String content = bookSearchPage.getPageContent().toLowerCase();
        assertThat(content).contains("harry potter");
    }

    @Test
    @DisplayName("5.8 - Fluent API for book search")
    void testFluentSearchAPI(Page page) {
        BookSearchPage searchPage = new BookSearchPage(page)
            .navigate()
            .searchFor("Rowling");

        // Verify results
        assertThat(searchPage.getTitle()).isNotBlank();
    }
}
