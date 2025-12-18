# Lab 6.2 - Library Book Search Feature
# Demonstrates DataTables and custom ParameterTypes for dates

Feature: Book Search
  As a library user
  I want to search for books
  So that I can find books that interest me

  # 6.2c - Using DataTable to load books upfront
  Background:
    Given the following books in the library:
      | title                           | author         | published  | category   |
      | One good book                   | Anonymous      | 2013-03-12 | Fiction    |
      | Some other book                 | Tim Tomson     | 2020-08-23 | Science    |
      | Harry Potter and the Sorcerer's Stone | J.K. Rowling | 1997-06-26 | Fantasy |
      | Clean Code                      | Robert Martin  | 2008-08-01 | Technology |
      | The Pragmatic Programmer        | David Thomas   | 2019-09-13 | Technology |

  # 6.2a - Search by author
  Scenario: Search books by author
    When the customer searches for books by author "Robert Martin"
    Then 1 book should be found
    And the book "Clean Code" should be in the results

  Scenario: Search books by author with multiple results
    When the customer searches for books by author "Thomas"
    Then 1 book should be found

  Scenario: Search books by author with no results
    When the customer searches for books by author "Unknown Author"
    Then 0 books should be found

  # 6.2a - Search by title
  Scenario: Search books by title
    When the customer searches for books by title "Harry Potter"
    Then 1 book should be found
    And the book "Harry Potter and the Sorcerer's Stone" should be in the results

  # 6.2a - Search by category
  Scenario: Search books by category
    When the customer searches for books in category "Technology"
    Then 2 books should be found

  Scenario: Search books in non-existent category
    When the customer searches for books in category "Romance"
    Then 0 books should be found

  # 6.2b - Search by date range (uses custom ParameterType)
  Scenario: Search books published between dates
    When the customer searches for books published between 2010-01-01 and 2021-12-31
    Then 3 books should be found
    And the book "One good book" should be in the results
    And the book "Some other book" should be in the results

  Scenario: Search books published in specific year range
    When the customer searches for books published between 2019-01-01 and 2020-12-31
    Then 2 books should be found

  Scenario: Search books published before 2000
    When the customer searches for books published between 1990-01-01 and 1999-12-31
    Then 1 book should be found
    And the book "Harry Potter and the Sorcerer's Stone" should be in the results
