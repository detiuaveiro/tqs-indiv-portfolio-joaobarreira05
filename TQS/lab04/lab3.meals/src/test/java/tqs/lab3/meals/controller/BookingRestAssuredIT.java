package tqs.lab3.meals.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tqs.lab3.meals.entity.Booking;
import tqs.lab3.meals.entity.BookingStatus;
import tqs.lab3.meals.repository.BookingRepository;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Integration test using REST Assured library for BDD-style API testing.
 * Uses fluent syntax: given().when().then()
 * (Lab 4.4 requirement)
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookingRestAssuredIT {

    @LocalServerPort
    private int port;

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/bookings";
    }

    @AfterEach
    void cleanUp() {
        bookingRepository.deleteAll();
    }

    @Test
    void whenPostBooking_thenReturnToken() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"studentId\":\"stu1\",\"serviceShift\":\"LUNCH\",\"dietaryRequirements\":\"vegan\"}")
        .when()
            .post()
        .then()
            .statusCode(200)
            .body(hasLength(8));
    }

    @Test
    void whenPostInvalidBooking_thenReturnBadRequest() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"studentId\":\"\",\"serviceShift\":\"LUNCH\",\"dietaryRequirements\":\"none\"}")
        .when()
            .post()
        .then()
            .statusCode(400);
    }

    @Test
    void whenGetBookingByToken_thenReturnBookingDetails() {
        Booking booking = new Booking("tok12345", "stu1", "LUNCH", BookingStatus.ACTIVE, "vegan");
        bookingRepository.save(booking);

        given()
        .when()
            .get("/tok12345")
        .then()
            .statusCode(200)
            .body("token", equalTo("tok12345"))
            .body("studentId", equalTo("stu1"))
            .body("serviceShift", equalTo("LUNCH"))
            .body("status", equalTo("ACTIVE"))
            .body("dietaryRequirements", equalTo("vegan"));
    }

    @Test
    void whenGetInvalidToken_thenReturnNotFound() {
        given()
        .when()
            .get("/invalid-token")
        .then()
            .statusCode(404);
    }

    @Test
    void whenGetAllBookings_thenReturnBookingsList() {
        Booking b1 = new Booking("tok1", "stu1", "LUNCH", BookingStatus.ACTIVE, "none");
        Booking b2 = new Booking("tok2", "stu2", "DINNER", BookingStatus.USED, "vegan");
        bookingRepository.save(b1);
        bookingRepository.save(b2);

        given()
        .when()
            .get()
        .then()
            .statusCode(200)
            .body("$", hasSize(2))
            .body("[0].token", equalTo("tok1"))
            .body("[1].token", equalTo("tok2"));
    }

    @Test
    void whenCancelActiveBooking_thenReturnSuccess() {
        Booking booking = new Booking("tok12345", "stu1", "LUNCH", BookingStatus.ACTIVE, "none");
        bookingRepository.save(booking);

        given()
        .when()
            .put("/tok12345/cancel")
        .then()
            .statusCode(200)
            .body(equalTo("Booking cancelled successfully"));
    }

    @Test
    void whenCancelNonActiveBooking_thenReturnBadRequest() {
        Booking booking = new Booking("tok12345", "stu1", "LUNCH", BookingStatus.CANCELLED, "none");
        bookingRepository.save(booking);

        given()
        .when()
            .put("/tok12345/cancel")
        .then()
            .statusCode(400)
            .body(equalTo("Cannot cancel booking"));
    }

    @Test
    void whenCheckInActiveBooking_thenReturnSuccess() {
        Booking booking = new Booking("tok12345", "stu1", "LUNCH", BookingStatus.ACTIVE, "none");
        bookingRepository.save(booking);

        given()
        .when()
            .put("/tok12345/checkin")
        .then()
            .statusCode(200)
            .body(equalTo("Check-in successful"));
    }

    @Test
    void whenCheckInNonActiveBooking_thenReturnBadRequest() {
        Booking booking = new Booking("tok12345", "stu1", "LUNCH", BookingStatus.USED, "none");
        bookingRepository.save(booking);

        given()
        .when()
            .put("/tok12345/checkin")
        .then()
            .statusCode(400)
            .body(equalTo("Cannot check-in"));
    }

    @Test
    void givenMultipleBookings_whenFilterByStatus_thenReturnCorrectCount() {
        Booking active = new Booking("tok1", "stu1", "LUNCH", BookingStatus.ACTIVE, "none");
        Booking used = new Booking("tok2", "stu2", "LUNCH", BookingStatus.USED, "none");
        bookingRepository.save(active);
        bookingRepository.save(used);

        // Get all bookings and verify
        given()
        .when()
            .get()
        .then()
            .statusCode(200)
            .body("$", hasSize(2))
            .body("status", hasItems("ACTIVE", "USED"));
    }
}
