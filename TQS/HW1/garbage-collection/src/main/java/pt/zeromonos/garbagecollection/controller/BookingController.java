package pt.zeromonos.garbagecollection.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import pt.zeromonos.garbagecollection.dto.BookingRequestDTO;
import pt.zeromonos.garbagecollection.dto.BookingResponseDTO;
import pt.zeromonos.garbagecollection.dto.ErrorResponseDTO;
import pt.zeromonos.garbagecollection.dto.UpdateBookingStatusDTO;
import pt.zeromonos.garbagecollection.service.BookingService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings") // Prefixo para todos os endpoints nesta classe
public class BookingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private BookingService bookingService;

    // Endpoint para obter a lista de municípios
    // GET http://localhost:8080/api/bookings/municipalities
    @GetMapping("/municipalities")
    public ResponseEntity<List<String>> getMunicipalities() {
        List<String> municipalities = bookingService.getAvailableMunicipalities();
        return new ResponseEntity<>(municipalities, HttpStatus.OK);
    }

    // Endpoint para criar um novo agendamento
    // POST http://localhost:8080/api/bookings
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequestDTO bookingDto) {
        try {
            BookingResponseDTO createdBooking = bookingService.toResponseDto(bookingService.createBooking(bookingDto));
            return new ResponseEntity<>(createdBooking, HttpStatus.CREATED); // 201 Created
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDTO(e.getMessage()));
        }
    }

    // Endpoint para consultar um agendamento pelo token
    // GET http://localhost:8080/api/bookings/token/some-uuid-token
    @GetMapping("/token/{token}")
    public ResponseEntity<BookingResponseDTO> getBookingByToken(@PathVariable String token) {
        Optional<BookingResponseDTO> booking = bookingService.findBookingByToken(token)
                .map(bookingService::toResponseDto);

        // 'map' é uma forma elegante de lidar com o Optional.
        // Se o booking existir, executa a primeira parte. Se não, a segunda.
        return booking.map(b -> new ResponseEntity<>(b, HttpStatus.OK))
                      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND)); // 404 Not Found
    }

    // Endpoint para a equipa (staff) ver os agendamentos por município
    // GET http://localhost:8080/api/bookings/staff/Lisboa
    @GetMapping("/staff/{municipality}")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsForStaff(@PathVariable String municipality) {
        List<BookingResponseDTO> bookings = bookingService.toResponseDtoList(bookingService.findBookingsByMunicipality(municipality));
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @PatchMapping("/staff/{bookingId}/status")
    public ResponseEntity<?> updateBookingStatus(@PathVariable Long bookingId,
                                                 @RequestBody UpdateBookingStatusDTO updateBookingStatusDTO) {
        try {
            BookingResponseDTO updatedBooking = bookingService.toResponseDto(
                    bookingService.updateBookingStatus(bookingId, updateBookingStatusDTO.getStatus()));
            return ResponseEntity.ok(updatedBooking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponseDTO(e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(e.getMessage()));
        }
    }

    @DeleteMapping("/token/{token}")
    public ResponseEntity<?> cancelBookingByToken(@PathVariable String token) {
        try {
            BookingResponseDTO cancelled = bookingService.toResponseDto(bookingService.cancelBookingByToken(token));
            return ResponseEntity.ok(cancelled);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDTO(e.getMessage()));
        }
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleUnreadableMessage(HttpMessageNotReadableException ex) {
        LOGGER.warn("Failed to parse request payload for booking status update", ex);
        return new ErrorResponseDTO("Estado inválido");
    }
}