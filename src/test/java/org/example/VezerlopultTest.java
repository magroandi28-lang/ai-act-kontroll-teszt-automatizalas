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

public class VezerlopultTest {

    private WebDriver driver;

    @BeforeEach
    void belepesAVezerlopultra() {
        driver = new ChromeDriver();
        new BelepesPom(driver).belepes();
    }

    @Test
    @DisplayName("MT-DASH-001 · DASH-REQ-001 – a négy fő modul (kártya) látható")
    void aNegyKartyaLathato() {
        Assertions.assertTrue(driver.findElement(By.cssSelector("a[href='/rendszerek/uj']")).isDisplayed());
        Assertions.assertTrue(driver.findElement(By.cssSelector("a[href='/rendszerek']")).isDisplayed());
        Assertions.assertTrue(driver.findElement(By.cssSelector("a[href='/jogtar']")).isDisplayed());
        Assertions.assertTrue(driver.findElement(By.cssSelector("a[href='/szervezet']")).isDisplayed());
    }

    @Test
    @DisplayName("MT-DASH-002 · DASH-REQ-002 – az Új MI-rendszer kártya navigál")
    void ujRendszerKartyaNavigal() {
        driver.findElement(By.cssSelector("a[href='/rendszerek/uj']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("/rendszerek/uj"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/rendszerek/uj"));
    }

    @Test
    @DisplayName("MT-DASH-002 · DASH-REQ-002 – a Mentett rendszerek kártya navigál")
    void mentettRendszerekKartyaNavigal() {
        driver.findElement(By.cssSelector("a[href='/rendszerek']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("/rendszerek"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/rendszerek"));
    }

    @Test
    @DisplayName("MT-DASH-002 · DASH-REQ-002 – a Jogtár kártya navigál")
    void jogtarKartyaNavigal() {
        driver.findElement(By.cssSelector("a[href='/jogtar']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("/jogtar"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/jogtar"));
    }

    @Test
    @DisplayName("MT-DASH-002 · DASH-REQ-002 – a Tagok és szerepkörök kártya navigál")
    void tagokKartyaNavigal() {
        driver.findElement(By.cssSelector("a[href='/szervezet']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("/szervezet"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/szervezet"));
    }

    @AfterEach
    void bongeszoBezarasa() {
        driver.quit();
    }
}