package tqs.lab6;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Book entity for the Library
 */
public class Book {
    
    private final String title;
    private final String author;
    private final LocalDate publishedDate;
    private final String category;
    
    public Book(String title, String author, LocalDate publishedDate, String category) {
        this.title = title;
        this.author = author;
        this.publishedDate = publishedDate;
        this.category = category;
    }
    
    public Book(String title, String author, LocalDate publishedDate) {
        this(title, author, publishedDate, "General");
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public LocalDate getPublishedDate() {
        return publishedDate;
    }
    
    public String getCategory() {
        return category;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(title, book.title) && 
               Objects.equals(author, book.author);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(title, author);
    }
    
    @Override
    public String toString() {
        return String.format("Book{title='%s', author='%s', published=%s, category='%s'}", 
                title, author, publishedDate, category);
    }
}
