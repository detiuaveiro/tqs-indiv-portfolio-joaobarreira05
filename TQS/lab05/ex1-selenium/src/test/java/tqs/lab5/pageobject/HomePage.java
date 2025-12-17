package tqs.lab5.pageobject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

/**
 * Page Object for BlazeDemo Homepage
 * Represents the main page where users select departure and destination
 */
public class HomePage {

    private final WebDriver driver;

    @FindBy(name = "fromPort")
    private WebElement departureSelect;

    @FindBy(name = "toPort")
    private WebElement destinationSelect;

    @FindBy(css = "input[type='submit']")
    private WebElement findFlightsButton;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void open() {
        driver.get("https://blazedemo.com/");
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public void selectDeparture(String city) {
        new Select(departureSelect).selectByVisibleText(city);
    }

    public void selectDestination(String city) {
        new Select(destinationSelect).selectByVisibleText(city);
    }

    public FlightsPage searchFlights() {
        findFlightsButton.click();
        return new FlightsPage(driver);
    }

    // Fluent interface for chained calls
    public FlightsPage searchFlights(String from, String to) {
        selectDeparture(from);
        selectDestination(to);
        return searchFlights();
    }
}
