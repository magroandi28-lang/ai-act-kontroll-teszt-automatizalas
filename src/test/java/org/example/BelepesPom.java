package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class BelepesPom {
    private final WebDriver driver;

    public BelepesPom(WebDriver driver) {
        this.driver = driver;
    }

    // Belépés a teszt-fiókkal, a vezérlőpultig.
    public void belepes() {
        WebDriverWait varakozas = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.get(Konfig.alapUrl());
        varakozas.until(ExpectedConditions.elementToBeClickable(By.id("email"))).sendKeys(Konfig.email());
        driver.findElement(By.id("password")).sendKeys(Konfig.jelszo());
        varakozas.until(ExpectedConditions.elementToBeClickable(By.name("privacy"))).click();
        varakozas.until(ExpectedConditions.elementToBeClickable(By.className("bk-fo-gomb"))).click();
        varakozas.until(ExpectedConditions.urlContains("/vezerlopult"));
    }

    public boolean nyelvvaltoLathato() {
        return driver.findElement(By.cssSelector("button[aria-label='Magyar']")).isDisplayed();
    }
}