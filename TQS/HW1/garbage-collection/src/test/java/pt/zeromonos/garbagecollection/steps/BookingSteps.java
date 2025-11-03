package pt.zeromonos.garbagecollection.steps;

import com.microsoft.playwright.*;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

// Inicia a nossa aplicação Spring Boot numa porta aleatória para o teste.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookingSteps {

    // Injeta a porta aleatória em que o servidor está a correr.
    @LocalServerPort
    private int port;

    // Variáveis para gerir o Playwright
    private Playwright playwright;
    private Browser browser;
    private Page page;

    // Este método é executado ANTES de cada cenário.
    @Before
    public void setUp() {
        playwright = Playwright.create();
        // Usamos o Chromium, mas podia ser firefox ou webkit.
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true)); // false para ver o browser a abrir
        page = browser.newPage();
    }

    // Este método é executado DEPOIS de cada cenário.
    @After
    public void tearDown() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @Dado("que estou na página de agendamento de recolhas")
    public void que_estou_na_pagina_de_agendamento_de_recolhas() {
        // Navega para a página da nossa aplicação.
        page.navigate("http://localhost:" + port + "/");
    }

    @Quando("eu preencho o formulário com o município {string}, descrição {string} e uma data futura")
    public void eu_preencho_o_formulario_com_dados_validos(String municipality, String description) {
        // Espera que a lista de municípios seja carregada e seleciona o correto.
        page.selectOption("#municipality", municipality);

        // Preenche os outros campos.
        page.fill("#itemDescription", description);
        page.fill("#fullAddress", "Rua de Teste, 123");

        // Calcula uma data futura e formata-a como YYYY-MM-DD.
        String futureDate = LocalDate.now().plusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE);
        page.fill("#bookingDate", futureDate);
    }

    @Quando("eu submeto o formulário de agendamento")
    public void eu_submeto_o_formulario() {
        // Clica no botão de submissão.
        page.click("button[type='submit']");
    }

    @Entao("eu devo ver uma mensagem de sucesso com um código de agendamento")
    public void eu_devo_ver_uma_mensagem_de_sucesso() {
        // Usa o locator para encontrar a div de resultado.
        Locator resultDiv = page.locator("#result");
        
        // Verifica se a div contém o texto esperado.
        assertThat(resultDiv).containsText("Agendamento realizado com sucesso!");
        assertThat(resultDiv).containsText("Guarde o seu código de consulta:");
    }
}