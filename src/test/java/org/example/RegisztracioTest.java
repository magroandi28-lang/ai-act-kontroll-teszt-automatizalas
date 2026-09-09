package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
public class RegisztracioTest {

    private WebDriver driver;

    @BeforeEach
    void oldalMegnyitasa() {
        driver = new ChromeDriver();
        driver.get("https://energia-ai-kontroll.vercel.app/regisztracio");
    }

    @Test
    void teljesNevMezoLathato() {
        WebElement teljesNevMezo = driver.findElement(By.id("fullName"));
        Assertions.assertTrue(teljesNevMezo.isDisplayed());
    }
    @Test
    void szervezetNeveMezoLathato() {
        WebElement szervezetMezo = driver.findElement(By.id("organisationName"));
        Assertions.assertTrue(szervezetMezo.isDisplayed());
    }
    @Test
    void emailMezoLathato() {
        WebElement emailMezo = driver.findElement(By.id("registerEmail"));
        Assertions.assertTrue(emailMezo.isDisplayed());
    }
    @Test
    void jelszoMezoLathato() {
        WebElement jelszoMezo = driver.findElement(By.id("registerPassword"));
        Assertions.assertTrue(jelszoMezo.isDisplayed());
    }
    @Test
    void jelszoUjraMezoLathato() {
        WebElement jelszoUjraMezo = driver.findElement(By.id("passwordAgain"));
        Assertions.assertTrue(jelszoUjraMezo.isDisplayed());
    }
    @Test
    void adatkezelesLinkLathatoEsHelyesreMutat() {
        WebElement adatkezelesLink = driver.findElement(By.linkText("adatkezelési nyilatkozatot"));
        Assertions.assertTrue(adatkezelesLink.isDisplayed());
        Assertions.assertEquals("/adatkezeles", adatkezelesLink.getDomAttribute("href"));
    }
    @Test
    void fiokLetrehozasaGombLathato() {
        WebElement gomb = driver.findElement(By.className("primary-button"));
        Assertions.assertTrue(gomb.isDisplayed());
    }
    @Test
    void ketKulonbozoJelszoHibatAd() {
        driver.findElement(By.id("fullName")).sendKeys("Teszt Elek");
        driver.findElement(By.id("organisationName")).sendKeys("Teszt Kft");
        driver.findElement(By.id("registerEmail")).sendKeys("teszt@example.com");
        driver.findElement(By.id("registerPassword")).sendKeys("Titok123");
        driver.findElement(By.id("passwordAgain")).sendKeys("Masik456");

        driver.findElement(By.className("primary-button")).click();

        WebElement uzenet = driver.findElement(By.cssSelector("[role='alert']"));
        Assertions.assertEquals("A két jelszó nem egyezik.", uzenet.getText());
    }
    @Test
    void tulRovidJelszoHibatAd() {
        driver.findElement(By.id("fullName")).sendKeys("Teszt Elek");
        driver.findElement(By.id("organisationName")).sendKeys("Teszt Kft");
        driver.findElement(By.id("registerEmail")).sendKeys("teszt@example.com");
        driver.findElement(By.id("registerPassword")).sendKeys("rov");
        driver.findElement(By.id("passwordAgain")).sendKeys("rov");

        driver.findElement(By.className("primary-button")).click();

        WebElement uzenet = driver.findElement(By.cssSelector("[role='alert']"));
        Assertions.assertEquals("A jelszó legalább 8 karakter hosszú legyen.", uzenet.getText());
    }
    @Test
    void uresMezokHibatAdnak() {
        driver.findElement(By.className("primary-button")).click();

        WebElement uzenet = driver.findElement(By.cssSelector("[role='alert']"));
        Assertions.assertEquals("Minden mező kitöltése kötelező.", uzenet.getText());
    }
    @Test
    void adatkezelesNelkulHibatAd() {
        driver.findElement(By.id("fullName")).sendKeys("Teszt Elek");
        driver.findElement(By.id("organisationName")).sendKeys("Teszt Kft");
        driver.findElement(By.id("registerEmail")).sendKeys("teszt@example.com");
        driver.findElement(By.id("registerPassword")).sendKeys("Titok123");
        driver.findElement(By.id("passwordAgain")).sendKeys("Titok123");
        // az adatkezelés jelölőnégyzetet SZÁNDÉKOSAN nem pipáljuk be

        driver.findElement(By.className("primary-button")).click();

        WebElement uzenet = driver.findElement(By.cssSelector("[role='alert']"));
        Assertions.assertEquals("A regisztrációhoz fogadd el az adatkezelési nyilatkozatot.", uzenet.getText());
    }
    @Test
    @DisplayName("MT-AUTH-001 · AUTH-REQ-001 – sikeres regisztráció megerősítő levelet ígér")
    void sikeresRegisztracio() {
        String egyediEmail = "magroandi28+teszt" + System.currentTimeMillis() + "@gmail.com";

        driver.findElement(By.id("fullName")).sendKeys("Teszt Elek");
        driver.findElement(By.id("organisationName")).sendKeys("Teszt Kft.");
        driver.findElement(By.id("registerEmail")).sendKeys(egyediEmail);
        driver.findElement(By.id("registerPassword")).sendKeys("test1234");
        driver.findElement(By.id("passwordAgain")).sendKeys("test1234");
        driver.findElement(By.name("privacy")).click();

        driver.findElement(By.className("primary-button")).click();

        WebDriverWait varakozas = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement sikeresUzenet = varakozas.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[role='status']"))
        );
        Assertions.assertTrue(sikeresUzenet.getText().contains("A regisztráció elkészült"));
    }
    @AfterEach
    void bongeszoBezarasa() {
        driver.quit();
    }
}
