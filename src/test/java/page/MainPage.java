package page;

import core.BaseSeleniumPage;
import loadProperties.ConfigProvider;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class MainPage extends BaseSeleniumPage {

    @FindBy(xpath = "//div[@id='main-navbar']//a[@title='find owners']")
    private WebElement findOwners;

    @FindBy(xpath = "//div[@id='main-navbar']//a[@title='veterinarians']")
    private WebElement veterinarians;

    @FindBy(xpath = "//div[@id='main-navbar']//a[@title='trigger a RuntimeException to see how it is handled']")
    private WebElement error;

    public MainPage() {
        driver.get(ConfigProvider.URL);
        PageFactory.initElements(driver, this);
    }

    public FindOwnerPage clickFindOwnerPage() {
        findOwners.click();
        return new FindOwnerPage();
    }

    public VeterinariansListPage clickVeterinariansPage() {
        veterinarians.click();
        return new VeterinariansListPage();
    }

    public OupsPage clickErrorPage() {
        error.click();
        return new OupsPage();
    }
}
