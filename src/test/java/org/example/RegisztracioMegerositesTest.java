package org.example;
import io.qameta.allure.Issue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class RegisztracioMegerositesTest {

    private WebDriver driver;
    private WebDriverWait varakozas;

    @BeforeEach
    void bongeszoMegnyitasa() {
        driver = new ChromeDriver();
        varakozas = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Issue("KAN-5")
    @Test
    @DisplayName("MT-AUTH-004/005 – regisztráció + e-mailes megerősítés (Gmail API)[KAN-5]")
    void regisztracioMegerositoLinkkel() throws Exception {
        String egyediEmail = "magroandi28+teszt" + System.currentTimeMillis() + "@gmail.com";

        // 1. Regisztráció
        driver.get(Konfig.alapUrl() + "/regisztracio");
        varakozas.until(ExpectedConditions.elementToBeClickable(By.id("fullName"))).sendKeys("Teszt Elek");
        driver.findElement(By.id("organisationName")).sendKeys("Teszt Kft.");
        driver.findElement(By.id("registerEmail")).sendKeys(egyediEmail);
        driver.findElement(By.id("registerPassword")).sendKeys("test1234");
        driver.findElement(By.id("passwordAgain")).sendKeys("test1234");
        driver.findElement(By.name("privacy")).click();
        driver.findElement(By.className("primary-button")).click();

        // 2. Megvárjuk a "sikeres regisztráció" képernyőt
        varakozas.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[role='status']")));

        // 3. A Gmail API-val kiolvassuk a megerősítő linket (max 60 mp)
        String link = GmailKliens.megerositoLink(egyediEmail);

        // 4. Megnyitjuk a linket → a fiók megerősítődik
        driver.get(link);

        // 5. Ellenőrzés: bejutunk a vezérlőpultra (megerősített, belépett fiók)
        varakozas.until(ExpectedConditions.urlContains("/vezerlopult"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/vezerlopult"));
    }

    @AfterEach
    void bongeszoBezarasa() {
        driver.quit();
    }
}