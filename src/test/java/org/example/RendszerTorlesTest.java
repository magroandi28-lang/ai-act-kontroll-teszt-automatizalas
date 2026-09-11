package org.example;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class RendszerTorlesTest {

    private WebDriver driver;
    private WebDriverWait varakozas;

    @BeforeEach
    void seedEsBelepes() throws Exception {
        SupabaseAdmin.seedTorles();
        SupabaseAdmin.rendszertBeszur("SEED-LAPOZAS-DEL-" + System.currentTimeMillis());
        driver = new ChromeDriver();
        new BelepesPom(driver).belepes();
        varakozas = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Test
    @DisplayName("MT-EDIT-004 · EDIT-REQ-004 – rendszer törlése és a tényleges eltűnés ellenőrzése")
    void rendszerTorles() {
        driver.get(Konfig.alapUrl() + "/rendszerek");
        varakozas.until(ExpectedConditions.visibilityOfElementLocated(By.className("systems-list")));

        // A törlendő (legfrissebb, saját) rendszer neve
        String torlendoNev = driver.findElement(By.cssSelector(".system-row h2")).getText();

        varakozas.until(ExpectedConditions.elementToBeClickable(By.className("system-row-edit"))).click();
        varakozas.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Rendszer törlése')]"))).click();
        varakozas.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Igen, törlöm')]"))).click();

        varakozas.until(ExpectedConditions.urlContains("torolve=1"));

        // VALÓDI ellenőrzés: a törölt rendszer neve MÁR NINCS az aktív listában
        varakozas.until(ExpectedConditions.visibilityOfElementLocated(By.className("systems-list")));
        boolean megMindigOtt = driver.findElements(By.cssSelector(".system-row h2"))
                .stream().anyMatch(e -> e.getText().equals(torlendoNev));
        Assertions.assertFalse(megMindigOtt, "A törölt rendszernek nem szabad a listában lennie.");
    }

    @AfterEach
    void bezaras() { driver.quit(); }
}