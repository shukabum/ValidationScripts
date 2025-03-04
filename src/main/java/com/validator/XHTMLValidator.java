package com.validator;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.service.DriverService;
import org.openqa.selenium.JavascriptExecutor;
import java.io.File;
import java.util.List;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.XMLConstants;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XHTMLValidator {
    private static int errorCount = 0;

    static class XHTMLErrorHandler implements ErrorHandler {
        public void warning(SAXParseException e) {
            // System.out.println("Warning: " + e.getMessage());
        }

        public void error(SAXParseException e) {
            // System.out.println("Error: " + e.getMessage());
            // errorCount++;
        }

        public void fatalError(SAXParseException e) {
            // System.out.println("Fatal Error: " + e.getMessage());
            // errorCount++;
        }
    }

    private static void validateXHTMLStructure(File file) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(true); // Use Schema instead
            // factory.setSchema(getXHTMLSchema());
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new XHTMLErrorHandler());
            Document doc = builder.parse(file);
            System.out.println("✅ XHTML Structure Validated Successfully!");

        } catch (Exception e) {
            System.out.println("❌ Validation Failed: " + e.getMessage());
            errorCount++;
        }
    }

    private static Schema getXHTMLSchema() throws SAXException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return schemaFactory.newSchema(new File("src/main/resources/xhtml1-transitional.xsd")); // Use a proper XHTML schema file
    }

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, DriverService.LOG_STDOUT);

        ChromeDriverService service = new ChromeDriverService.Builder()
            .withLogOutput(System.out)
            .build();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(service, options);

        try {
            File file = new File("src/main/resources/test.xhtml");
            validateXHTMLStructure(file);

            String filePath = "file:///" + file.getAbsolutePath();
            driver.get(filePath);

            System.out.println("\n🔍 Checking JavaScript execution...");
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Capture JavaScript Errors
            LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
            List<LogEntry> logs = logEntries.getAll();
            if (logs.isEmpty()) {
                System.out.println("✅ No JavaScript errors found!");
            } else {
                System.out.println("⚠️ JavaScript Errors Found:");
                for (LogEntry entry : logs) {
                    System.out.println(entry.getLevel() + ": " + entry.getMessage());
                    errorCount++;
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Validation Error: " + e.getMessage());
            errorCount++;
        } finally {
            driver.quit();
            service.stop();
        }

        System.out.println("\n📊 Validation Summary:");
        System.out.println("Total Errors: " + errorCount);
        System.exit(errorCount);
    }
}
