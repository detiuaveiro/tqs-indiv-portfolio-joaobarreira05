package tqs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class TqsStackTest {
    
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
        stack.push(30);
        assertFalse(stack.isEmpty());
        assertEquals(3, stack.size());
        
        stack.push(40);
        stack.push(50);
        assertFalse(stack.isEmpty());
        assertEquals(5, stack.size());
    }
    
    @Test
    void pushThenPop() {
        Integer value = 42;
        stack.push(value);
        Integer result = stack.pop();
        assertEquals(value, result);
        
        stack.push(100);
        assertEquals(Integer.valueOf(100), stack.pop());
        
        stack.push(-5);
        assertEquals(Integer.valueOf(-5), stack.pop());
    }
    

    @Test
    void pushThenPeek() {
        Integer value = 99;
        stack.push(value);
        int sizeBefore = stack.size();
        
        Integer result = stack.peek();
        int sizeAfter = stack.size();
        
        assertEquals(value, result);
        assertEquals(sizeBefore, sizeAfter);
        
        assertEquals(value, stack.pop());
    }
    
    @Test
    void popAllElements() {
        int n = 4;
        for (int i = 1; i <= n; i++) {
            stack.push(i * 10);
        }
        
        assertEquals(n, stack.size());
        assertFalse(stack.isEmpty());
        
        for (int i = 0; i < n; i++) {
            stack.pop();
        }
        
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
    }
    
    @Test
    void popEmptyThrows() {
        assertTrue(stack.isEmpty());
        assertThrows(NoSuchElementException.class, () -> stack.pop());
    }
    
    @Test
    void peekEmptyThrows() {
        assertTrue(stack.isEmpty());
        assertThrows(NoSuchElementException.class, () -> stack.peek());
    }
    
    @Test
    void popTopN() {
        // Push elements: bottom -> top = [10, 20, 30, 40, 50]
        stack.push(10);
        stack.push(20);
        stack.push(30);
        stack.push(40);
        stack.push(50);
        
        assertEquals(5, stack.size());
        
        // popTopN(1) should return 50 (top element)
        Integer result1 = stack.popTopN(1);
        assertEquals(Integer.valueOf(50), result1);
        assertEquals(4, stack.size());
        
        // popTopN(3) should return 20 (discard 40, 30, return 20)
        Integer result2 = stack.popTopN(3);
        assertEquals(Integer.valueOf(20), result2);
        assertEquals(1, stack.size());
        
        // Test edge cases
        assertThrows(IllegalArgumentException.class, () -> stack.popTopN(0));
        assertThrows(IllegalArgumentException.class, () -> stack.popTopN(-1));
        assertThrows(IllegalArgumentException.class, () -> stack.popTopN(2)); // only 1 element left
    }
    
    @Test
    void lifoOrder() {
        stack.push(1);
        stack.push(2);
        stack.push(3);
        
        assertEquals(Integer.valueOf(3), stack.pop());
        assertEquals(Integer.valueOf(2), stack.pop());
        assertEquals(Integer.valueOf(1), stack.pop());
        
        assertTrue(stack.isEmpty());
    }
    
    @Test
    void peekMultipleTimes() {
        stack.push(77);
        
        Integer first = stack.peek();
        Integer second = stack.peek();
        Integer third = stack.peek();
        
        assertEquals(first, second);
        assertEquals(second, third);
        assertEquals(1, stack.size());
        assertFalse(stack.isEmpty());
    }
    

}

