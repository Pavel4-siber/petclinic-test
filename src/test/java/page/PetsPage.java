package page;

import core.BaseSeleniumPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

public class PetsPage extends BaseSeleniumPage {

    @FindBy(xpath = "//div[@class='col-sm-10']//following::span")
    private WebElement rowOwnerName;

    @FindBy(id = "name")
    private WebElement rowName;

    @FindBy(id = "birthDate")
    private WebElement rowBirthDate;

    @FindBy(id = "type")
    private WebElement typeSelect;

    @FindBy(xpath = "//select[@id='type']//option[@selected='selected']")
    private WebElement typeSelected;

    @FindBy(xpath = "//select[@name='type']/option[2]")
    private WebElement optionValue;

    @FindBy(xpath = "//button[@type='submit']")
    private WebElement updateButton;

    @FindBy(xpath = "//button[normalize-space()='Add Pet']")
    private WebElement addPetButton;

    @FindBy(xpath = "//span[text()='is already in use']")
    private WebElement rowCheckName;

    @FindBy(xpath = "//span[text()='invalid date']")
    private WebElement rowErrorDate;

    public PetsPage() {
        PageFactory.initElements(driver, this);
    }


    public String getName() {
        return rowName.getText();
    }

    public String getBirthDate() {
        return rowBirthDate.getText();
    }

    public String getType() {
        return typeSelected.getText();
    }

    public String getOwnerName() {
        return rowOwnerName.getText();
    }

    public OwnerInformationPage addPet(String namePet, String birthDate, String type) {
        rowName.sendKeys(namePet);
        rowBirthDate.sendKeys(birthDate);
        new Select(typeSelect).selectByValue(type);
        addPetButton.click();
        return new OwnerInformationPage();
    }

    public OwnerInformationPage updateNamePet(String name) {
        rowName.clear();
        rowName.sendKeys(name);
        updateButton.click();
        return new OwnerInformationPage();
    }

    public OwnerInformationPage updateBirthDate(String date) {
        rowBirthDate.clear();
        rowBirthDate.sendKeys(date);
        updateButton.click();
        return new OwnerInformationPage();
    }

    public OwnerInformationPage updateTypePet(String value) {
        By optionValue = By.xpath("//select[@name='type']/option[@value='" + value + "']");
        driver.findElement(optionValue).click();
        updateButton.click();
        return new OwnerInformationPage();
    }
    public OwnerInformationPage update_name_birth_type_pet(String name, String date, String value) {
        rowName.clear();
        rowName.sendKeys(name);
        rowBirthDate.clear();
        rowBirthDate.sendKeys(date);
        By optionValue = By.xpath("//select[@name='type']/option[@value='" + value + "']");
        driver.findElement(optionValue).click();
        updateButton.click();
        return new OwnerInformationPage();
    }

    public String updateBirthDateWithInvalidDate(String date) {
        rowBirthDate.clear();
        rowBirthDate.sendKeys(date);
        updateButton.click();
        if (rowErrorDate.isDisplayed() && rowErrorDate.isEnabled())
            return rowErrorDate.getText();
        else
            return "row error not found";
    }
}
