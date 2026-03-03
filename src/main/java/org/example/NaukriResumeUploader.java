package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class NaukriResumeUploader {

    public static void main(String[] args) throws InterruptedException {

        // 🔐 Read from environment variables (GitHub Secrets)
        String email = System.getenv("NAUKRI_EMAIL");
        String password = System.getenv("NAUKRI_PASS");
        String resumePath = System.getenv("RESUME_PATH");

        if (email == null || password == null || resumePath == null) {
            System.err.println("Missing GitHub Secrets: NAUKRI_EMAIL, NAUKRI_PASS, RESUME_PATH");
            return;
        }

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");  // Required for GitHub Actions
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        try {

            // STEP 1: Open direct login page
            System.out.println("Opening login page...");
            driver.get("https://www.naukri.com/nlogin/login");
            Thread.sleep(4000);

            // STEP 2: Enter email
            System.out.println("Entering email...");
            WebElement emailField = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//input[contains(@placeholder,'Email')]")));
            emailField.clear();
            emailField.sendKeys(email);

            // STEP 3: Enter password
            System.out.println("Entering password...");
            WebElement passwordField = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//input[@type='password']")));
            passwordField.clear();
            passwordField.sendKeys(password);

            // STEP 4: Click Login
            System.out.println("Clicking login...");
            driver.findElement(By.xpath("//button[text()='Login']")).click();
            Thread.sleep(6000);

            // STEP 5: Navigate to stable profile page
            System.out.println("Navigating to profile...");
            driver.get("https://www.naukri.com/mnjuser/profile");
            Thread.sleep(5000);

            // STEP 6: Upload resume
            System.out.println("Uploading resume...");
            WebElement uploadInput = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//input[@type='file']")));
            uploadInput.sendKeys(resumePath);

            Thread.sleep(5000);

            System.out.println("🎉 RESUME UPLOAD COMPLETED SUCCESSFULLY!");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}