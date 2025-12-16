package tqs.lab3.meals.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tqs.lab3.meals.entity.Booking;
import tqs.lab3.meals.entity.BookingStatus;
import tqs.lab3.meals.repository.BookingRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test using @SpringBootTest with MockMvc.
 * Full application context is loaded (Controller + Service + Repository + DB).
 * (Test type D from the Employee example)
 */
@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingRepository bookingRepository;

    @AfterEach
    void cleanUp() {
        bookingRepository.deleteAll();
    }

    @Test
    void whenPostBooking_thenCreateBooking() throws Exception {
        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"studentId\":\"stu1\",\"serviceShift\":\"LUNCH\",\"dietaryRequirements\":\"none\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.hasLength(8)));

        // Verify booking was persisted
        assertThat(bookingRepository.findAll()).hasSize(1);
        Booking saved = bookingRepository.findAll().get(0);
        assertThat(saved.getStudentId()).isEqualTo("stu1");
        assertThat(saved.getServiceShift()).isEqualTo("LUNCH");
        assertThat(saved.getStatus()).isEqualTo(BookingStatus.ACTIVE);
    }

    @Test
    void whenGetBookingByToken_thenReturnBooking() throws Exception {
        // Create booking directly in repository
        Booking booking = new Booking("tok123", "stu1", "LUNCH", BookingStatus.ACTIVE, "vegan");
        bookingRepository.save(booking);

        mockMvc.perform(get("/api/bookings/tok123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("tok123")))
                .andExpect(jsonPath("$.studentId", is("stu1")))
                .andExpect(jsonPath("$.status", is("ACTIVE")));
    }

    @Test
    void whenGetAllBookings_thenReturnAllBookings() throws Exception {
        Booking b1 = new Booking("tok1", "stu1", "LUNCH", BookingStatus.ACTIVE, "none");
        Booking b2 = new Booking("tok2", "stu2", "DINNER", BookingStatus.USED, "vegan");
        bookingRepository.save(b1);
        bookingRepository.save(b2);

        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void whenCancelActiveBooking_thenStatusBecomesCancelled() throws Exception {
        Booking booking = new Booking("tok123", "stu1", "LUNCH", BookingStatus.ACTIVE, "none");
        bookingRepository.save(booking);

        mockMvc.perform(put("/api/bookings/tok123/cancel"))
                .andExpect(status().isOk())
                .andExpect(content().string("Booking cancelled successfully"));

        // Verify status changed
        Booking updated = bookingRepository.findById("tok123").orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    void whenCheckInActiveBooking_thenStatusBecomesUsed() throws Exception {
        Booking booking = new Booking("tok123", "stu1", "LUNCH", BookingStatus.ACTIVE, "none");
        bookingRepository.save(booking);

        mockMvc.perform(put("/api/bookings/tok123/checkin"))
                .andExpect(status().isOk())
                .andExpect(content().string("Check-in successful"));

        // Verify status changed
        Booking updated = bookingRepository.findById("tok123").orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(BookingStatus.USED);
    }

    @Test
    void givenMultipleBookings_whenFindByStatus_thenReturnFiltered() throws Exception {
        Booking active = new Booking("tok1", "stu1", "LUNCH", BookingStatus.ACTIVE, "none");
        Booking used = new Booking("tok2", "stu2", "LUNCH", BookingStatus.USED, "none");
        Booking cancelled = new Booking("tok3", "stu3", "LUNCH", BookingStatus.CANCELLED, "none");
        bookingRepository.save(active);
        bookingRepository.save(used);
        bookingRepository.save(cancelled);

        // Verify custom query works in integration
        assertThat(bookingRepository.findByStatus(BookingStatus.ACTIVE)).hasSize(1);
        assertThat(bookingRepository.findByStatus(BookingStatus.USED)).hasSize(1);
        assertThat(bookingRepository.findByStatus(BookingStatus.CANCELLED)).hasSize(1);
    }
}
