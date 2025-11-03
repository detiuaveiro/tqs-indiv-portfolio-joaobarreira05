package pt.zeromonos.garbagecollection.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.zeromonos.garbagecollection.domain.BookingRequest;
import pt.zeromonos.garbagecollection.domain.BookingStatus;
import pt.zeromonos.garbagecollection.domain.TimeSlot;
import pt.zeromonos.garbagecollection.dto.BookingRequestDTO;
import pt.zeromonos.garbagecollection.repository.BookingRequestRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"null", "DataFlowIssue"})
class BookingServiceTest {

    @Mock
    private BookingRequestRepository bookingRepository;

    @Mock
    private GeoApiService geoApiService;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void whenCreateBooking_withValidData_thenBookingIsSaved() {
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setMunicipality("Lisboa");
        dto.setItemDescription("Uma secretária");
        dto.setFullAddress("Rua das Flores, 10");
        dto.setBookingDate(LocalDate.now().plusDays(3));
        dto.setTimeSlot(TimeSlot.MORNING);

        when(geoApiService.getMunicipalities()).thenReturn(List.of("Lisboa", "Porto"));
        when(bookingRepository.countByMunicipalityAndBookingDateAndTimeSlot(eq("Lisboa"), any(LocalDate.class), eq(TimeSlot.MORNING)))
                .thenReturn(0L);
        when(bookingRepository.save(argThat(Objects::nonNull))).thenAnswer(invocation -> {
            BookingRequest request = invocation.getArgument(0, BookingRequest.class);
            request.setId(1L);
            request.setBookingToken("token-123");
            return request;
        });

        BookingRequest saved = bookingService.createBooking(dto);

        assertEquals(1L, saved.getId());
        assertEquals("Lisboa", saved.getMunicipality());
        assertEquals(BookingStatus.RECEIVED, saved.getStatus());
        assertEquals(1, saved.getStatusHistory().size());

        ArgumentCaptor<BookingRequest> captor = ArgumentCaptor.forClass(BookingRequest.class);
        verify(bookingRepository).save(captor.capture());
        verify(geoApiService).getMunicipalities();
        verify(bookingRepository).countByMunicipalityAndBookingDateAndTimeSlot(eq("Lisboa"), any(LocalDate.class), eq(TimeSlot.MORNING));
        assertEquals("Lisboa", captor.getValue().getMunicipality());
    }

    @Test
    void whenCreateBooking_withInvalidMunicipality_thenThrowException() {
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setMunicipality("Narnia");
        dto.setBookingDate(LocalDate.now().plusDays(1));
        dto.setTimeSlot(TimeSlot.AFTERNOON);

        when(geoApiService.getMunicipalities()).thenReturn(List.of("Lisboa", "Porto"));

        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(dto));
        verify(bookingRepository, never()).save(argThat(Objects::nonNull));
    }

    @Test
    void whenCreateBooking_withPastDate_thenThrowException() {
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setMunicipality("Lisboa");
        dto.setBookingDate(LocalDate.now().minusDays(1));
        dto.setTimeSlot(TimeSlot.MORNING);

        when(geoApiService.getMunicipalities()).thenReturn(List.of("Lisboa"));

        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(dto));
        verify(bookingRepository, never()).save(argThat(Objects::nonNull));
    }

    @Test
    void whenCreateBooking_withoutTimeSlot_thenThrowException() {
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setMunicipality("Lisboa");
        dto.setBookingDate(LocalDate.now().plusDays(2));

        when(geoApiService.getMunicipalities()).thenReturn(List.of("Lisboa"));

        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(dto));
        verify(bookingRepository, never()).save(argThat(Objects::nonNull));
    }

    @Test
    void whenCreateBooking_exceedsCapacity_thenThrowException() {
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setMunicipality("Lisboa");
        dto.setItemDescription("Máquina de lavar");
        dto.setFullAddress("Rua das Flores, 12");
        dto.setBookingDate(LocalDate.now().plusDays(2));
        dto.setTimeSlot(TimeSlot.MORNING);

        when(geoApiService.getMunicipalities()).thenReturn(List.of("Lisboa"));
        when(bookingRepository.countByMunicipalityAndBookingDateAndTimeSlot(eq("Lisboa"), any(LocalDate.class), eq(TimeSlot.MORNING)))
                .thenReturn(BookingServiceTestHelper.MAX_PER_SLOT());

        assertThrows(IllegalStateException.class, () -> bookingService.createBooking(dto));
    verify(bookingRepository, never()).save(argThat(Objects::nonNull));
    }

    @Test
    void whenUpdateBookingStatus_withValidData_thenHistoryIsUpdated() {
        BookingRequest existing = new BookingRequest(
                "Frigorífico",
                "Lisboa",
                "Rua Verde, 5",
                LocalDate.now().plusDays(4),
                TimeSlot.AFTERNOON
        );
        existing.setId(1L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(bookingRepository.save(argThat(Objects::nonNull))).thenAnswer(invocation -> invocation.getArgument(0, BookingRequest.class));

        BookingRequest updated = bookingService.updateBookingStatus(1L, BookingStatus.IN_PROGRESS);

        assertEquals(BookingStatus.IN_PROGRESS, updated.getStatus());
        assertEquals(2, updated.getStatusHistory().size());
        assertEquals(BookingStatus.IN_PROGRESS, updated.getStatusHistory().get(1).getStatus());

        verify(bookingRepository).findById(1L);
    verify(bookingRepository).save(argThat(Objects::nonNull));
    }

    @Test
    void whenUpdateBookingStatus_withNullStatus_thenThrowException() {
        assertThrows(IllegalArgumentException.class, () -> bookingService.updateBookingStatus(1L, null));
        verify(bookingRepository, never()).findById(anyLong());
    }

    @Test
    void whenUpdateBookingStatus_unknownBooking_thenThrowException() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.updateBookingStatus(99L, BookingStatus.COMPLETED));
        verify(bookingRepository).findById(99L);
    }

    @Test
    void whenCancelBooking_withValidToken_thenStatusBecomesCancelled() {
        BookingRequest existing = new BookingRequest(
                "Televisão",
                "Lisboa",
                "Rua Azul, 8",
                LocalDate.now().plusDays(3),
                TimeSlot.MORNING
        );
        existing.setBookingToken("token-xyz");

        when(bookingRepository.findByBookingToken("token-xyz")).thenReturn(Optional.of(existing));
        when(bookingRepository.save(existing)).thenAnswer(invocation -> invocation.getArgument(0));

        BookingRequest cancelled = bookingService.cancelBookingByToken("token-xyz");

        assertEquals(BookingStatus.CANCELLED, cancelled.getStatus());
        assertEquals(2, cancelled.getStatusHistory().size());
        assertEquals(BookingStatus.CANCELLED, cancelled.getStatusHistory().get(1).getStatus());

        verify(bookingRepository).findByBookingToken("token-xyz");
    verify(bookingRepository).save(existing);
    }

    @Test
    void whenCancelBooking_completedBooking_thenThrowException() {
        BookingRequest existing = new BookingRequest(
                "Armário",
                "Lisboa",
                "Rua Vermelha, 12",
                LocalDate.now().plusDays(1),
                TimeSlot.AFTERNOON
        );
        existing.setBookingToken("done");
        existing.setStatus(BookingStatus.COMPLETED);

        when(bookingRepository.findByBookingToken("done")).thenReturn(Optional.of(existing));

        assertThrows(IllegalStateException.class, () -> bookingService.cancelBookingByToken("done"));
    verify(bookingRepository, never()).save(argThat(Objects::nonNull));
    }

    @Test
    void whenCancelBooking_unknownToken_thenThrowException() {
        when(bookingRepository.findByBookingToken("unknown")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.cancelBookingByToken("unknown"));
    }

    private static class BookingServiceTestHelper {
        static long MAX_PER_SLOT() {
            return 5L;
        }
    }
}