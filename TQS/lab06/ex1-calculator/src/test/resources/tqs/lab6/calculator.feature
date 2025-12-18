# Lab 6.1 - RPN Calculator Feature
# Cucumber feature file describing the behavior of the calculator

Feature: RPN Calculator
  As a user of the RPN calculator
  I want to perform mathematical operations
  So that I can calculate complex expressions

  Background:
    Given a calculator I just turned on

  # 6.1b - Basic addition scenario
  Scenario: Addition of two numbers
    When I push 3
    And I push 4
    And I push "+"
    Then the result is 7.0

  # 6.1e - More scenarios
  Scenario: Subtraction of two numbers
    When I push 10
    And I push 3
    And I push "-"
    Then the result is 7.0

  Scenario: Multiplication of two numbers
    When I push 6
    And I push 7
    And I push "*"
    Then the result is 42.0

  Scenario: Division of two numbers
    When I push 20
    And I push 4
    And I push "/"
    Then the result is 5.0

  Scenario: Complex expression (3 + 4) * 2
    When I push 3
    And I push 4
    And I push "+"
    And I push 2
    And I push "*"
    Then the result is 14.0

  Scenario: Power operation
    When I push 2
    And I push 10
    And I push "^"
    Then the result is 1024.0

  Scenario Outline: Multiple operations
    When I push <a>
    And I push <b>
    And I push "<op>"
    Then the result is <result>

    Examples:
      | a  | b  | op | result |
      | 5  | 3  | +  | 8.0    |
      | 10 | 4  | -  | 6.0    |
      | 6  | 7  | *  | 42.0   |
      | 15 | 3  | /  | 5.0    |
