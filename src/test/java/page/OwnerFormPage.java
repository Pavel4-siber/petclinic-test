package page;

import core.BaseSeleniumPage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class OwnerFormPage extends BaseSeleniumPage {

    public OwnerFormPage() {
        PageFactory.initElements(driver, this);
    }

    @FindBy(id = "firstName")
    private WebElement rowFirstname;

    @FindBy(id = "lastName")
    private WebElement rowLastName;

    @FindBy(id = "address")
    private WebElement rowAddress;

    @FindBy(id = "city")
    private WebElement rowCity;

    @FindBy(id = "telephone")
    private WebElement rowTelephone;

    @FindBy(xpath = "//button[text()='Add Owner']")
    private WebElement addOwnerButton;

    public OwnerInformationPage addOwner(String firstName, String lastName, String address,
                                         String city, String telephone) {
        rowFirstname.sendKeys(firstName);
        rowLastName.sendKeys(lastName);
        rowAddress.sendKeys(address);
        rowCity.sendKeys(city);
        rowTelephone.sendKeys(telephone);
        addOwnerButton.click();
        return new OwnerInformationPage();
    }

}
