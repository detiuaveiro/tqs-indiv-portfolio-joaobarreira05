package tqs;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class __TqsStackTest__ {
    
    private TqsStack<Integer> stack;
    
    @BeforeEach
    void setup() {
        stack = new TqsStack<>();
    }
    
    @Test
    void emptyOnConstruction() {
        assertTrue(stack.isEmpty());
    }
    
    @Test
    void sizeZeroOnConstruction() {
        assertEquals(0, stack.size());
    }
    
    @Test
    void pushMakesStackNotEmpty() {
        stack.push(10);
        assertFalse(stack.isEmpty());
        assertEquals(1, stack.size());
        
        stack.push(20);
        assertFalse(stack.isEmpty());
        assertEquals(2, stack.size());
    }
    
    @Test
    void pushThenPop() {
        stack.push(42);
        Integer result = stack.pop();
        assertEquals(42, result);
    }
    

    @Test
    void pushThenPeek() {
        stack.push(99);
        Integer result = stack.peek();
        assertEquals(99, result);
        assertEquals(1, stack.size());
    }
    
    @Test
    void popAllElements() {
        stack.push(10);
        stack.push(20);
        stack.push(30);
        
        stack.pop();
        stack.pop();
        stack.pop();
        
        assertTrue(stack.isEmpty());
    }
    
    @Test
    void popEmptyThrows() {
        assertThrows(NoSuchElementException.class, () -> stack.pop());
    }
    
    @Test
    void peekEmptyThrows() {
        assertThrows(NoSuchElementException.class, () -> stack.peek());
    }
}