package page;

import core.BaseSeleniumPage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class OupsPage extends BaseSeleniumPage {

    @FindBy(xpath = "//div[@class='container-fluid']//h2[text()='Something happened...']")
    private WebElement textRow;

    public OupsPage() {
        PageFactory.initElements(driver, this);
    }

    public String getTextFromPage() {
        return textRow.getText();
    }
}
