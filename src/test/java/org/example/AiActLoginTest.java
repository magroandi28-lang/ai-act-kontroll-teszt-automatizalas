package org.example;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
public class AiActLoginTest {
    private WebDriver driver;
    @BeforeEach
    void bongeszoEsOldalMegnyitasa() {
        driver = new ChromeDriver();
        driver.get("https://energia-ai-kontroll.vercel.app");
    }
    @Test
    void emailMezoLathatoAzOldalon(){
        WebElement emailMezo = driver.findElement(By.id("email"));
        boolean emailMezoLathato = emailMezo.isDisplayed();
        Assertions.assertTrue(emailMezoLathato);

    }
    @Test
    void emailMezobeLehetIrni() {
        WebElement emailMezo = driver.findElement(By.id("email"));
        emailMezo.sendKeys("teszt@example.com");
        Assertions.assertEquals("teszt@example.com", emailMezo.getAttribute("value"));
    }
    @Test
    void jelszoMezoLathatoAzOldalon() {
        WebElement jelszoMezo = driver.findElement(By.id("password"));
        boolean jelszoMezoLathato = jelszoMezo.isDisplayed();
        Assertions.assertTrue(jelszoMezoLathato);
    }
    @Test
    void jelszoMezobeLehetIrni() {
        WebElement jelszoMezo = driver.findElement(By.id("password"));
        jelszoMezo.sendKeys("Titok123");
        Assertions.assertEquals("Titok123", jelszoMezo.getAttribute("value"));
    }
    @Test
    void adatkezelesiJelolonegyzetLathato() {
        WebElement jelolonegyzet = driver.findElement(By.name("privacy"));
        Assertions.assertTrue(jelolonegyzet.isDisplayed());
    }
    @Test
    void adatkezelesiJelolonegyzetKijelolheto() {
        WebElement jelolonegyzet = driver.findElement(By.name("privacy"));
        jelolonegyzet.click();
        Assertions.assertTrue(jelolonegyzet.isSelected());
    }
    @Test
    void demoModGombLathato() {
        WebElement demoGomb = driver.findElement(
                By.cssSelector("button.bk-demo-gomb")
        );
        Assertions.assertTrue(demoGomb.isDisplayed());
    }
    @Test
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
    void regisztracioLinkAHelyesOldalraMutat() {
        WebElement regisztracioLink = driver.findElement(By.linkText("Regisztráció"));
        Assertions.assertTrue(regisztracioLink.isDisplayed());
        String href = regisztracioLink.getDomAttribute("href");
        Assertions.assertTrue(href.endsWith("/regisztracio"));
    }
    @Test
    void regisztracioLinkreKattintvaMegnyilikAzOldal() {
        driver.findElement(By.linkText("Regisztráció")).click();

        WebDriverWait varakozas = new WebDriverWait(driver, Duration.ofSeconds(10));
        varakozas.until(ExpectedConditions.urlContains("/regisztracio"));

        Assertions.assertTrue(driver.getCurrentUrl().contains("/regisztracio"));
    }
    @Test
    void elfelejtettJelszoLinkLathato() {
        WebElement elfelejtettJelszoLink = driver.findElement(By.linkText("Elfelejtett jelszó?"));
        Assertions.assertTrue(elfelejtettJelszoLink.isDisplayed());
    }
    @Test
    void elfelejtettJelszoLinkreKattintvaMegnyilikAzOldal() {
        driver.findElement(By.linkText("Elfelejtett jelszó?")).click();

        WebDriverWait varakozas = new WebDriverWait(driver, Duration.ofSeconds(10));
        varakozas.until(ExpectedConditions.urlContains("/jelszo"));

        Assertions.assertTrue(driver.getCurrentUrl().contains("/jelszo"));
    }
    @AfterEach
    void bongeszoBezarasa() {
        driver.quit();
    }
}
