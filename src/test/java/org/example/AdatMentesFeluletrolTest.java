package org.example;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class AdatMentesFeluletrolTest {

    private static final String RENDSZER_NEV = "Claude Fable 5.1 automata teszt asszisztens";

    private WebDriver driver;
    private WebDriverWait varakozas;

    @BeforeEach
    void belepes() {
        driver = new ChromeDriver();
        new BelepesPom(driver).belepes();
        varakozas = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Test
    @DisplayName("POLICY-REQ-005 · MT-MENTES – adatok lementése felületről (szabályzat PDF/nyomtatás)")
        void szabalyzatMenthetoFeluletrol() throws Exception {
        String id = SupabaseAdmin.rendszerId(RENDSZER_NEV);
        driver.get(Konfig.alapUrl() + "/rendszerek/" + id + "/szabalyzat");

        // A dokumentum megjelenik – van mit menteni
        WebElement dokumentum = varakozas.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("policy-document")));
        Assertions.assertTrue(dokumentum.isDisplayed(), "A szabályzat dokumentumnak látszania kell.");

        // A window.print()-et lecseréljük, hogy ne nyíljon meg az OS nyomtatási ablaka
        ((JavascriptExecutor) driver).executeScript(
                "window.__nyomtatasHivva = false; window.print = function(){ window.__nyomtatasHivva = true; };");

        // Mentés / nyomtatás gomb
        varakozas.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".policy-print-button"))).click();

        // Elsült-e a mentés/nyomtatás?
        Boolean hivva = (Boolean) ((JavascriptExecutor) driver)
                .executeScript("return window.__nyomtatasHivva;");
        Assertions.assertTrue(Boolean.TRUE.equals(hivva),
                "A 'Nyomtatás / Mentés PDF-ként' gombnak el kell indítania a mentést.");
    }

    @AfterEach
    void bezaras() {
        if (driver != null) driver.quit();
    }
}