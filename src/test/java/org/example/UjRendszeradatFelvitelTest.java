package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class UjRendszeradatFelvitelTest {

    private WebDriver driver;

    @BeforeEach
    void belepes() {
        driver = new ChromeDriver();
        new BelepesPom(driver).belepes();
    }

    @Test
    @DisplayName("MT-CREATE-006/007 · CREATE-REQ-006/007 – energetikai MI-rendszer létrehozása és mentése")
    void energetikaiRendszertLetrehozEsMent() {
        String egyediNev = "Teszt Energia Rendszer " + System.currentTimeMillis();

        new UjrahasznosithatoLepesek(driver).rendszertFelvisz(egyediNev);

        Assertions.assertTrue(driver.getCurrentUrl().contains("/rendszerek/"));
    }

    @AfterEach
    void bongeszoBezarasa() {
        driver.quit();
    }
}