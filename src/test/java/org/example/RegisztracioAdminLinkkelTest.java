package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class RegisztracioAdminLinkkelTest {

    private WebDriver driver;
    private WebDriverWait varakozas;

    @BeforeEach
    void bongeszoMegnyitasa() {
        driver = new ChromeDriver();
        varakozas = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Test
    @DisplayName("MT-AUTH-006 – regisztráció + megerősítés admin linkkel (e-mail nélkül, stabil)")
    void regisztracioAdminLinkkel() throws Exception {
        String egyediEmail = "magroandi28+admin" + System.currentTimeMillis() + "@gmail.com";

        // 1. A Supabase Admin API létrehozza a usert és visszaadja a megerősítő linket (e-mail nélkül)
        String link = SupabaseAdmin.megerositoLink(egyediEmail, "test1234", "Teszt Elek", "Teszt Kft.");

        // 2. Megnyitjuk a linket – a fiók megerősítődik, mintha az e-mailből kattintottunk volna
        driver.get(link);

        // 3. Ellenőrzés: bejutunk a vezérlőpultra (megerősített fiók + létrejött szervezet)
        varakozas.until(ExpectedConditions.urlContains("/vezerlopult"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/vezerlopult"));
    }

    @AfterEach
    void bongeszoBezarasa() {
        driver.quit();
    }
}