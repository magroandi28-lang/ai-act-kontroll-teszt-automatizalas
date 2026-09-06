package org.example;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
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
    @AfterEach
    void bongeszoBezarasa() {
        driver.quit();
    }
}
