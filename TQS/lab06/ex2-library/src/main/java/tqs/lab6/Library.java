package tqs.lab6;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Library service that manages a collection of Books
 */
public class Library {
    
    private final List<Book> store = new ArrayList<>();
    
    public void addBook(Book book) {
        store.add(book);
    }
    
    public List<Book> findBooks(LocalDate from, LocalDate to) {
        return store.stream()
                .filter(book -> {
                    LocalDate published = book.getPublishedDate();
                    return !published.isBefore(from) && !published.isAfter(to);
                })
                .collect(Collectors.toList());
    }
    
    public List<Book> findBooksByAuthor(String author) {
        return store.stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    public List<Book> findBooksByTitle(String title) {
        return store.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    public List<Book> findBooksByCategory(String category) {
        return store.stream()
                .filter(book -> book.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }
    
    public List<Book> getAllBooks() {
        return new ArrayList<>(store);
    }
    
    public void clear() {
        store.clear();
    }
}
