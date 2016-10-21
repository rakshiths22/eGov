package pages;

import entities.LoginDetails;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LandingPage extends BasePage {
    private WebDriver driver;

    @FindBy(id = "j_username")
    private WebElement userNameTextBox;

    @FindBy(id = "j_password")
    private WebElement passwordTextBox;

    @FindBy(id = "signin-action")
    private WebElement signInButton;

    public LandingPage(WebDriver driver) {
        this.driver = driver;
    }

    public void loginAs(LoginDetails loginDetails) {
        userNameTextBox.sendKeys(loginDetails.getLoginId());
        passwordTextBox.sendKeys(loginDetails.getPassword());
        waitForElementToBeClickable(signInButton, driver);
        signInButton.click();
    }
}
