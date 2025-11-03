package pt.zeromonos.garbagecollection.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pt.zeromonos.garbagecollection.domain.BookingRequest;
import pt.zeromonos.garbagecollection.domain.TimeSlot;
import pt.zeromonos.garbagecollection.dto.BookingRequestDTO;
import pt.zeromonos.garbagecollection.service.BookingService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Carrega apenas a camada Web (o nosso controller) e não a aplicação inteira. É mais rápido.
@WebMvcTest(BookingController.class)
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

        // Preparamos a resposta que o nosso *mock* do BookingService vai dar.
        BookingRequest savedBooking = new BookingRequest(
            dto.getItemDescription(), dto.getMunicipality(), "", dto.getBookingDate(), dto.getTimeSlot());

        // Quando o método createBooking for chamado com qualquer DTO, retorna o objeto 'savedBooking'.
        when(bookingService.createBooking(any(BookingRequestDTO.class))).thenReturn(savedBooking);

        // Act & Assert (Agir e Verificar)
        mockMvc.perform(post("/api/bookings") // Simula um POST para o nosso endpoint.
                .contentType(MediaType.APPLICATION_JSON) // Diz que estamos a enviar JSON.
                .content(objectMapper.writeValueAsString(dto))) // Converte o nosso DTO para uma string JSON.
                .andExpect(status().isCreated()) // Esperamos um status HTTP 201 Created.
                .andExpect(jsonPath("$.municipality").value("Lisboa")) // Verifica se o JSON de resposta tem o campo "municipality" com o valor "Lisboa".
                .andExpect(jsonPath("$.itemDescription").value("Um monitor"))
                .andExpect(jsonPath("$.bookingToken").exists()); // Verifica se o token foi gerado na resposta.
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
}