package pt.zeromonos.garbagecollection.service;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.zeromonos.garbagecollection.domain.BookingRequest;
import pt.zeromonos.garbagecollection.domain.BookingStatus;
import pt.zeromonos.garbagecollection.domain.BookingStatusHistory;
import pt.zeromonos.garbagecollection.domain.TimeSlot;
import pt.zeromonos.garbagecollection.dto.BookingHistoryEntryDTO;
import pt.zeromonos.garbagecollection.dto.BookingRequestDTO;
import pt.zeromonos.garbagecollection.dto.BookingResponseDTO;
import pt.zeromonos.garbagecollection.repository.BookingRequestRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private static final int MAX_BOOKINGS_PER_SLOT = 5;

    @Autowired
    private BookingRequestRepository bookingRepository;

    @Autowired
    private GeoApiService geoApiService;

    public List<String> getAvailableMunicipalities() {
        return geoApiService.getMunicipalities();
    }

    public BookingRequest createBooking(BookingRequestDTO dto) {
        // 1. Validar os dados
        List<String> validMunicipalities = geoApiService.getMunicipalities();
        if (dto.getMunicipality() == null || !validMunicipalities.contains(dto.getMunicipality())) {
            logger.warn("Attempt to create booking with invalid municipality: {}", dto.getMunicipality());
            throw new IllegalArgumentException("Municipality not available for service or is null: " + dto.getMunicipality());
        }

        if (dto.getBookingDate() == null || dto.getBookingDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Booking date must be today or in the future.");
        }

        if (dto.getTimeSlot() == null) {
            throw new IllegalArgumentException("Booking time slot must be provided.");
        }

        // 2. Criar a entidade a partir do DTO
        BookingRequest newBooking = new BookingRequest(
                dto.getItemDescription(),
                dto.getMunicipality(),
                dto.getFullAddress(),
                dto.getBookingDate(),
                dto.getTimeSlot()
        );

        enforceCapacityLimits(newBooking.getMunicipality(), newBooking.getBookingDate(), newBooking.getTimeSlot());

        // 3. Guardar na base de dados
        BookingRequest savedBooking = bookingRepository.save(newBooking);
        logger.info("New booking created with token: {}", savedBooking.getBookingToken());

        return savedBooking;
    }

    public Optional<BookingRequest> findBookingByToken(String token) {
        return bookingRepository.findByBookingToken(token);
    }

    public List<BookingRequest> findBookingsByMunicipality(String municipality) {
        return bookingRepository.findByMunicipality(municipality);
    }

    public BookingRequest updateBookingStatus(Long bookingId, BookingStatus newStatus) {
        Objects.requireNonNull(bookingId, "Booking id cannot be null");

        if (newStatus == null) {
            throw new IllegalArgumentException("Booking status cannot be null");
        }

        BookingRequest booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking with id " + bookingId + " not found"));

        LocalDateTime now = LocalDateTime.now();

        booking.setStatus(newStatus);
        booking.setLastUpdatedAt(now);
        booking.addHistoryEntry(newStatus, "Estado atualizado pela equipa", now);

        BookingRequest saved = bookingRepository.save(booking);
        logger.info("Booking {} status updated to {}", saved.getBookingToken(), saved.getStatus());
        return saved;
    }

    public BookingRequest cancelBookingByToken(String token) {
        BookingRequest booking = bookingRepository.findByBookingToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Booking with token " + token + " not found"));

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Não é possível cancelar uma recolha já concluída.");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return booking; // já cancelado
        }

        LocalDateTime now = LocalDateTime.now();
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setLastUpdatedAt(now);
        booking.addHistoryEntry(BookingStatus.CANCELLED, "Pedido cancelado pelo cidadão", now);

        BookingRequest saved = bookingRepository.save(booking);
        logger.info("Booking {} cancelled by token", saved.getBookingToken());
        return saved;
    }

    public BookingResponseDTO toResponseDto(BookingRequest booking) {
        List<BookingHistoryEntryDTO> historyEntries = booking.getStatusHistory().stream()
                .sorted(Comparator.comparing(BookingStatusHistory::getChangedAt))
                .map(entry -> new BookingHistoryEntryDTO(
                        entry.getStatus(),
                        entry.getChangedAt(),
                        entry.getNote()
                ))
                .collect(Collectors.toUnmodifiableList());

        return new BookingResponseDTO(
                booking.getId(),
                booking.getBookingToken(),
                booking.getItemDescription(),
                booking.getMunicipality(),
                booking.getFullAddress(),
                booking.getBookingDate(),
                booking.getTimeSlot(),
                booking.getStatus(),
                booking.getCreatedAt(),
                booking.getLastUpdatedAt(),
                historyEntries
        );
    }

    public List<BookingResponseDTO> toResponseDtoList(List<BookingRequest> bookings) {
        return bookings.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    private void enforceCapacityLimits(String municipality, LocalDate date, TimeSlot timeSlot) {
        long existing = bookingRepository.countByMunicipalityAndBookingDateAndTimeSlot(municipality, date, timeSlot);
        if (existing >= MAX_BOOKINGS_PER_SLOT) {
            throw new IllegalStateException("Limite de marcações atingido para " + municipality + " em " + date + " (" + timeSlot + ")");
        }
    }
}