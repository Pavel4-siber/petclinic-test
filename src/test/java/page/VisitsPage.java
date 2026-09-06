package page;

import core.BaseSeleniumPage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class VisitsPage extends BaseSeleniumPage {

    @FindBy(xpath = "//b[text()='Pet']/following-sibling::table[1]/tbody/tr")
    private List<WebElement> rowPet;

    @FindBy(xpath = "//b[text()='Pet']/following-sibling::table[2]/tbody/tr")
    private List<WebElement> rowPreviousVisits;

    @FindBy(id = "date")
    private WebElement rowDate;

    @FindBy(id = "description")
    private WebElement rowDescription;

    @FindBy(xpath = "//button[@type='submit']")
    private WebElement addVisitButton;

    @FindBy(className = "help-inline")
    private WebElement rowHasError;

    public VisitsPage() {
        PageFactory.initElements(driver, this);
    }

    public OwnerInformationPage addNewVisit(String date, String description) {
        rowDate.sendKeys(date);
        rowDescription.sendKeys(description);
        addVisitButton.click();
        return new OwnerInformationPage();
    }

    public String addNewVisitWithDefaultDescription(String date, String description) {
        rowDate.sendKeys(date);
        rowDescription.sendKeys(description);
        addVisitButton.click();
        if (rowHasError.isDisplayed() && rowHasError.isEnabled())
        return rowHasError.getText();
        else
            return "error row not found";
    }
}
