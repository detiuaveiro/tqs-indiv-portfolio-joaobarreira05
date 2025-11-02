package tqs.lab3.meals.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import tqs.lab3.meals.entity.Booking;
import tqs.lab3.meals.entity.BookingStatus;
import tqs.lab3.meals.repository.BookingRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    private BookingService bookingService;
    private BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        bookingService = new BookingService();
        bookingRepository = Mockito.mock(BookingRepository.class);
        // inject mock into private field
        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingRepository);
    }

    @Test
    void bookMeal_success() {
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String token = bookingService.bookMeal("stu1", "LUNCH", "none");

        assertNotNull(token);
        assertEquals(8, token.length());

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository, times(1)).save(captor.capture());

        Booking saved = captor.getValue();
        assertEquals("stu1", saved.getStudentId());
        assertEquals("LUNCH", saved.getServiceShift());
        assertEquals(BookingStatus.ACTIVE, saved.getStatus());
    }

    @Test
    void bookMeal_nullStudent_throws() {
        assertThrows(IllegalArgumentException.class, () -> bookingService.bookMeal(null, "LUNCH", ""));
    }

    @Test
    void bookMeal_duplicateStudent_throws() {
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String token = bookingService.bookMeal("stu2", "DINNER", "v");
        assertNotNull(token);

        // second booking same student same shift should fail
        assertThrows(IllegalStateException.class, () -> bookingService.bookMeal("stu2", "DINNER", "v"));
    }

    @Test
    void bookMeal_noCapacity_throws() {
        // set capacity to 0 for a custom shift
        ReflectionTestUtils.invokeMethod(bookingService, "getAvailableCapacity", "MORNING");
        // directly manipulate shiftCapacities via ReflectionTestUtils
        ReflectionTestUtils.setField(bookingService, "shiftCapacities", java.util.Map.of("MORNING", 0));

        assertThrows(IllegalStateException.class, () -> bookingService.bookMeal("stu3", "MORNING", ""));
    }

    @Test
    void getBookingDetails_success_and_invalid() {
        Booking b = new Booking("tok1", "s1", "LUNCH", BookingStatus.ACTIVE, "none");
        when(bookingRepository.findById("tok1")).thenReturn(Optional.of(b));

        Booking found = bookingService.getBookingDetails("tok1");
        assertEquals("s1", found.getStudentId());

        when(bookingRepository.findById("bad")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> bookingService.getBookingDetails("bad"));
    }

    @Test
    void cancelBooking_flow() {
        Booking b = new Booking("tok2", "s2", "LUNCH", BookingStatus.ACTIVE, "x");
        when(bookingRepository.findById("tok2")).thenReturn(Optional.of(b));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        boolean cancelled = bookingService.cancelBooking("tok2");
        assertTrue(cancelled);
        assertEquals(BookingStatus.CANCELLED, b.getStatus());

        // cancelling again should return false (status not ACTIVE)
        when(bookingRepository.findById("tok2")).thenReturn(Optional.of(b));
        assertFalse(bookingService.cancelBooking("tok2"));
    }

    @Test
    void checkIn_flow() {
        Booking b = new Booking("tok3", "s3", "LUNCH", BookingStatus.ACTIVE, "x");
        when(bookingRepository.findById("tok3")).thenReturn(Optional.of(b));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(i -> i.getArgument(0));

        boolean checked = bookingService.checkIn("tok3");
        assertTrue(checked);
        assertEquals(BookingStatus.USED, b.getStatus());

        // check-in non-active
        when(bookingRepository.findById("tok3")).thenReturn(Optional.of(b));
        assertFalse(bookingService.checkIn("tok3"));
    }
}
