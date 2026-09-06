package page;

import core.BaseSeleniumPage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class FindOwnerPage extends BaseSeleniumPage {

    @FindBy(name = "lastName")
    private WebElement inputField;

    @FindBy(xpath = "//button[text()='Find Owner']")
    private WebElement findOwnerButton;

    @FindBy(xpath = "//a[text()='Add Owner']")
    private WebElement addOwnerButton;

    @FindBy(xpath = "//p[text()='has not been found']")
    private WebElement rowError;

    public FindOwnerPage() {
        PageFactory.initElements(driver, this);
    }

    public OwnerFormPage addOwner() {
        addOwnerButton.click();
        return new OwnerFormPage();
    }

    public OwnersListPage findAllOwner() {
        findOwnerButton.click();
        return new OwnersListPage();
    }

    public OwnerInformationPage findOwnerWithTrueParam(String param) {
        inputField.sendKeys(param);
        findOwnerButton.click();
        return new OwnerInformationPage();
    }

    public String findOwnerWithFalseParam(String param) {
        inputField.sendKeys(param);
        findOwnerButton.click();
        return rowError.getText();
    }
}
