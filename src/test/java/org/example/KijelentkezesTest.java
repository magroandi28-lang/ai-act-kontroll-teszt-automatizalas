package org.example;

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

public class KijelentkezesTest {

    private WebDriver driver;
    private WebDriverWait varakozas;

    @BeforeEach
    void belepesAVezerlopultra() {
        driver = new ChromeDriver();
        new BelepesPom(driver).belepes();
        varakozas = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Test
    @DisplayName("SESSION-REQ-001 – kijelentkezés visszavisz a belépő oldalra")
    void kijelentkezesVisszavisz() {
        varakozas.until(ExpectedConditions.elementToBeClickable(By.className("dashboard-signout"))).click();

        // a belépő oldal jelenik meg újra (látszik az e-mail mező)
        varakozas.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        Assertions.assertFalse(driver.getCurrentUrl().contains("/vezerlopult"));
    }

    @Test
    @DisplayName("MT-SESSION-001 · SESSION-REQ-001 – kijelentkezés után a /vezerlopult nem nyílik meg")
    void vezerlopultNemErhetoKijelentkezesUtan() {
        varakozas.until(ExpectedConditions.elementToBeClickable(By.className("dashboard-signout"))).click();
        varakozas.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));

        // közvetlen URL-lel próbáljuk a védett oldalt
        driver.get(Konfig.alapUrl() + "/vezerlopult");

        // nem enged be: a belépő e-mail mező látszik, nem a vezérlőpult
        varakozas.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        Assertions.assertFalse(driver.getCurrentUrl().contains("/vezerlopult"));
    }

    @AfterEach
    void bongeszoBezarasa() {
        driver.quit();
    }
}
