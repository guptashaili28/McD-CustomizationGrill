package utilities;

import java.time.Duration;
import java.util.function.Function;

import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

/**
 * Explicit / fluent waits. McDelivery is an Angular app that re-renders after
 * hydration, so stale and not-yet-present elements are retried instead of failing.
 */
public class WaitUtils {

    private WaitUtils() {
    }

    public static Wait<WebDriver> fluentWait(WebDriver driver, int seconds) {
        return new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(seconds))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(ElementClickInterceptedException.class);
    }

    public static Wait<WebDriver> defaultWait(WebDriver driver) {
        return fluentWait(driver, ConfigReader.getInt("explicitWait"));
    }

    public static <T> T until(WebDriver driver, Function<WebDriver, T> condition) {
        return defaultWait(driver).until(condition);
    }

    public static WebElement waitForVisible(WebDriver driver, WebElement element) {
        return defaultWait(driver).until(ExpectedConditions.visibilityOf(element));
    }

    /** Returns true if the element becomes visible within the given time, otherwise false. */
    public static boolean isVisibleWithin(WebDriver driver, WebElement element, int seconds) {
        try {
            fluentWait(driver, seconds).until(d -> element.isDisplayed());
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public static void waitForUrlContains(WebDriver driver, String fragment) {
        defaultWait(driver).until(ExpectedConditions.urlContains(fragment));
    }

    public static void waitForPageLoad(WebDriver driver) {
        defaultWait(driver).until(d -> "complete".equals(
                ((JavascriptExecutor) d).executeScript("return document.readyState")));
    }
}
