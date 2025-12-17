package tqs.lab5.pageobject;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

/**
 * Page Object for BlazeDemo Purchase Page
 * Form for entering passenger and payment details
 */
public class PurchasePage {

    private final WebDriver driver;

    @FindBy(id = "inputName")
    private WebElement nameInput;

    @FindBy(id = "address")
    private WebElement addressInput;

    @FindBy(id = "city")
    private WebElement cityInput;

    @FindBy(id = "state")
    private WebElement stateInput;

    @FindBy(id = "zipCode")
    private WebElement zipCodeInput;

    @FindBy(id = "cardType")
    private WebElement cardTypeSelect;

    @FindBy(id = "creditCardNumber")
    private WebElement cardNumberInput;

    @FindBy(id = "creditCardMonth")
    private WebElement cardMonthInput;

    @FindBy(id = "creditCardYear")
    private WebElement cardYearInput;

    @FindBy(id = "nameOnCard")
    private WebElement nameOnCardInput;

    @FindBy(css = "input[type='submit']")
    private WebElement purchaseButton;

    public PurchasePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public PurchasePage fillName(String name) {
        nameInput.clear();
        nameInput.sendKeys(name);
        return this;
    }

    public PurchasePage fillAddress(String address) {
        addressInput.clear();
        addressInput.sendKeys(address);
        return this;
    }

    public PurchasePage fillCity(String city) {
        cityInput.clear();
        cityInput.sendKeys(city);
        return this;
    }

    public PurchasePage fillState(String state) {
        stateInput.clear();
        stateInput.sendKeys(state);
        return this;
    }

    public PurchasePage fillZipCode(String zipCode) {
        zipCodeInput.clear();
        zipCodeInput.sendKeys(zipCode);
        return this;
    }

    public PurchasePage selectCardType(String cardType) {
        new Select(cardTypeSelect).selectByVisibleText(cardType);
        return this;
    }

    public PurchasePage fillCardNumber(String cardNumber) {
        cardNumberInput.clear();
        cardNumberInput.sendKeys(cardNumber);
        return this;
    }

    public PurchasePage fillCardMonth(String month) {
        cardMonthInput.clear();
        cardMonthInput.sendKeys(month);
        return this;
    }

    public PurchasePage fillCardYear(String year) {
        cardYearInput.clear();
        cardYearInput.sendKeys(year);
        return this;
    }

    public PurchasePage fillNameOnCard(String name) {
        nameOnCardInput.clear();
        nameOnCardInput.sendKeys(name);
        return this;
    }

    public ConfirmationPage purchaseFlight() {
        purchaseButton.click();
        return new ConfirmationPage(driver);
    }

    // Convenience method to fill all details at once
    public ConfirmationPage completePurchase(String name, String address, String city, 
                                              String state, String zipCode, String cardType,
                                              String cardNumber, String month, String year) {
        return fillName(name)
                .fillAddress(address)
                .fillCity(city)
                .fillState(state)
                .fillZipCode(zipCode)
                .selectCardType(cardType)
                .fillCardNumber(cardNumber)
                .fillCardMonth(month)
                .fillCardYear(year)
                .fillNameOnCard(name)
                .purchaseFlight();
    }
}
