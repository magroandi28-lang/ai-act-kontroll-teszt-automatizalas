package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
public class BelepesOldal {
    private final WebDriver driver;
    public BelepesOldal(WebDriver driver) {
        this.driver = driver;
    }
    public void beirEmail(String szoveg) {
        driver.findElement(By.id("email")).sendKeys(szoveg);
    }
    public boolean nyelvvaltoLathato() {
        return driver.findElement(By.cssSelector("button[aria-label='Magyar']")).isDisplayed();
    }
}


