package pt.zeromonos.garbagecollection.dto;

import pt.zeromonos.garbagecollection.domain.BookingStatus;

import java.time.LocalDateTime;

public record BookingHistoryEntryDTO(BookingStatus status, LocalDateTime changedAt, String note) {
}
