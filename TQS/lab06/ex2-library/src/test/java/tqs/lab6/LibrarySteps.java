package tqs.lab6;

import io.cucumber.java.DataTableType;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step definitions for the Library feature
 * Demonstrates:
 * - DataTable handling (6.2c)
 * - Custom ParameterType for dates (6.2b)
 * - Cucumber expressions
 */
public class LibrarySteps {
    
    private Library library;
    private List<Book> searchResults;
    
    /**
     * 6.2b - Custom ParameterType for ISO-8601 dates
     * Matches dates in format yyyy-MM-dd
     */
    @ParameterType("\\d{4}-\\d{2}-\\d{2}")
    public LocalDate iso8601Date(String dateString) {
        return LocalDate.parse(dateString);
    }
    
    /**
     * 6.2c - DataTable mapping to create Book objects
     */
    @DataTableType
    public Book bookEntry(Map<String, String> entry) {
        return new Book(
            entry.get("title"),
            entry.get("author"),
            LocalDate.parse(entry.get("published")),
            entry.get("category")
        );
    }
    
    @Given("the following books in the library:")
    public void theFollowingBooksInTheLibrary(List<Book> books) {
        library = new Library();
        books.forEach(library::addBook);
    }
    
    @When("the customer searches for books by author {string}")
    public void theCustomerSearchesForBooksByAuthor(String author) {
        searchResults = library.findBooksByAuthor(author);
    }
    
    @When("the customer searches for books by title {string}")
    public void theCustomerSearchesForBooksByTitle(String title) {
        searchResults = library.findBooksByTitle(title);
    }
    
    @When("the customer searches for books in category {string}")
    public void theCustomerSearchesForBooksInCategory(String category) {
        searchResults = library.findBooksByCategory(category);
    }
    
    /**
     * 6.2b - Step using custom ParameterType for dates
     */
    @When("the customer searches for books published between {iso8601Date} and {iso8601Date}")
    public void theCustomerSearchesForBooksPublishedBetween(LocalDate from, LocalDate to) {
        searchResults = library.findBooks(from, to);
    }
    
    @Then("{int} book(s) should be found")
    public void booksShouldBeFound(int count) {
        assertEquals(count, searchResults.size(), 
            "Expected " + count + " books but found " + searchResults.size());
    }
    
    @And("the book {string} should be in the results")
    public void theBookShouldBeInTheResults(String title) {
        boolean found = searchResults.stream()
            .anyMatch(book -> book.getTitle().equals(title));
        assertTrue(found, "Book '" + title + "' not found in results: " + searchResults);
    }
}
