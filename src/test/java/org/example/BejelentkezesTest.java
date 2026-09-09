package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

@DisplayName("Belépő oldal – Bejelentkezes és Adatkezelési nyilatkozat")
public class BejelentkezesTest {
    private WebDriver driver;
    private BelepesPom belepesOldal;

    @BeforeEach
    void bongeszoEsOldalMegnyitasa() {
        driver = new ChromeDriver();
        driver.get(Konfig.alapUrl());
        belepesOldal = new BelepesPom(driver);
    }

    @Test
    @DisplayName("MT-LOGIN-002 · LOGIN-REQ-002 – az e-mail mező látható")
    void emailMezoLathatoAzOldalon(){
        WebElement emailMezo = driver.findElement(By.id("email"));
        boolean emailMezoLathato = emailMezo.isDisplayed();
        Assertions.assertTrue(emailMezoLathato);
    }

    @Test
    @DisplayName("MT-LOGIN-002 · LOGIN-REQ-002 – az e-mail mezőbe lehet írni")
    void emailMezobeLehetIrni() {
        WebElement emailMezo = driver.findElement(By.id("email"));
        emailMezo.sendKeys("teszt@example.com");
        Assertions.assertEquals("teszt@example.com", emailMezo.getAttribute("value"));
    }

    @Test
    @DisplayName("MT-LOGIN-002 · LOGIN-REQ-002 – a jelszó mező látható")
    void jelszoMezoLathatoAzOldalon() {
        WebElement jelszoMezo = driver.findElement(By.id("password"));
        boolean jelszoMezoLathato = jelszoMezo.isDisplayed();
        Assertions.assertTrue(jelszoMezoLathato);
    }

    @Test
    @DisplayName("MT-LOGIN-002 · LOGIN-REQ-002 – a jelszó mezőbe lehet írni")
    void jelszoMezobeLehetIrni() {
        WebElement jelszoMezo = driver.findElement(By.id("password"));
        jelszoMezo.sendKeys("Titok123");
        Assertions.assertEquals("Titok123", jelszoMezo.getAttribute("value"));
    }

    @Test
    @DisplayName("MT-LOGIN-001 · LOGIN-REQ-001 – az adatkezelési jelölőnégyzet látható")
    void adatkezelesiJelolonegyzetLathato() {
        WebElement jelolonegyzet = driver.findElement(By.name("privacy"));
        Assertions.assertTrue(jelolonegyzet.isDisplayed());
    }

    @Test
    @DisplayName("MT-LOGIN-001 · LOGIN-REQ-001 – az adatkezelési jelölőnégyzet kijelölhető")
    void adatkezelesiJelolonegyzetKijelolheto() {
        WebElement jelolonegyzet = driver.findElement(By.name("privacy"));
        jelolonegyzet.click();
        Assertions.assertTrue(jelolonegyzet.isSelected());
    }

    @Test
    @DisplayName("MT-LOGIN-001 · LOGIN-REQ-001 – a demó gomb látható")
    void demoModGombLathato() {
        WebElement demoGomb = driver.findElement(
                By.cssSelector("button.bk-demo-gomb")
        );
        Assertions.assertTrue(demoGomb.isDisplayed());
    }

    @Test
    @DisplayName("MT-DEMO-001 · DEMO-REQ-001 – nyilatkozat nélkül a demó figyelmeztet")
    void demoGombKattinthato() {
        WebElement demoGomb = driver.findElement(By.className("bk-demo-gomb"));
        demoGomb.click();

        WebElement uzenet = driver.findElement(By.cssSelector("[role='alert']"));
        Assertions.assertEquals(
                "A demó indításához fogadd el az adatkezelési nyilatkozatot.",
                uzenet.getText()
        );
    }

    @Test
    @DisplayName("MT-LOGIN-001 · LOGIN-REQ-001 – a Regisztráció link a helyes oldalra mutat")
    void regisztracioLinkAHelyesOldalraMutat() {
        WebElement regisztracioLink = driver.findElement(By.linkText("Regisztráció"));
        Assertions.assertTrue(regisztracioLink.isDisplayed());
        String href = regisztracioLink.getDomAttribute("href");
        Assertions.assertTrue(href.endsWith("/regisztracio"));
    }

    @Test
    @DisplayName("MT-LOGIN-001 · LOGIN-REQ-001 – a Regisztráció linkre kattintva megnyílik az oldal")
    void regisztracioLinkreKattintvaMegnyilikAzOldal() {
        driver.findElement(By.linkText("Regisztráció")).click();

        WebDriverWait varakozas = new WebDriverWait(driver, Duration.ofSeconds(10));
        varakozas.until(ExpectedConditions.urlContains("/regisztracio"));

        Assertions.assertTrue(driver.getCurrentUrl().contains("/regisztracio"));
    }

    @Test
    @DisplayName("MT-LOGIN-001 · LOGIN-REQ-001 – az Elfelejtett jelszó link látható")
    void elfelejtettJelszoLinkLathato() {
        WebElement elfelejtettJelszoLink = driver.findElement(By.linkText("Elfelejtett jelszó?"));
        Assertions.assertTrue(elfelejtettJelszoLink.isDisplayed());
    }

    @Test
    @DisplayName("MT-LOGIN-001 · LOGIN-REQ-001 – az Elfelejtett jelszó linkre kattintva megnyílik az oldal")
    void elfelejtettJelszoLinkreKattintvaMegnyilikAzOldal() {
        driver.findElement(By.linkText("Elfelejtett jelszó?")).click();

        WebDriverWait varakozas = new WebDriverWait(driver, Duration.ofSeconds(10));
        varakozas.until(ExpectedConditions.urlContains("/jelszo"));

        Assertions.assertTrue(driver.getCurrentUrl().contains("/jelszo"));
    }

    @Test
    @DisplayName("MT-LOGIN-005 · LOGIN-REQ-005 – a jelszó-megjelenítés gomb látható")
    void jelszoMegjelenitesGombLathato() {
        WebElement szemGomb = driver.findElement(By.className("bk-szem"));
        Assertions.assertTrue(szemGomb.isDisplayed());
    }

    @Test
    @DisplayName("MT-LOGIN-005 · LOGIN-REQ-005 – a szem gomb megjeleníti a jelszót")
    void szemGombMegjelenitiAJelszot() {
        WebElement jelszoMezo = driver.findElement(By.id("password"));
        jelszoMezo.sendKeys("Titok123");
        Assertions.assertEquals("password", jelszoMezo.getDomAttribute("type"));
        driver.findElement(By.className("bk-szem")).click();
        Assertions.assertEquals("text", jelszoMezo.getDomAttribute("type"));
    }

    @Test
    @DisplayName("MT-LOGIN-001 · LOGIN-REQ-001 – a Belépés gomb látható")
    void belepesGombLathato() {
        WebElement belepesGomb = driver.findElement(By.className("bk-fo-gomb"));
        Assertions.assertTrue(belepesGomb.isDisplayed());
    }

    @Test
    @DisplayName("MT-LOGIN-011 · LOGIN-REQ-010 – sikeres belépés átvisz a vezérlőpultra")
    void sikeresBelepesAtviszAVezerlopultra() {
        driver.findElement(By.id("email")).sendKeys(Konfig.email());
        driver.findElement(By.id("password")).sendKeys(Konfig.jelszo());
        driver.findElement(By.name("privacy")).click();
        driver.findElement(By.className("bk-fo-gomb")).click();

        WebDriverWait varakozas = new WebDriverWait(driver, Duration.ofSeconds(15));
        varakozas.until(ExpectedConditions.urlContains("/vezerlopult"));

        Assertions.assertTrue(driver.getCurrentUrl().contains("/vezerlopult"));
    }

    @Test
    @DisplayName("MT-LOGIN-006 · LOGIN-REQ-006 – az adatkezelési hivatkozás látható")
    void adatkezelesLinkLathato() {
        WebElement adatkezelesLink = driver.findElement(By.linkText("adatkezelési nyilatkozatot"));
        Assertions.assertTrue(adatkezelesLink.isDisplayed());
    }

    @Test
    @DisplayName("MT-LOGIN-006 · LOGIN-REQ-006 · PRIVACY-REQ-001 – az adatkezelési oldal megnyílik")
    void adatkezelesLinkreKattintvaMegnyilikAzOldal() {
        driver.findElement(By.linkText("adatkezelési nyilatkozatot")).click();
        WebDriverWait varakozas = new WebDriverWait(driver, Duration.ofSeconds(10));
        varakozas.until(ExpectedConditions.urlContains("/adatkezeles"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/adatkezeles"));
    }

    @Test
    @DisplayName("MT-LOGIN-003 · LOGIN-REQ-003 – a nyelvváltó látszik")
    void nyelvvaltoLatszik() {
        Assertions.assertTrue(belepesOldal.nyelvvaltoLathato());
    }

    @AfterEach
    void bongeszoBezarasa() {
        driver.quit();
    }
}