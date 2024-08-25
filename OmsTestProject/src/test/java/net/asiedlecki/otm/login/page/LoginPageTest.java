package net.asiedlecki.otm.login.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class LoginPageTest {

    private WebDriver driver;

    @BeforeClass
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver();
    }

    @Test
    public void shouldRejectUnknownUser() {
        driver.get("https://otm.asiedlecki.net/panel");

        WebElement usernameField = driver.findElement(By.id("username"));
        usernameField.sendKeys("unknown-user");

        WebElement passwordField = driver.findElement(By.id("password"));
        passwordField.sendKeys("password");

        WebElement button = driver.findElement(By.className("button"));
        button.click();

        WebElement errors = driver.findElement(By.id("errors"));
        WebElement errorMessageField = errors.findElement(By.tagName("p"));

        Assert.assertEquals(errorMessageField.getText(), "Login OR/AND password IS/ARE incorrect!");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
