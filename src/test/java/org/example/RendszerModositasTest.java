package org.example;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class RendszerModositasTest {

    private WebDriver driver;
    private WebDriverWait varakozas;

    @BeforeEach
    void seedEsBelepes() throws Exception {
        SupabaseAdmin.seedTorles();
        SupabaseAdmin.rendszertBeszur("SEED-LAPOZAS-MOD-" + System.currentTimeMillis());
        driver = new ChromeDriver();
        new BelepesPom(driver).belepes();
        varakozas = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Test
    @DisplayName("MT-EDIT-001 · EDIT-REQ-001 – rendszer nevének módosítása és a változás valódi ellenőrzése")
    void rendszerNevModositas() {
        driver.get(Konfig.alapUrl() + "/rendszerek");
        varakozas.until(ExpectedConditions.elementToBeClickable(By.className("system-row-edit"))).click();

        String ujNev = "MODOSITOTT-" + System.currentTimeMillis();
        WebElement mezo = varakozas.until(ExpectedConditions.elementToBeClickable(By.id("edit-system-name")));
        mezo.clear();
        mezo.sendKeys(ujNev);

        varakozas.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Módosítás mentése')]"))).click();

        // VALÓDI ellenőrzés: az új név tényleg megjelenik a listában → a módosítás elmentődött
        varakozas.until(ExpectedConditions.textToBePresentInElementLocated(
                By.className("systems-list"), ujNev));
        Assertions.assertTrue(driver.getPageSource().contains(ujNev), "Az új névnek látszania kell a mentés után.");
    }

    @AfterEach
    void bezaras() { driver.quit(); }
}