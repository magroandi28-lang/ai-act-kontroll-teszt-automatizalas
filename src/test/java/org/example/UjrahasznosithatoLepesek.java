package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.List;

// Több oldalon átívelő, újrahasznosítható folyamatok (pl. rendszer felvitele a varázslón).
public class UjrahasznosithatoLepesek {

    private final WebDriver driver;
    private final WebDriverWait varakozas;

    public UjrahasznosithatoLepesek(WebDriver driver) {
        this.driver = driver;
        this.varakozas = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // Végigviszi a varázslót és elment egy energetikai MI-rendszert a megadott névvel.
    // Feltétel: a felhasználó már be van jelentkezve.
    public void rendszertFelvisz(String nev) {
        driver.get(Konfig.alapUrl() + "/rendszerek/uj");

        // 1. Azonosítás
        varakozas.until(ExpectedConditions.elementToBeClickable(By.id("rendszer-nev"))).sendKeys(nev);
        kattintTovabb();

        // 2. Iparág: Energetika
        varakozas.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class,'felvitel-opcio')][contains(.,'Energ')]"))).click();
        kattintTovabb();

        // 3. Szerepkör: Használjuk
        varakozas.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class,'felvitel-opcio')][contains(.,'Használjuk')]"))).click();
        kattintTovabb();

        varju(1500); // a katalógus (RPC) betöltése

        // 4. Végigmegyünk a szakaszokon, amíg meg nem jelenik a "Rendszer mentése"
        for (int i = 0; i < 60; i++) {
            if (elemLatszik(By.xpath("//button[contains(@class,'felvitel-tovabb')][contains(.,'Rendszer mentése')]"))) {
                break;
            }
            List<WebElement> nemGombok = driver.findElements(By.className("felvitel-nem"));
            if (!nemGombok.isEmpty() && nemGombok.get(0).isDisplayed()) {
                nemGombok.get(0).click();
                varju(400);
                continue;
            }
            List<WebElement> chipek = driver.findElements(By.className("felvitel-chip"));
            if (!chipek.isEmpty() && chipek.get(0).isDisplayed()) {
                chipek.get(0).click();
                varju(300);
                driver.findElement(By.className("felvitel-tovabb")).click();
                varju(400);
                continue;
            }
            List<WebElement> tovabbGombok = driver.findElements(By.className("felvitel-tovabb"));
            if (!tovabbGombok.isEmpty() && tovabbGombok.get(0).isDisplayed()) {
                tovabbGombok.get(0).click();
                varju(400);
            }
        }

        // 5. Mentés → átirányít a rendszer adatlapjára
        varakozas.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@class,'felvitel-tovabb')][contains(.,'Rendszer mentése')]"))).click();

        varakozas.until(ExpectedConditions.not(ExpectedConditions.urlContains("/rendszerek/uj")));
    }

    private void kattintTovabb() {
        varakozas.until(ExpectedConditions.elementToBeClickable(By.className("felvitel-tovabb"))).click();
    }

    private boolean elemLatszik(By by) {
        List<WebElement> elemek = driver.findElements(by);
        return !elemek.isEmpty() && elemek.get(0).isDisplayed();
    }

    private void varju(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}