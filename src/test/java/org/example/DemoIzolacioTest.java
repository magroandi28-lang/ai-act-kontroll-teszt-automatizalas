package org.example;
import io.qameta.allure.Issue;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class DemoIzolacioTest {

    WebDriver driver;
    WebDriverWait wait;
    String demoUserId;

    @BeforeEach
    void oldalMegnyitasa() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.get("https://energia-ai-kontroll.vercel.app/");
    }

    @Issue("KAN-4")
    @Test
    @DisplayName("MT-DEMO-005 – demó belépés csak editor jogot ad, nem owner [KAN-4]")
    void demoBelepesEditorSzerep() throws Exception {
        driver.findElement(By.name("privacy")).click();
        driver.findElement(By.cssSelector(".bk-demo-gomb")).click();
        wait.until(ExpectedConditions.urlContains("/vezerlopult"));

        demoUserId = kiolvasDemoUserId();
        assertNotNull(demoUserId, "Nem sikerült kiolvasni a demó felhasználó azonosítóját a cookie-ból.");

        String szerep = SupabaseAdmin.tagsagSzerepkor(demoUserId);
        assertEquals("editor", szerep,
                "A demófelhasználó szerepköre 'editor' kell legyen, nem '" + szerep + "' (KAN-4).");
    }

    private String kiolvasDemoUserId() {
        String js =
                "var cookies = document.cookie ? document.cookie.split('; ') : [];" +
                        "var map = {};" +
                        "for (var i=0;i<cookies.length;i++){var c=cookies[i];var e=c.indexOf('=');if(e>-1)map[c.substring(0,e)]=c.substring(e+1);}" +
                        "var base=null;" +
                        "for (var k in map){if(/^sb-.*-auth-token(\\.\\d+)?$/.test(k)){base=k.replace(/\\.\\d+$/,'');break;}}" +
                        "if(!base) return null;" +
                        "var raw = map[base];" +
                        "if(raw===undefined){raw='';var n=0;while(map[base+'.'+n]!==undefined){raw+=map[base+'.'+n];n++;}}" +
                        "raw = decodeURIComponent(raw);" +
                        "if(raw.indexOf('base64-')===0){var s=raw.substring(7).replace(/-/g,'+').replace(/_/g,'/');while(s.length%4)s+='=';raw=atob(s);}" +
                        "var o = JSON.parse(raw);" +
                        "if(o && o.user && o.user.id) return o.user.id;" +
                        "if(o && o.access_token){var p=o.access_token.split('.')[1].replace(/-/g,'+').replace(/_/g,'/');while(p.length%4)p+='=';return JSON.parse(atob(p)).sub;}" +
                        "return null;";
        return (String) ((JavascriptExecutor) driver).executeScript(js);
    }

    @AfterEach
    void bezaras() {
        if (demoUserId != null) {
            try { SupabaseAdmin.torolFelhasznalo(demoUserId); } catch (Exception ignored) {}
        }
        if (driver != null) driver.quit();
    }
}