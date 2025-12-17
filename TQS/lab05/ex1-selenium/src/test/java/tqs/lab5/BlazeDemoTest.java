package tqs.lab5;

import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Lab 5.2 - BlazeDemo Travel Agency Test
 * Tests booking a flight on the blazedemo.com dummy travel agency
 * 
 * This is the Java version of what would be recorded in Selenium IDE
 */
@ExtendWith(SeleniumJupiter.class)
class BlazeDemoTest {

    @Test
    @DisplayName("5.2 - Complete flight booking flow")
    void testBookFlight(ChromeDriver driver) {
        // Step 1: Navigate to homepage
        driver.get("https://blazedemo.com/");
        assertThat(driver.getTitle()).isEqualTo("BlazeDemo");

        // Step 2: Select departure city (Boston)
        WebElement fromSelect = driver.findElement(By.name("fromPort"));
        new Select(fromSelect).selectByVisibleText("Boston");

        // Step 3: Select destination city (Berlin)
        WebElement toSelect = driver.findElement(By.name("toPort"));
        new Select(toSelect).selectByVisibleText("Berlin");

        // Step 4: Click "Find Flights"
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        // Assert: We're on the flights page
        assertThat(driver.getTitle()).isEqualTo("BlazeDemo - reserve");
        assertThat(driver.getCurrentUrl()).contains("reserve");

        // Step 5: Choose the first flight
        driver.findElement(By.cssSelector("tr:nth-child(1) input[type='submit']")).click();

        // Assert: We're on the purchase page
        assertThat(driver.getTitle()).isEqualTo("BlazeDemo Purchase");

        // Step 6: Fill in passenger details
        driver.findElement(By.id("inputName")).clear();
        driver.findElement(By.id("inputName")).sendKeys("João Barreira");
        
        driver.findElement(By.id("address")).clear();
        driver.findElement(By.id("address")).sendKeys("123 Main Street");
        
        driver.findElement(By.id("city")).clear();
        driver.findElement(By.id("city")).sendKeys("Aveiro");
        
        driver.findElement(By.id("state")).clear();
        driver.findElement(By.id("state")).sendKeys("Aveiro");
        
        driver.findElement(By.id("zipCode")).clear();
        driver.findElement(By.id("zipCode")).sendKeys("3800-000");
        
        // Step 7: Select card type
        WebElement cardSelect = driver.findElement(By.id("cardType"));
        new Select(cardSelect).selectByVisibleText("Visa");
        
        driver.findElement(By.id("creditCardNumber")).clear();
        driver.findElement(By.id("creditCardNumber")).sendKeys("1234567890123456");
        
        driver.findElement(By.id("creditCardMonth")).clear();
        driver.findElement(By.id("creditCardMonth")).sendKeys("12");
        
        driver.findElement(By.id("creditCardYear")).clear();
        driver.findElement(By.id("creditCardYear")).sendKeys("2025");
        
        driver.findElement(By.id("nameOnCard")).clear();
        driver.findElement(By.id("nameOnCard")).sendKeys("João Barreira");

        // Step 8: Click "Purchase Flight"
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        // Step 9: 5.2b - Assert confirmation page title
        assertThat(driver.getTitle()).isEqualTo("BlazeDemo Confirmation");
        
        // Verify confirmation message is present
        WebElement heading = driver.findElement(By.tagName("h1"));
        assertThat(heading.getText()).contains("Thank you for your purchase");
    }

    @Test
    @DisplayName("5.2 - Simple flight search")
    void testFlightSearch(ChromeDriver driver) {
        driver.get("https://blazedemo.com/");

        // Select departure and destination
        new Select(driver.findElement(By.name("fromPort"))).selectByVisibleText("Paris");
        new Select(driver.findElement(By.name("toPort"))).selectByVisibleText("London");

        // Find flights
        driver.findElement(By.cssSelector("input[type='submit']")).click();

        // Verify flights are shown
        assertThat(driver.getCurrentUrl()).contains("reserve");
        assertThat(driver.findElements(By.cssSelector("table tbody tr"))).hasSizeGreaterThan(0);
    }
}
