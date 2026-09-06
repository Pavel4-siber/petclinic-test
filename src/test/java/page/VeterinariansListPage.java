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

public class VeterinariansListPage extends BaseSeleniumPage {

    @FindBy(xpath = "//table[@id='vets']/tbody/tr")
    private List<WebElement> veterinariansRows;

    @FindBy(xpath = "//a[@title='Next']")
    private WebElement nextButton;

    public VeterinariansListPage() {
        PageFactory.initElements(driver, this);
    }

    public List<String> getAllVeterinarians() {
        List<String> allVeterinarians = new ArrayList<>();
        while (true) {
            for (WebElement row : veterinariansRows) {
                allVeterinarians.add(row.getText());
            }
            if (hasNextPage()) {
                clickNext();
                PageFactory.initElements(driver, this);
            } else {
                break;
            }
        }
        return allVeterinarians;
    }

    public VeterinariansListPage clickNext() {
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
