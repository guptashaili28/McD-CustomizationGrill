package utilities;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CommonMethods {

    private CommonMethods() {
    }

    /** Clicks the element, retrying through Angular re-renders and overlays. */
    public static void click(WebDriver driver, WebElement element) {
        WaitUtils.until(driver, d -> {
            element.click();
            return true;
        });
    }

    /** Clears the field with Ctrl+A / Delete (works for Angular inputs) and types the value. */
    public static void enterText(WebElement element, String value) {
        element.click();
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        element.sendKeys(value);
    }

    public static String getText(WebElement element) {
        return element.getText().trim();
    }
}
