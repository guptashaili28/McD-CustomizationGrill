
package utilities;

import java.time.Duration;
import java.util.Locale;
import java.util.Map;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER =
            new ThreadLocal<>();

    private static final Map<String, Object> CHROMIUM_PREFS =
            Map.of(
                "profile.default_content_setting_values.geolocation", 2
            );

    private DriverFactory() {
    }

    public static WebDriver initDriver() {

        // Clean up an existing session for this thread, if any.
        quitDriver();

        String browser = ConfigReader.getProperty("browser");

        if (browser == null || browser.isBlank()) {
            browser = "firefox";
        }

        browser = browser.trim().toLowerCase(Locale.ROOT);

        boolean headless = ConfigReader.getBoolean("headless");
        int width = ConfigReader.getInt("windowWidth");
        int height = ConfigReader.getInt("windowHeight");

        WebDriver driver = null;

        try {
            switch (browser) {

                case "firefox": {
                    FirefoxOptions options = new FirefoxOptions();

                    if (headless) {
                        options.addArguments("-headless");
                    }

                    options.addPreference("geo.enabled", false);
                    driver = new FirefoxDriver(options);
                    break;
                }

                case "chrome": {
                    ChromeOptions options = new ChromeOptions();

                    if (headless) {
                        options.addArguments("--headless=new");
                    }

                    options.addArguments("--disable-notifications");
                    options.setExperimentalOption("prefs", CHROMIUM_PREFS);

                    driver = new ChromeDriver(options);
                    break;
                }

                case "edge": {
                    EdgeOptions options = new EdgeOptions();

                    if (headless) {
                        options.addArguments("--headless=new");
                    }

                    options.addArguments("--disable-notifications");
                    options.setExperimentalOption("prefs", CHROMIUM_PREFS);

                    driver = new EdgeDriver(options);
                    break;
                }

                default:
                    throw new IllegalArgumentException(
                        "Unsupported browser: " + browser
                        + ". Use firefox, chrome, or edge."
                    );
            }

            // Register the driver before configuring it so cleanup
            // can find the session if configuration fails.
            DRIVER.set(driver);

            driver.manage().timeouts().pageLoadTimeout(
                Duration.ofSeconds(
                    ConfigReader.getInt("pageLoadTimeout")
                )
            );

            driver.manage().window().setSize(
                new Dimension(width, height)
            );

            System.out.println("Browser selected: " + browser);
            System.out.println("Browser session initialized.");

            return driver;

        } catch (RuntimeException e) {
            quitDriver();
            throw e;
        }
    }

    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();

        try {
            if (driver != null) {
                driver.quit();
                System.out.println("Browser closed successfully.");
            }
        } catch (org.openqa.selenium.WebDriverException e) {
            System.out.println(
                "Browser session was already closed: " + e.getMessage()
            );
        } finally {
            DRIVER.remove();
        }
    }
}