package org.example;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class RendszerListaTest {

    private WebDriver driver;
    private WebDriverWait varakozas;

    @BeforeEach
    void seedEsBelepes() throws Exception {
        // Seedelés a háttéren (gyors, stabil): tiszta lap + 6 rendszer
        SupabaseAdmin.seedTorles();
        long batch = System.currentTimeMillis();
        for (int i = 1; i <= 6; i++) {
            SupabaseAdmin.rendszertBeszur("SEED-LAPOZAS-" + batch + "-" + i);
        }

        // Böngésző + belépés a listázás teszteléséhez
        driver = new ChromeDriver();
        new BelepesPom(driver).belepes();
        varakozas = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Test
    @DisplayName("MT-SYSTEM-003 · SYSTEM-REQ-003 – lapozás: oldalanként max 5, nem duplikál/hagy ki")
    void lapozasMax5PerOldal() {
        driver.get(Konfig.alapUrl() + "/rendszerek");

        // 1. oldal: pontosan 5 rendszer
        varakozas.until(ExpectedConditions.visibilityOfElementLocated(By.className("systems-list")));
        List<String> elsoOldal = rendszerNevek();
        Assertions.assertEquals(5, elsoOldal.size(), "Az első oldalon pontosan 5 rendszernek kell lennie.");

        // Lapozás a következő oldalra, és MEGVÁRJUK, amíg tényleg a 2. oldal töltődik be
        varakozas.until(ExpectedConditions.elementToBeClickable(By.linkText("Következő →"))).click();
        varakozas.until(ExpectedConditions.urlContains("oldal=2"));

        // 2. oldal: van rendszer, és EGYIK sem szerepelt az 1. oldalon (nincs duplikáció)
        varakozas.until(ExpectedConditions.visibilityOfElementLocated(By.className("systems-list")));
        List<String> masodikOldal = rendszerNevek();
        Assertions.assertFalse(masodikOldal.isEmpty(), "A második oldalon is kell lennie rendszernek.");
        Assertions.assertTrue(
                masodikOldal.stream().noneMatch(elsoOldal::contains),
                "A második oldal nem ismételheti az első oldal rendszereit.");
    }

    private List<String> rendszerNevek() {
        return driver.findElements(By.cssSelector(".system-row h2"))
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    @AfterEach
    void bongeszoBezarasa() {
        driver.quit();
    }
}