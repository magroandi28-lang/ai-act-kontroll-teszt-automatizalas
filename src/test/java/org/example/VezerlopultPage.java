package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class VezerlopultPage {
    private final WebDriver driver;

    // Lokátorok: a négy kártya.
    private final By ujRendszerKartya = By.cssSelector("a[href='/rendszerek/uj']");
    private final By mentettRendszerekKartya = By.cssSelector("a[href='/rendszerek']");
    private final By jogtarKartya = By.cssSelector("a[href='/jogtar']");
    private final By tagokKartya = By.cssSelector("a[href='/szervezet']");

    // Konstruktor: megkapja és eltárolja a teszt böngészőjét.
    public VezerlopultPage(WebDriver driver) {
        this.driver = driver;
    }

    // Közös segédmetódus: rákattint a kártyára, és megvárja a várt URL-t.
    private void kartyaMegnyitasa(By kartya, String vartUrlResz) {
        driver.findElement(kartya).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains(vartUrlResz));
    }

    // Megnyitó metódusok: ezeket hívja a teszt.
    public void ujRendszerMegnyitasa() {
        kartyaMegnyitasa(ujRendszerKartya, "/rendszerek/uj");
    }

    public void mentettRendszerekMegnyitasa() {
        kartyaMegnyitasa(mentettRendszerekKartya, "/rendszerek");
    }

    public void jogtarMegnyitasa() {
        kartyaMegnyitasa(jogtarKartya, "/jogtar");
    }

    public void tagokMegnyitasa() {
        kartyaMegnyitasa(tagokKartya, "/szervezet");
    }

    // Lekérdezés: látható-e mind a négy kártya.
    public boolean mindenKartyaLathato() {
        return driver.findElement(ujRendszerKartya).isDisplayed()
                && driver.findElement(mentettRendszerekKartya).isDisplayed()
                && driver.findElement(jogtarKartya).isDisplayed()
                && driver.findElement(tagokKartya).isDisplayed();
    }

    // Az aktuális URL, hogy a teszt ellenőrizhesse.
    public String aktualisUrl() {
        return driver.getCurrentUrl();
    }
}