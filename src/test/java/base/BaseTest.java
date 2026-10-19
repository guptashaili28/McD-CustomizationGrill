
package base;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import listeners.TestListener;
import utilities.ConfigReader;
import utilities.DriverFactory;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {

        String url = ConfigReader.getProperty("url");

        if (url == null || url.isBlank()) {
            throw new IllegalStateException(
                "URL is missing in config.properties"
            );
        }

        url = url.trim();

        System.out.println("====================================");
        System.out.println("Starting McDelivery test");
        System.out.println("Configured URL: " + url);

        driver = DriverFactory.initDriver();

        if (driver == null) {
            throw new IllegalStateException(
                "WebDriver initialization failed"
            );
        }

        try {
            System.out.println(
                "Browser session started: "
                + driver.getClass().getSimpleName()
            );

            driver.get(url);

            System.out.println("Page title: " + driver.getTitle());
            System.out.println("Current URL: " + driver.getCurrentUrl());

            System.out.println(
                "McDelivery page opened. Waiting for page rendering..."
            );

            // Allow the page time to render.
            Thread.sleep(5000);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(
                "Interrupted while waiting for the page", e
            );

        } catch (RuntimeException e) {
            DriverFactory.quitDriver();
            driver = null;
            throw e;
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {

        try {
            if (driver != null) {
                System.out.println(
                    "Test completed. Browser will remain open for 15 seconds."
                );

                // Temporary debugging pause: inspect the page in Firefox.
                Thread.sleep(15000);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

        } finally {
            DriverFactory.quitDriver();
            driver = null;

            System.out.println("Browser closed successfully.");
            System.out.println("====================================");
        }
    }

    protected void logStep(String message) {

        if (TestListener.getTest() != null) {
            TestListener.getTest().info(message);
        }

        System.out.println(
            "[" + Thread.currentThread().getName() + "] " + message
        );
    }
}
