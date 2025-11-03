package pt.zeromonos.garbagecollection.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pt.zeromonos.garbagecollection.domain.BookingRequest;
import pt.zeromonos.garbagecollection.domain.BookingStatus;
import pt.zeromonos.garbagecollection.domain.TimeSlot;
import pt.zeromonos.garbagecollection.dto.BookingHistoryEntryDTO;
import pt.zeromonos.garbagecollection.dto.BookingRequestDTO;
import pt.zeromonos.garbagecollection.dto.BookingResponseDTO;
import pt.zeromonos.garbagecollection.service.BookingService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Carrega apenas a camada Web (o nosso controller) e não a aplicação inteira. É mais rápido.
@WebMvcTest(BookingController.class)
@SuppressWarnings({"null", "removal"})
class BookingControllerTest {

    // O MockMvc é a nossa ferramenta para simular os pedidos HTTP.
    @Autowired
    private MockMvc mockMvc;

    // Usamos @MockBean em vez de @Mock.
    // O @MockBean substitui o bean real do BookingService no contexto da aplicação
    // por um mock do Mockito. Essencial para isolar a camada web.
    @MockBean
    private BookingService bookingService;
    
    // O ObjectMapper ajuda-nos a converter objetos Java para uma string JSON.
    @Autowired
    private ObjectMapper objectMapper;


    // Testar o POST para criar um agendamento com sucesso.
    @Test
    void whenPostBooking_withValidData_thenReturns201Created() throws Exception {
        // Arrange (Preparar)
    BookingRequestDTO dto = new BookingRequestDTO();
    dto.setMunicipality("Lisboa");
    dto.setItemDescription("Um monitor");
    dto.setBookingDate(LocalDate.parse("2026-05-10"));
    dto.setTimeSlot(TimeSlot.MORNING);

    BookingRequest savedBooking = new BookingRequest(
        dto.getItemDescription(), dto.getMunicipality(), "", dto.getBookingDate(), dto.getTimeSlot());
    savedBooking.setId(1L);
    savedBooking.setBookingToken("token-123");
    LocalDateTime createdAt = LocalDateTime.of(2026, 5, 1, 10, 0);
    savedBooking.setCreatedAt(createdAt);
    savedBooking.setLastUpdatedAt(createdAt);

    BookingResponseDTO responseDTO = new BookingResponseDTO(
        savedBooking.getId(),
        savedBooking.getBookingToken(),
        savedBooking.getItemDescription(),
        savedBooking.getMunicipality(),
        savedBooking.getFullAddress(),
        savedBooking.getBookingDate(),
        savedBooking.getTimeSlot(),
        savedBooking.getStatus(),
        createdAt,
        createdAt,
        List.of(new BookingHistoryEntryDTO(BookingStatus.RECEIVED, createdAt, "Pedido criado"))
    );

    when(bookingService.createBooking(any(BookingRequestDTO.class))).thenReturn(savedBooking);
    when(bookingService.toResponseDto(savedBooking)).thenReturn(responseDTO);

    mockMvc.perform(post("/api/bookings")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.municipality").value("Lisboa"))
        .andExpect(jsonPath("$.itemDescription").value("Um monitor"))
        .andExpect(jsonPath("$.history").isArray())
        .andExpect(jsonPath("$.history[0].status").value("RECEIVED"));
    }

    // Testar o POST quando o serviço lança uma excepção (dados inválidos).
    @Test
    void whenPostBooking_withInvalidData_thenReturns400BadRequest() throws Exception {
        // Arrange
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setMunicipality("Cidade da Fantasia"); // Município inválido
        dto.setBookingDate(LocalDate.parse("2026-05-10"));

        // Configuramos o mock do serviço para lançar a excepção que o controller espera.
        when(bookingService.createBooking(any(BookingRequestDTO.class)))
            .thenThrow(new IllegalArgumentException("Municipality not available"));

        // Act & Assert
        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest()); // Esperamos um status HTTP 400 Bad Request.
    }

    @Test
    void whenPatchStatus_withValidPayload_thenReturnsUpdatedBooking() throws Exception {
    BookingRequest booking = new BookingRequest(
        "Máquina de lavar",
        "Lisboa",
        "Rua A",
        LocalDate.now().plusDays(2),
        TimeSlot.AFTERNOON
    );
    booking.setId(1L);
    booking.setStatus(BookingStatus.COMPLETED);

    when(bookingService.updateBookingStatus(eq(1L), eq(BookingStatus.COMPLETED))).thenReturn(booking);
    when(bookingService.toResponseDto(booking)).thenReturn(
        new BookingResponseDTO(
            1L,
            booking.getBookingToken(),
            booking.getItemDescription(),
            booking.getMunicipality(),
            booking.getFullAddress(),
            booking.getBookingDate(),
            booking.getTimeSlot(),
            BookingStatus.COMPLETED,
            booking.getCreatedAt(),
            booking.getLastUpdatedAt(),
            List.of(new BookingHistoryEntryDTO(BookingStatus.RECEIVED, booking.getCreatedAt(), "Pedido criado"),
                new BookingHistoryEntryDTO(BookingStatus.COMPLETED, booking.getLastUpdatedAt(), "Estado atualizado"))
        )
    );

    mockMvc.perform(patch("/api/bookings/staff/1/status")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"status\":\"COMPLETED\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void whenPatchStatus_withUnknownBooking_thenReturns404() throws Exception {
        when(bookingService.updateBookingStatus(eq(99L), eq(BookingStatus.CANCELLED)))
            .thenThrow(new EntityNotFoundException("Booking not found"));

        mockMvc.perform(patch("/api/bookings/staff/99/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"CANCELLED\"}"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Booking not found"));
    }

    @Test
    void whenPatchStatus_withInvalidPayload_thenReturns400() throws Exception {
        when(bookingService.updateBookingStatus(eq(1L), any()))
            .thenThrow(new IllegalArgumentException("Estado inválido"));

        mockMvc.perform(patch("/api/bookings/staff/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"INVALIDO\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Estado inválido"));
    }

    @Test
    void whenCancelBooking_withValidToken_thenReturnsUpdatedBooking() throws Exception {
        BookingRequest booking = new BookingRequest(
                "Sofá",
                "Lisboa",
                "Rua B",
                LocalDate.now().plusDays(3),
                TimeSlot.MORNING
        );
        booking.setBookingToken("token-cancel");
        booking.setId(10L);
        booking.setStatus(BookingStatus.CANCELLED);
        LocalDateTime now = LocalDateTime.now();
        booking.setLastUpdatedAt(now);

        BookingResponseDTO dto = new BookingResponseDTO(
                booking.getId(),
                booking.getBookingToken(),
                booking.getItemDescription(),
                booking.getMunicipality(),
                booking.getFullAddress(),
                booking.getBookingDate(),
                booking.getTimeSlot(),
                BookingStatus.CANCELLED,
                booking.getCreatedAt(),
                booking.getLastUpdatedAt(),
                List.of(new BookingHistoryEntryDTO(BookingStatus.RECEIVED, booking.getCreatedAt(), "Pedido criado"),
                        new BookingHistoryEntryDTO(BookingStatus.CANCELLED, now, "Pedido cancelado"))
        );

        when(bookingService.cancelBookingByToken("token-cancel")).thenReturn(booking);
        when(bookingService.toResponseDto(booking)).thenReturn(dto);

        mockMvc.perform(delete("/api/bookings/token/token-cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void whenCancelBooking_withUnknownToken_thenReturns404() throws Exception {
        when(bookingService.cancelBookingByToken("unknown"))
                .thenThrow(new EntityNotFoundException("Booking with token unknown not found"));

        mockMvc.perform(delete("/api/bookings/token/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Booking with token unknown not found"));
    }

    @Test
    void whenCancelBooking_completedBooking_thenReturns409() throws Exception {
        when(bookingService.cancelBookingByToken("done"))
                .thenThrow(new IllegalStateException("Não é possível cancelar uma recolha já concluída."));

        mockMvc.perform(delete("/api/bookings/token/done"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Não é possível cancelar uma recolha já concluída."));
    }
}