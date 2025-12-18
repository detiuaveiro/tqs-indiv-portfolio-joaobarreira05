package tqs.lab6;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Reverse Polish Notation (RPN) Calculator
 * Operates by pushing operands and operators one at a time
 * 
 * Example: To calculate (3 + 4) * 2:
 * push(3), push(4), push("+"), push(2), push("*") -> value() = 14
 */
public class Calculator {
    
    private final Deque<Double> stack = new ArrayDeque<>();
    
    public void push(Object arg) {
        if (arg instanceof Number number) {
            stack.push(number.doubleValue());
        } else if (arg instanceof String operator) {
            performOperation(operator);
        } else {
            throw new IllegalArgumentException("Unknown argument type: " + arg.getClass());
        }
    }
    
    private void performOperation(String operator) {
        if (stack.size() < 2) {
            throw new IllegalStateException("Not enough operands on stack");
        }
        
        double b = stack.pop();
        double a = stack.pop();
        
        double result = switch (operator) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> {
                if (b == 0) throw new ArithmeticException("Division by zero");
                yield a / b;
            }
            case "^" -> Math.pow(a, b);
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
        
        stack.push(result);
    }
    
    public double value() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        return stack.peek();
    }
    
    public void clear() {
        stack.clear();
    }
}
