package tqs;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class TqsStack<T> {
    
    private List<T> elements;
    
    public TqsStack() {
        elements = new ArrayList<>();
    }
    
    public void push(T item) {
        elements.add(item);
    }
    
    public T pop() {
        if (isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        return elements.remove(elements.size() - 1);
    }
    
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Stack is empty");
        }
        return elements.get(elements.size() - 1);
    }
    
    public int size() {
        return elements.size();
    }
    
    public boolean isEmpty() {
        return elements.isEmpty();
    }
    
    public T popTopN(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be positive");
        }
        if (n > size()) {
            throw new IllegalArgumentException("n cannot be greater than stack size");
        }
        
        // Remove n-1 elements from the top
        for (int i = 0; i < n - 1; i++) {
            elements.remove(elements.size() - 1);
        }
        
        // Return the nth element (which is now at the top)
        return elements.remove(elements.size() - 1);
    }
}
