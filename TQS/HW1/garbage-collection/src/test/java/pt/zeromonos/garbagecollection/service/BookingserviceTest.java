package pt.zeromonos.garbagecollection.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.zeromonos.garbagecollection.domain.BookingRequest;
import pt.zeromonos.garbagecollection.domain.TimeSlot;
import pt.zeromonos.garbagecollection.dto.BookingRequestDTO;
import pt.zeromonos.garbagecollection.repository.BookingRequestRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Ativa a integração do Mockito com o JUnit 5
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    // Cria um mock (uma versão falsa) do repositório.
    @Mock
    private BookingRequestRepository bookingRepository;

    // Cria um mock do nosso serviço de API externa.
    @Mock
    private GeoApiService geoApiService;

    // Cria uma instância real do BookingService e injecta os mocks acima nele.
    @InjectMocks
    private BookingService bookingService;

    // -- Teste 1: Caminho Feliz (Happy Path) --
    // Testa a criação de um agendamento com dados válidos.
    @Test
    void whenCreateBooking_withValidData_thenBookingIsSaved() {
        // 1. Arrange (Preparar)
        // Criamos os dados de entrada para o teste.
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setMunicipality("Lisboa");
        dto.setItemDescription("Uma secretária");
        dto.setBookingDate(LocalDate.now().plusDays(5)); // Uma data futura
        dto.setTimeSlot(TimeSlot.MORNING);

        // Preparamos o que os nossos mocks devem fazer.
        // Dizemos ao mock do GeoApiService para retornar uma lista válida de municípios.
        when(geoApiService.getMunicipalities()).thenReturn(List.of("Lisboa", "Porto"));
        
        // Dizemos ao mock do repositório para retornar o próprio objeto que recebeu ao ser guardado.
        // any() significa que não nos importamos qual o objeto BookingRequest exato, qualquer um serve.
        when(bookingRepository.save(any(BookingRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. Act (Agir)
        // Executamos o método que queremos testar.
        BookingRequest result = bookingService.createBooking(dto);

        // 3. Assert (Verificar)
        // Verificamos se o resultado é o esperado.
        assertNotNull(result); // O resultado não deve ser nulo.
        assertEquals("Lisboa", result.getMunicipality()); // O município deve ser o que enviámos.
        assertNotNull(result.getBookingToken()); // O token deve ter sido gerado.

        // Verificamos se os nossos mocks foram chamados como esperado.
        // Garante que o método getMunicipalities() foi chamado exatamente 1 vez.
        verify(geoApiService, times(1)).getMunicipalities();
        // Garante que o método save() foi chamado exatamente 1 vez.
        verify(bookingRepository, times(1)).save(any(BookingRequest.class));
    }

    // -- Teste 2: Caminho Triste (Sad Path) --
    // Testa a criação de um agendamento com um município inválido.
    @Test
    void whenCreateBooking_withInvalidMunicipality_thenThrowException() {
        // 1. Arrange
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setMunicipality("Terra do Nunca"); // Município que não existe na lista.
        dto.setBookingDate(LocalDate.now().plusDays(5));
        
        // Configuramos o mock para retornar a lista de municípios válidos.
        when(geoApiService.getMunicipalities()).thenReturn(List.of("Lisboa", "Porto"));

        // 2. Act & 3. Assert
        // Verificamos se uma excepção do tipo IllegalArgumentException é lançada.
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(dto);
        });

        // Verificamos que o método save NUNCA foi chamado, porque a validação falhou antes.
        verify(bookingRepository, never()).save(any(BookingRequest.class));
    }

    // -- Teste 3: Outro Caminho Triste --
    // Testa a criação de um agendamento com uma data no passado.
    @Test
    void whenCreateBooking_withPastDate_thenThrowException() {
        // 1. Arrange
        BookingRequestDTO dto = new BookingRequestDTO();
        dto.setMunicipality("Lisboa");
        dto.setBookingDate(LocalDate.now().minusDays(1)); // Data no passado.

        // Configuramos o mock para o município ser válido, para passarmos essa validação.
        when(geoApiService.getMunicipalities()).thenReturn(List.of("Lisboa"));

        // 2. Act & 3. Assert
        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(dto);
        });

        // Mais uma vez, o save não deve ser chamado.
        verify(bookingRepository, never()).save(any(BookingRequest.class));
    }
}