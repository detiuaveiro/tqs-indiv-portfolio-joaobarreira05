package tqs.lab5.pageobject;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.chrome.ChromeDriver;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Lab 5.4 - Page Object Pattern Test
 * Uses Page Objects for cleaner, more maintainable tests
 * 
 * Benefits of Page Object Pattern:
 * - Separation of concerns (page logic vs test logic)
 * - Reusable page methods across multiple tests
 * - Easier maintenance when UI changes
 * - More readable test code
 */
@ExtendWith(SeleniumJupiter.class)
class BlazeDemoPageObjectTest {

    @Test
    @DisplayName("5.4 - Complete booking flow using Page Objects")
    void testCompleteBookingWithPageObjects(ChromeDriver driver) {
        // Arrange: Open homepage
        HomePage homePage = new HomePage(driver);
        homePage.open();
        
        assertThat(homePage.getTitle()).isEqualTo("BlazeDemo");

        // Act: Search for flights
        FlightsPage flightsPage = homePage.searchFlights("Boston", "Berlin");
        
        // Assert: Verify flights page
        assertThat(flightsPage.getTitle()).isEqualTo("BlazeDemo - reserve");
        assertThat(flightsPage.getFlightCount()).isGreaterThan(0);

        // Act: Select first flight
        PurchasePage purchasePage = flightsPage.selectFirstFlight();
        
        // Assert: Verify purchase page
        assertThat(purchasePage.getTitle()).isEqualTo("BlazeDemo Purchase");

        // Act: Complete purchase using fluent interface
        ConfirmationPage confirmationPage = purchasePage
                .fillName("João Barreira")
                .fillAddress("123 Main Street")
                .fillCity("Aveiro")
                .fillState("Aveiro")
                .fillZipCode("3800-000")
                .selectCardType("Visa")
                .fillCardNumber("1234567890123456")
                .fillCardMonth("12")
                .fillCardYear("2025")
                .fillNameOnCard("João Barreira")
                .purchaseFlight();

        // Assert: Verify confirmation
        assertThat(confirmationPage.getTitle()).isEqualTo("BlazeDemo Confirmation");
        assertThat(confirmationPage.isConfirmationDisplayed()).isTrue();
    }

    @Test
    @DisplayName("5.4 - Quick booking using convenience method")
    void testQuickBooking(ChromeDriver driver) {
        HomePage homePage = new HomePage(driver);
        homePage.open();

        // Use fluent API for entire flow
        ConfirmationPage confirmationPage = homePage
                .searchFlights("Paris", "London")
                .selectFirstFlight()
                .completePurchase(
                        "Test User",
                        "456 Test Ave",
                        "Porto",
                        "Porto",
                        "4000-000",
                        "American Express",
                        "9876543210987654",
                        "06",
                        "2026"
                );

        assertThat(confirmationPage.getTitle()).isEqualTo("BlazeDemo Confirmation");
    }

    @Test
    @DisplayName("5.4 - Verify flight options count")
    void testFlightOptionsAvailable(ChromeDriver driver) {
        HomePage homePage = new HomePage(driver);
        homePage.open();
        
        FlightsPage flightsPage = homePage.searchFlights("Philadelphia", "Dublin");
        
        assertThat(flightsPage.getFlightCount())
                .withFailMessage("Expected multiple flight options")
                .isGreaterThan(0);
        
        assertThat(flightsPage.getHeadingText()).contains("Flights from");
    }
}
