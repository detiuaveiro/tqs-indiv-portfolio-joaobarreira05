package tqs.lab3.meals.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tqs.lab3.meals.entity.Booking;
import tqs.lab3.meals.entity.BookingStatus;
import tqs.lab3.meals.repository.BookingRepository;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test using @SpringBootTest with TestRestTemplate (real HTTP client).
 * Full application context with real server running on random port.
 * (Test type E from the Employee example)
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookingControllerTemplateIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookingRepository bookingRepository;

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/bookings";
    }

    @AfterEach
    void cleanUp() {
        bookingRepository.deleteAll();
    }

    @Test
    void whenPostBooking_thenReturnToken() {
        Map<String, String> request = new HashMap<>();
        request.put("studentId", "stu1");
        request.put("serviceShift", "LUNCH");
        request.put("dietaryRequirements", "vegan");

        ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl(), request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(8);

        // Verify booking was persisted
        assertThat(bookingRepository.findAll()).hasSize(1);
    }

    @Test
    void whenGetBookingByToken_thenReturnBooking() {
        // Create booking directly in repository
        Booking booking = new Booking("tok12345", "stu1", "LUNCH", BookingStatus.ACTIVE, "vegan");
        bookingRepository.save(booking);

        ResponseEntity<Booking> response = restTemplate.getForEntity(
                getBaseUrl() + "/tok12345", Booking.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isEqualTo("tok12345");
        assertThat(response.getBody().getStudentId()).isEqualTo("stu1");
        assertThat(response.getBody().getStatus()).isEqualTo(BookingStatus.ACTIVE);
    }

    @Test
    void whenGetAllBookings_thenReturnArray() {
        Booking b1 = new Booking("tok1", "stu1", "LUNCH", BookingStatus.ACTIVE, "none");
        Booking b2 = new Booking("tok2", "stu2", "DINNER", BookingStatus.USED, "vegan");
        bookingRepository.save(b1);
        bookingRepository.save(b2);

        ResponseEntity<Booking[]> response = restTemplate.getForEntity(getBaseUrl(), Booking[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void whenCancelBooking_thenReturnSuccessMessage() {
        Booking booking = new Booking("tok12345", "stu1", "LUNCH", BookingStatus.ACTIVE, "none");
        bookingRepository.save(booking);

        // Using put method for cancel
        restTemplate.put(getBaseUrl() + "/tok12345/cancel", null);

        // Verify status changed
        Booking updated = bookingRepository.findById("tok12345").orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    void whenCheckIn_thenReturnSuccessMessage() {
        Booking booking = new Booking("tok12345", "stu1", "LUNCH", BookingStatus.ACTIVE, "none");
        bookingRepository.save(booking);

        // Using put method for checkin
        restTemplate.put(getBaseUrl() + "/tok12345/checkin", null);

        // Verify status changed
        Booking updated = bookingRepository.findById("tok12345").orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(BookingStatus.USED);
    }

    @Test
    void whenGetInvalidToken_thenReturnNotFound() {
        ResponseEntity<Booking> response = restTemplate.getForEntity(
                getBaseUrl() + "/invalid-token", Booking.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
