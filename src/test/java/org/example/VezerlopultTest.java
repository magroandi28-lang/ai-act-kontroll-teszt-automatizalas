package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class VezerlopultTest {

    private WebDriver driver;
    private VezerlopultPage vezerlopult;

    @BeforeEach
    void belepesAVezerlopultra() {
        driver = new ChromeDriver();
        vezerlopult = new BelepesPage(driver).belepes();
    }

    @Test
    @DisplayName("MT-DASH-001 · DASH-REQ-001 – a négy fő modul (kártya) látható")
    void aNegyKartyaLathato() {
        Assertions.assertTrue(vezerlopult.mindenKartyaLathato());
    }

    @Test
    @DisplayName("MT-DASH-002 · DASH-REQ-002 – az Új MI-rendszer kártya navigál")
    void ujRendszerKartyaNavigal() {
        vezerlopult.ujRendszerMegnyitasa();
        Assertions.assertTrue(vezerlopult.aktualisUrl().contains("/rendszerek/uj"));
    }

    @Test
    @DisplayName("MT-DASH-002 · DASH-REQ-002 – a Mentett rendszerek kártya navigál")
    void mentettRendszerekKartyaNavigal() {
        vezerlopult.mentettRendszerekMegnyitasa();
        Assertions.assertTrue(vezerlopult.aktualisUrl().contains("/rendszerek"));
    }

    @Test
    @DisplayName("MT-DASH-002 · DASH-REQ-002 – a Jogtár kártya navigál")
    void jogtarKartyaNavigal() {
        vezerlopult.jogtarMegnyitasa();
        Assertions.assertTrue(vezerlopult.aktualisUrl().contains("/jogtar"));
    }

    @Test
    @DisplayName("MT-DASH-002 · DASH-REQ-002 – a Tagok és szerepkörök kártya navigál")
    void tagokKartyaNavigal() {
        vezerlopult.tagokMegnyitasa();
        Assertions.assertTrue(vezerlopult.aktualisUrl().contains("/szervezet"));
    }

    @AfterEach
    void bongeszoBezarasa() {
        driver.quit();
    }
}