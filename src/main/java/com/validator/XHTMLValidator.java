package com.validator;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.JavascriptExecutor;
import java.io.File;

public class XHTMLValidator {
    public static void main(String[] args) {
        // Set the path to ChromeDriver
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");

        // Initialize Chrome WebDriver with headless option
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);

        int errorCount = 0; // Initialize error counter

        try {
            // Load local XHTML file
            File file = new File("src/main/resources/test.xhtml");
            String filePath = "file:///" + file.getAbsolutePath();

            driver.get(filePath);  // Open XHTML file in browser

            // Execute JavaScript inside XHTML
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String scriptResult = (String) js.executeScript("return document.title;");
            System.out.println("Page Title: " + scriptResult);

            // Example: Executing a JavaScript function inside XHTML
            js.executeScript("document.getElementById('testButton').click();");

            // Wait and close browser
            Thread.sleep(5000);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            errorCount++; 
        } finally {
            driver.quit();  
        }

        System.out.println("Total Errors: " + errorCount); 
        System.exit(errorCount);
    }
}
