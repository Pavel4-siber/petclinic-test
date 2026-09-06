package page;

import core.BaseSeleniumPage;
import dto.OwnerDtoResponse;
import dto.PetDto;
import dto.VisitDto;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

public class OwnerInformationPage extends BaseSeleniumPage {

    @FindBy(xpath = "//div[@class='container-fluid']//a[text()='Edit Owner']")
    private WebElement editOwnerButton;

    @FindBy(xpath = "//div[@class='container-fluid']//a[text()='Add New Pet']")
    private WebElement addNewPetButton;

    @FindBy(xpath = "//h2[text()='Owner Information']/following-sibling::table[1]")
    private WebElement ownerTable;

    @FindBy(xpath = "//table[@class='table table-striped']//dt[text()='Birth Date']/following-sibling::dd")
    private WebElement rowBirthDate;

    private static final By PETS = By.cssSelector("dl.dl-horizontal");

    private static final By PET_NAME = By.xpath(".//dt[normalize-space()='Name']/following-sibling::dd[1]");

    private static final By PET_BIRTH_DATE = By.xpath(".//dt[normalize-space()='Birth Date']/following-sibling::dd[1]");

    private static final By PET_TYPE = By.xpath(".//dt[normalize-space()='Type']/following-sibling::dd[1]");

    private static final By VISITS_TABLE = By.xpath("./ancestor::tr[1]//table[contains(@class,'table-condensed')]");

    private static final By VISIT_ROWS = By.cssSelector("tbody tr");

    private static final By OWNER_NAME = By.xpath(".//th[normalize-space()='Name']/following-sibling::td[1]");

    private static final By OWNER_ADDRESS = By.xpath(".//th[normalize-space()='Address']/following-sibling::td[1]");

    private static final By OWNER_CITY = By.xpath(".//th[normalize-space()='City']/following-sibling::td[1]");

    private static final By OWNER_TELEPHONE = By.xpath(".//th[normalize-space()='Telephone']/following-sibling::td[1]");

    private static final By OWNER_ID = By.xpath("//a[normalize-space()='Edit Owner']");

    public OwnerInformationPage() {
        PageFactory.initElements(driver, this);
    }

    public OwnerDtoResponse getOwner() {
        String href = driver.findElement(OWNER_ID).getAttribute("href");
        String[] parts = URI.create(href).getPath().split("/");
        int id = Integer.parseInt(parts[2]);
        String name = driver.findElement(OWNER_NAME).getText();
        String address = driver.findElement(OWNER_ADDRESS).getText();
        String city = driver.findElement(OWNER_CITY).getText();
        String telephone = driver.findElement(OWNER_TELEPHONE).getText();
        return new OwnerDtoResponse(id, name, address, city, telephone);
    }

    public List<PetDto> getPets() {
        List<WebElement> petElements = driver.findElements(PETS);

        return petElements.stream()
                .map(this::createPet)
                .toList();
    }

    public PetsPage editPet(String petName) {
        WebElement petElement = findPetElement(petName);

        petElement.findElement(
                By.xpath("./ancestor::tr[1]//a[normalize-space()='Edit Pet']")
        ).click();

        return new PetsPage();
    }

    public VisitsPage addVisit(String petName) {
        WebElement petElement = findPetElement(petName);

        petElement.findElement(
                By.xpath("./ancestor::tr[1]//a[normalize-space()='Add Visit']")
        ).click();

        return new VisitsPage();
    }

    public PetsPage addPet() {
        addNewPetButton.click();
        return new PetsPage();
    }

    public String getBirthDate() {
        return rowBirthDate.getText();
    }

    private PetDto createPet(WebElement element) {

        String name = element.findElement(PET_NAME).getText();

        LocalDate birthDate = LocalDate.parse(
                element.findElement(PET_BIRTH_DATE).getText()
        );

        String type = element.findElement(PET_TYPE).getText();
        List<VisitDto> visits = getVisits(element);

        return new PetDto(name, birthDate, type, visits);
    }

    private List<VisitDto> getVisits(WebElement petElement) {
        WebElement table = petElement.findElement(VISITS_TABLE);
        return table.findElements(VISIT_ROWS).stream()
                .filter(this::isVisitRow)
                .map(this::createVisit)
                .toList();
    }

    private boolean isVisitRow(WebElement row) {

        List<WebElement> cells = row.findElements(By.tagName("td"));

        return cells.size() == 2
                && cells.get(0).getText().matches("\\d{4}-\\d{2}-\\d{2}");
    }

    private VisitDto createVisit(WebElement row) {

        List<WebElement> cells = row.findElements(By.tagName("td"));

        LocalDate date = LocalDate.parse(cells.get(0).getText());
        String description = cells.get(1).getText();

        return new VisitDto(date, description);
    }

    private WebElement findPetElement(String petName) {
        return driver.findElements(PETS).stream()
                .filter(element ->
                        element.findElement(PET_NAME)
                                .getText()
                                .equals(petName))
                .findFirst()
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Pet not found: " + petName
                        ));
    }
}
