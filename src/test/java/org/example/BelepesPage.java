package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

public class BelepesPage {
    private final WebDriver driver;

    // Lokátorok: az oldal elemei, egy helyen.
    private final By emailMezo = By.id("email");
    private final By jelszoMezo = By.id("password");
    private final By adatkezelesiJelolo = By.name("privacy");
    private final By belepesGomb = By.className("bk-fo-gomb");
    private final By magyarNyelvGomb = By.cssSelector("button[aria-label='Magyar']");

    public BelepesPage(WebDriver driver) {
        this.driver = driver;
    }

    // Belépés a teszt-fiókkal; a végén átadja a vezérlőpult oldalát.
    public VezerlopultPage belepes() {
        WebDriverWait varakozas = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.get(Konfig.alapUrl());
        varakozas.until(ExpectedConditions.elementToBeClickable(emailMezo)).sendKeys(Konfig.email());
        driver.findElement(jelszoMezo).sendKeys(Konfig.jelszo());
        varakozas.until(ExpectedConditions.elementToBeClickable(adatkezelesiJelolo)).click();
        varakozas.until(ExpectedConditions.elementToBeClickable(belepesGomb)).click();
        varakozas.until(ExpectedConditions.urlContains("/vezerlopult"));
        return new VezerlopultPage(driver);
    }

    public boolean nyelvvaltoLathato() {
        return driver.findElement(magyarNyelvGomb).isDisplayed();
    }
}