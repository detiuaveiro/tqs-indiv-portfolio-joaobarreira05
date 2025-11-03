package pt.zeromonos.garbagecollection.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pt.zeromonos.garbagecollection.domain.BookingStatus;
import pt.zeromonos.garbagecollection.domain.TimeSlot;
import pt.zeromonos.garbagecollection.dto.BookingHistoryEntryDTO;
import pt.zeromonos.garbagecollection.dto.BookingRequestDTO;
import pt.zeromonos.garbagecollection.dto.BookingResponseDTO;
import pt.zeromonos.garbagecollection.dto.ErrorResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookingCancellationSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<BookingResponseDTO> bookingCreationResponse;
    private ResponseEntity<BookingResponseDTO> cancellationResponse;
    private ResponseEntity<ErrorResponseDTO> errorResponse;
    private String bookingToken;

    @Given("a citizen submits a valid booking request")
    public void aCitizenSubmitsAValidBookingRequest() {
        BookingRequestDTO request = new BookingRequestDTO();
        request.setMunicipality("Lisboa");
        request.setItemDescription("Candeeiro de chão");
        request.setFullAddress("Rua do BDD, 456");
        request.setBookingDate(LocalDate.now().plusDays(3));
        request.setTimeSlot(TimeSlot.MORNING);

        bookingCreationResponse = restTemplate.postForEntity(apiUrl("/api/bookings"), request, BookingResponseDTO.class);
        Assertions.assertThat(bookingCreationResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        BookingResponseDTO body = Objects.requireNonNull(bookingCreationResponse.getBody(), "Resposta de criação não pode ser nula");
        bookingToken = body.bookingToken();

        cancellationResponse = null;
        errorResponse = null;
    }

    @When("the citizen cancels the booking using the provided token")
    public void theCitizenCancelsTheBookingUsingTheProvidedToken() {
        cancellationResponse = restTemplate.exchange(
                apiUrl("/api/bookings/token/" + bookingToken),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                BookingResponseDTO.class
        );
    }

    @Then("the booking status should be \"{string}\"")
    public void theBookingStatusShouldBe(String expectedStatus) {
        Assertions.assertThat(cancellationResponse).as("A resposta de cancelamento deve existir").isNotNull();
        Assertions.assertThat(cancellationResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        BookingResponseDTO body = Objects.requireNonNull(cancellationResponse.getBody(), "Corpo de cancelamento não pode ser nulo");
        BookingStatus status = BookingStatus.valueOf(expectedStatus);
        Assertions.assertThat(body.status()).isEqualTo(status);
    }

    @And("the booking timeline contains an entry with status \"{string}\"")
    public void theBookingTimelineContainsAnEntryWithStatus(String expectedStatus) {
        BookingResponseDTO body = Objects.requireNonNull(cancellationResponse.getBody());
        List<BookingHistoryEntryDTO> history = Objects.requireNonNull(body.history(), "Histórico não deve ser nulo");
    BookingStatus status = BookingStatus.valueOf(expectedStatus);
        Assertions.assertThat(history)
                .as("Histórico deve conter o estado esperado")
        .anyMatch(entry -> entry.status() == status);
    }

    @When("the citizen cancels the booking using an unknown token")
    public void theCitizenCancelsTheBookingUsingAnUnknownToken() {
        errorResponse = restTemplate.exchange(
                apiUrl("/api/bookings/token/unknown-token"),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                ErrorResponseDTO.class
        );
    }

    @Then("the API responds with status {int}")
    public void theApiRespondsWithStatus(int statusCode) {
        Assertions.assertThat(errorResponse).as("Resposta de erro deve existir").isNotNull();
        Assertions.assertThat(errorResponse.getStatusCode().value()).isEqualTo(statusCode);
    }

    @And("an error message \"{string}\" is returned")
    public void anErrorMessageIsReturned(String message) {
        ErrorResponseDTO body = Objects.requireNonNull(errorResponse.getBody(), "Corpo de erro não deve ser nulo");
        Assertions.assertThat(body.message()).isEqualTo(message);
    }

    private String apiUrl(String path) {
        return "http://localhost:" + port + path;
    }
}
