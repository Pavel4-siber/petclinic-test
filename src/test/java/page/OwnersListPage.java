package page;

import core.BaseSeleniumPage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class OwnersListPage extends BaseSeleniumPage {

    @FindBy(xpath = "//table[@id='owners']/tbody/tr")
    private List<WebElement> rowOwner;

    @FindBy(xpath = "//a[@title='Next']")
    private WebElement nextButton;

    public OwnersListPage() {
        PageFactory.initElements(driver, this);
    }

    public List<String> getAllOwners() {
        List<String> allOwners = new ArrayList<>();
        while (true) {
            for (WebElement row : rowOwner) {
                allOwners.add(row.getText());
            }
            if (hasNextPage()) {
                clickNext();
                PageFactory.initElements(driver, this);
            } else {
                break;
            }
        }
        return allOwners;
    }

    public OwnersListPage clickNext() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.elementToBeClickable(nextButton)).click();
        return this;
    }

    private boolean hasNextPage() {
        try {
            return nextButton.isDisplayed() && nextButton.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

}
