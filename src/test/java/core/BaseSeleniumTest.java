package core;

import database.DatabaseHelper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import steps.PetClinicSteps;
import tests.PetClinicTest;

import java.time.Duration;

abstract public class BaseSeleniumTest {

    protected static WebDriver driver;
    protected static DatabaseHelper databaseHelper;
    protected static PetClinicSteps petClinicSteps;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        BaseSeleniumPage.setDriver(driver);
        databaseHelper = new DatabaseHelper();
        petClinicSteps = new PetClinicSteps();
    }

    @AfterEach
    public void tearDowns() {
        if (driver != null) {
            driver.quit();
        }
    }

}
