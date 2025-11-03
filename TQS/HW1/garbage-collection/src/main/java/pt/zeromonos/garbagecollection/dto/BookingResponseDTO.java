package pt.zeromonos.garbagecollection.dto;

import pt.zeromonos.garbagecollection.domain.BookingStatus;
import pt.zeromonos.garbagecollection.domain.TimeSlot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record BookingResponseDTO(
        Long id,
        String bookingToken,
        String itemDescription,
        String municipality,
        String fullAddress,
        LocalDate bookingDate,
        TimeSlot timeSlot,
        BookingStatus status,
        LocalDateTime createdAt,
        LocalDateTime lastUpdatedAt,
        List<BookingHistoryEntryDTO> history
) {
}
