Feature: Citizen cancels a booking
  In order to free the time slot for other citizens
  As a resident who no longer needs the service
  I want to cancel an existing bulky garbage collection booking

  Scenario: Citizen cancels a booking successfully
    Given a citizen submits a valid booking request
    When the citizen cancels the booking using the provided token
    Then the booking status should be "CANCELLED"
    And the booking timeline contains an entry with status "CANCELLED"

  Scenario: Citizen attempts to cancel an unknown booking token
    Given a citizen submits a valid booking request
    When the citizen cancels the booking using an unknown token
    Then the API responds with status 404
    And an error message "Booking with token unknown-token not found" is returned
