# Lab 6.3 - Online Library Book Search Feature
# Web automation with Cucumber + Selenium

Feature: Online Library Book Search
  As a library user
  I want to search for books on the online library website
  So that I can find books I'm interested in

  Background:
    Given I am on the online library homepage

  Scenario: Search for a book by title
    When I search for "Harry Potter"
    Then the search results should contain "Harry Potter"

  Scenario: Search for a book by author
    When I search for "Rowling"
    Then the search results should display book information

  Scenario: Search with no results
    When I search for "xyznonexistentbook123"
    Then no books should be found or a message should appear

  Scenario: Navigate to book details
    When I search for "Harry Potter"
    And I click on the first book result
    Then I should see book details

  Scenario: Verify page title
    Then the page title should contain "Bookstore" or "Library" or "Books"
