package tqs.lab5.pageobject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Page Object for BlazeDemo Confirmation Page
 * Shows purchase confirmation after successful booking
 */
public class ConfirmationPage {

    private final WebDriver driver;

    @FindBy(tagName = "h1")
    private WebElement heading;

    @FindBy(css = "table tbody")
    private WebElement confirmationDetails;

    public ConfirmationPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public String getHeadingText() {
        return heading.getText();
    }

    public boolean isConfirmationDisplayed() {
        return heading.getText().contains("Thank you for your purchase");
    }

    public String getConfirmationDetails() {
        return confirmationDetails.getText();
    }
}
