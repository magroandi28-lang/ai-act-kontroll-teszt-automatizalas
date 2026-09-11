package org.example;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class KepernyokepMentoExtension implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext context) {
        // Csak bukott teszt esetén készítünk képet (a @AfterEach előtt fut, tehát a böngésző még él)
        if (context.getExecutionException().isEmpty()) return;

        Object tesztPeldany = context.getTestInstance().orElse(null);
        if (tesztPeldany == null) return;

        WebDriver driver = megkeresDriver(tesztPeldany);
        if (driver == null) return;   // pl. API-teszt: nincs böngésző

        try {
            byte[] kep = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            String idobelyeg = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            String nev = context.getRequiredTestClass().getSimpleName() + "-"
                    + context.getRequiredTestMethod().getName() + "-" + idobelyeg + ".png";
            Path mappa = Paths.get("target", "kepernyokepek");
            Files.createDirectories(mappa);
            Files.write(mappa.resolve(nev), kep);
            System.out.println("Képernyőkép mentve: " + mappa.resolve(nev).toAbsolutePath());
        } catch (Exception e) {
            System.out.println("Képernyőkép mentése nem sikerült: " + e.getMessage());
        }
    }

    private WebDriver megkeresDriver(Object peldany) {
        Class<?> osztaly = peldany.getClass();
        while (osztaly != null) {
            for (Field mezo : osztaly.getDeclaredFields()) {
                if (WebDriver.class.isAssignableFrom(mezo.getType())) {
                    try {
                        mezo.setAccessible(true);
                        return (WebDriver) mezo.get(peldany);
                    } catch (Exception ignored) {}
                }
            }
            osztaly = osztaly.getSuperclass();
        }
        return null;
    }
}