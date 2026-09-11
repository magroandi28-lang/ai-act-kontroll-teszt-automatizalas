package org.example;
import io.qameta.allure.Issue;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

public class RendszerImportTest {

    private WebDriver driver;
    private WebDriverWait varakozas;

    @BeforeEach
    void belepes() throws Exception {
        SupabaseAdmin.importTakaritas();      // korábbi IMPORT-TESZT-* rendszerek törlése
        driver = new ChromeDriver();
        new BelepesPom(driver).belepes();
        varakozas = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Test
    @DisplayName("IMPORT-REQ-001 · MT-IMPORT-LETOLTES – a hivatalos importsablon letölthető")
    void sablonLetoltheto() throws Exception {
        HttpRequest keres = HttpRequest.newBuilder()
                .uri(URI.create(Konfig.alapUrl() + "/energiaai-rendszerimport-sablon.xlsx"))
                .GET()
                .build();
        HttpResponse<byte[]> valasz = HttpClient.newHttpClient()
                .send(keres, HttpResponse.BodyHandlers.ofByteArray());

        Assertions.assertEquals(200, valasz.statusCode(),
                "A sablonnak letölthetőnek kell lennie (HTTP 200).");
        Assertions.assertTrue(valasz.body().length > 0,
                "A letöltött sablon nem lehet üres.");
    }

    // Hibajegy: https://vargaandreajob.atlassian.net/browse/KAN-6
    @Issue("KAN-6")
    @Test
    @DisplayName("KAN-6 · MT-IMPORT · IMPORT-REQ-007 – egyedi nevű sablon importja bekerül a tárolt rendszerek közé")
    void kitoltottSablonImportalhato() throws Exception {
        String egyediNev = "IMPORT-TESZT-" + System.currentTimeMillis();
        Path csvUt = keszitTesztCsv(egyediNev);

        driver.get(Konfig.alapUrl() + "/rendszerek/importalas");
        driver.findElement(By.cssSelector("input[type='file']"))
                .sendKeys(csvUt.toAbsolutePath().toString());
        varakozas.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Fájl ellenőrzése')]"))).click();

        // Előnézet – ha van hibás sor, a pontos hibaüzenettel bukjunk el
        varakozas.until(ExpectedConditions.visibilityOfElementLocated(By.className("import-preview")));
        java.util.List<WebElement> hibasSorok =
                driver.findElements(By.cssSelector(".import-preview tr.is-error"));
        if (!hibasSorok.isEmpty()) {
            Assertions.fail("Az importfájl hibás sort tartalmaz: " + hibasSorok.get(0).getText());
        }

        // Megerősítés + tényleges importálás
        varakozas.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".import-confirmation input[type='checkbox']"))).click();
        varakozas.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'importálása')]"))).click();
        varakozas.until(ExpectedConditions.urlContains("importalva"));

        // A LÉNYEG: az egyedi nevű rendszer tényleg bekerült a tárolt MI-rendszerek közé
        Assertions.assertTrue(SupabaseAdmin.rendszerLetezik(egyediNev),
                "Az importált '" + egyediNev + "' rendszernek szerepelnie kell a tárolt rendszerek között.");
    }

    private Path keszitTesztCsv(String nev) throws Exception {
        String fejlec = "Rendszer neve,Rendszertípus kód,Iparág kód,Rendeltetés,"
                + "Eszköz funkciók kódjai,Szervezeti szerep,Életciklus,"
                + "EU-ban használják,MI-használat egyértelmű,Nincs tiltott gyakorlat,Szabályozott termékbe épül";
        String sor = nev + ",CUSTOMER_CHATBOT,energy,Automata teszt importalt rendszer,"
                + ",deployer,production,Igen,Igen,Igen,Nem";
        Path ut = Files.createTempFile("import-teszt-", ".csv");
        Files.write(ut, (fejlec + "\n" + sor + "\n").getBytes(StandardCharsets.UTF_8));
        return ut;
    }

    @AfterEach
    void bezaras() {
        if (driver != null) driver.quit();
    }
}