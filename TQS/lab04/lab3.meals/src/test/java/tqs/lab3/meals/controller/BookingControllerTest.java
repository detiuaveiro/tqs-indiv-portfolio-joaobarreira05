package tqs.lab3.meals.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tqs.lab3.meals.entity.Booking;
import tqs.lab3.meals.entity.BookingStatus;
import tqs.lab3.meals.service.BookingService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller test using @WebMvcTest for sliced context.
 * Only the web layer is loaded, BookingService is mocked.
 * (Test type C from the Employee example)
 */
@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    void whenPostBooking_thenReturnToken() throws Exception {
        when(bookingService.bookMeal("stu1", "LUNCH", "none")).thenReturn("abc12345");

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"studentId\":\"stu1\",\"serviceShift\":\"LUNCH\",\"dietaryRequirements\":\"none\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("abc12345"));

        verify(bookingService, times(1)).bookMeal("stu1", "LUNCH", "none");
    }

    @Test
    void whenPostInvalidBooking_thenReturnBadRequest() throws Exception {
        when(bookingService.bookMeal(any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Student ID cannot be null or empty"));

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"studentId\":\"\",\"serviceShift\":\"LUNCH\",\"dietaryRequirements\":\"none\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetBookingByToken_thenReturnBooking() throws Exception {
        Booking booking = new Booking("tok123", "stu1", "LUNCH", BookingStatus.ACTIVE, "vegan");
        when(bookingService.getBookingDetails("tok123")).thenReturn(booking);

        mockMvc.perform(get("/api/bookings/tok123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("tok123")))
                .andExpect(jsonPath("$.studentId", is("stu1")))
                .andExpect(jsonPath("$.serviceShift", is("LUNCH")))
                .andExpect(jsonPath("$.status", is("ACTIVE")))
                .andExpect(jsonPath("$.dietaryRequirements", is("vegan")));

        verify(bookingService, times(1)).getBookingDetails("tok123");
    }

    @Test
    void whenGetInvalidToken_thenReturnNotFound() throws Exception {
        when(bookingService.getBookingDetails("badtoken"))
                .thenThrow(new IllegalArgumentException("Invalid token"));

        mockMvc.perform(get("/api/bookings/badtoken"))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGetAllBookings_thenReturnBookingsList() throws Exception {
        Booking b1 = new Booking("tok1", "stu1", "LUNCH", BookingStatus.ACTIVE, "none");
        Booking b2 = new Booking("tok2", "stu2", "DINNER", BookingStatus.USED, "vegan");
        List<Booking> bookings = Arrays.asList(b1, b2);

        when(bookingService.getAllBookings()).thenReturn(bookings);

        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].token", is("tok1")))
                .andExpect(jsonPath("$[1].token", is("tok2")));

        verify(bookingService, times(1)).getAllBookings();
    }

    @Test
    void whenCancelBooking_thenReturnSuccess() throws Exception {
        when(bookingService.cancelBooking("tok123")).thenReturn(true);

        mockMvc.perform(put("/api/bookings/tok123/cancel"))
                .andExpect(status().isOk())
                .andExpect(content().string("Booking cancelled successfully"));

        verify(bookingService, times(1)).cancelBooking("tok123");
    }

    @Test
    void whenCancelInvalidBooking_thenReturnBadRequest() throws Exception {
        when(bookingService.cancelBooking("badtoken"))
                .thenThrow(new IllegalArgumentException("Invalid token"));

        mockMvc.perform(put("/api/bookings/badtoken/cancel"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenCancelNonActiveBooking_thenReturnBadRequest() throws Exception {
        when(bookingService.cancelBooking("tok123")).thenReturn(false);

        mockMvc.perform(put("/api/bookings/tok123/cancel"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot cancel booking"));
    }

    @Test
    void whenCheckIn_thenReturnSuccess() throws Exception {
        when(bookingService.checkIn("tok123")).thenReturn(true);

        mockMvc.perform(put("/api/bookings/tok123/checkin"))
                .andExpect(status().isOk())
                .andExpect(content().string("Check-in successful"));

        verify(bookingService, times(1)).checkIn("tok123");
    }

    @Test
    void whenCheckInNonActiveBooking_thenReturnBadRequest() throws Exception {
        when(bookingService.checkIn("tok123")).thenReturn(false);

        mockMvc.perform(put("/api/bookings/tok123/checkin"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot check-in"));
    }
}
