package tqs.lab6;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Step definitions for the Calculator feature
 * Uses Cucumber expressions (not regex) as per best practices
 */
public class CalculatorSteps {
    
    private Calculator calculator;
    
    @Given("a calculator I just turned on")
    public void aCalculatorIJustTurnedOn() {
        calculator = new Calculator();
    }
    
    @When("I push {int}")
    public void iPushNumber(int number) {
        calculator.push(number);
    }
    
    @When("I push {string}")
    public void iPushOperator(String operator) {
        calculator.push(operator);
    }
    
    @Then("the result is {double}")
    public void theResultIs(double expected) {
        assertEquals(expected, calculator.value(), 0.0001);
    }
}
