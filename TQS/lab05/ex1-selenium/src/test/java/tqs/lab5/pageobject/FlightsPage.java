package tqs.lab5.pageobject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

/**
 * Page Object for BlazeDemo Flights List Page
 * Shows available flights after search
 */
public class FlightsPage {

    private final WebDriver driver;

    @FindBy(css = "table tbody tr")
    private List<WebElement> flightRows;

    @FindBy(css = "tr:nth-child(1) input[type='submit']")
    private WebElement firstFlightButton;

    @FindBy(tagName = "h3")
    private WebElement heading;

    public FlightsPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public int getFlightCount() {
        return flightRows.size();
    }

    public String getHeadingText() {
        return heading.getText();
    }

    public PurchasePage selectFirstFlight() {
        firstFlightButton.click();
        return new PurchasePage(driver);
    }

    public PurchasePage selectFlight(int index) {
        flightRows.get(index).findElement(org.openqa.selenium.By.cssSelector("input[type='submit']")).click();
        return new PurchasePage(driver);
    }
}
