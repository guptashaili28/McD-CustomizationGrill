
package pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CustomizationPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final String HOME_URL =
            "https://mcdelivery.co.in/";

    public CustomizationPage(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException(
                    "WebDriver is null. Check BaseTest setup."
            );
        }

        this.driver = driver;
        this.wait = new WebDriverWait(
                driver, Duration.ofSeconds(30)
        );

        this.wait.ignoring(
                StaleElementReferenceException.class
        );
    }

    // BaseTest already opens the website.
    // This method can also be used to navigate explicitly.
    public void openHome() {
        if (!driver.getCurrentUrl().startsWith(HOME_URL)) {
            driver.get(HOME_URL);
        }

        wait.until(d ->
                d.getTitle() != null
                        && !d.getTitle().isBlank()
        );

        System.out.println("Page title: " + driver.getTitle());
        System.out.println("Current URL: " + driver.getCurrentUrl());
    }

    public String getTitle() {
        return driver.getTitle();
    }

    // Wait for a menu-related element to appear.
    private void waitForMenu() {
        By menuLocator = By.cssSelector(
                ".menu-card, app-menu-card-v4, .menus__grid"
        );

        try {
            wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            menuLocator
                    )
            );
        } catch (TimeoutException e) {
            throw new TimeoutException(
                    "Menu did not become visible. "
                    + "The website may require location or restaurant "
                    + "selection before showing products. "
                    + "Current URL: " + driver.getCurrentUrl()
                    + ", title: " + driver.getTitle(),
                    e
            );
        }
    }

    // Click a specific CSS selector.
    public void clickCss(String selector) {
        WebElement element = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.cssSelector(selector)
                )
        );

        safeClick(element);
    }

    // Normal click with JavaScript fallback if intercepted.
    private void safeClick(WebElement element) {
        try {
            wait.until(
                    ExpectedConditions.visibilityOf(element)
            );

            try {
                element.click();
            } catch (ElementClickInterceptedException e) {
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].click();", element
                );
            }
        } catch (StaleElementReferenceException e) {
            throw new TimeoutException(
                    "Element changed before it could be clicked.", e
            );
        }
    }

    // Check whether any selector becomes visible.
    public boolean isAnyDisplayed(String... selectors) {
        long endTime = System.nanoTime()
                + Duration.ofSeconds(10).toNanos();

        while (System.nanoTime() < endTime) {
            for (String selector : selectors) {
                List<WebElement> elements =
                        driver.findElements(
                                By.cssSelector(selector)
                        );

                for (WebElement element : elements) {
                    try {
                        if (element.isDisplayed()) {
                            return true;
                        }
                    } catch (StaleElementReferenceException ignored) {
                        // Retry with fresh elements.
                    }
                }
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(
                        "Interrupted while checking visibility.", e
                );
            }
        }

        return false;
    }

    // Click the first visible and enabled matching element.
    public void clickFirstVisible(String... selectors) {
        long endTime = System.nanoTime()
                + Duration.ofSeconds(20).toNanos();

        while (System.nanoTime() < endTime) {
            for (String selector : selectors) {
                List<WebElement> elements =
                        driver.findElements(
                                By.cssSelector(selector)
                        );

                for (WebElement element : elements) {
                    try {
                        if (element.isDisplayed()
                                && element.isEnabled()) {

                            System.out.println(
                                    "Clicking selector: " + selector
                            );

                            safeClick(element);
                            return;
                        }
                    } catch (StaleElementReferenceException ignored) {
                        // Retry with fresh elements.
                    } catch (ElementClickInterceptedException ignored) {
                        // Try another matching element.
                    }
                }
            }

            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(
                        "Interrupted while waiting for an element.", e
                );
            }
        }

        throw new TimeoutException(
                "No visible clickable element found for selectors: "
                + String.join(", ", selectors)
        );
    }

    // Open the customization panel of a customizable product.
    public void openFirstCustomizableProduct() {
        waitForMenu();

        String[] productSelectors = {
                ".cart-details__menu-card:nth-child(1) "
                        + ".menu__lable-customised > div",
                ".menu__lable-customised > div",
                "div:nth-child(3) .menu__customised",
                "div:nth-child(1) > .menu-card .menu__customised",
                ".menu-card .menu__customised",
                "app-menu-card-v4 .menu__customised",
                ".menu__customised"
        };

        String[] panelSelectors = {
                ".bottom-sheet__customisation",
                ".bottom-sheet__footer-container",
                ".bottom-sheet__footer"
        };

        for (String selector : productSelectors) {
            List<WebElement> elements =
                    driver.findElements(
                            By.cssSelector(selector)
                    );

            for (WebElement element : elements) {
                try {
                    if (!element.isDisplayed()
                            || !element.isEnabled()) {
                        continue;
                    }

                    System.out.println(
                            "Trying product selector: " + selector
                    );

                    safeClick(element);

                    if (isAnyDisplayed(panelSelectors)) {
                        System.out.println(
                                "Customization panel opened."
                        );
                        return;
                    }

                } catch (StaleElementReferenceException ignored) {
                    // The menu changed; retry fresh elements.
                } catch (ElementClickInterceptedException ignored) {
                    // Try another matching element.
                }
            }
        }

        String pageText = "";

        try {
            pageText = driver.findElement(
                    By.tagName("body")
            ).getText();

            if (pageText.length() > 500) {
                pageText = pageText.substring(0, 500);
            }
        } catch (NoSuchElementException ignored) {
            // Keep URL and title in the error.
        }

        throw new TimeoutException(
                "Customization panel did not open."
                + "\nURL: " + driver.getCurrentUrl()
                + "\nTitle: " + driver.getTitle()
                + "\nVisible page text: " + pageText
        );
    }

    // Select the first visible customization option, if available.
    public void selectFirstCustomizationOptionIfAvailable() {
        String[] optionSelectors = {
                ".bottom-sheet__customisation .menu__radio-ic",
                ".bottom-sheet__customisation .menu__customised",
                ".menu__radio-ic"
        };

        for (String selector : optionSelectors) {
            List<WebElement> elements =
                    driver.findElements(
                            By.cssSelector(selector)
                    );

            for (WebElement element : elements) {
                try {
                    if (element.isDisplayed()
                            && element.isEnabled()) {

                        safeClick(element);

                        System.out.println(
                                "Customization option selected."
                        );
                        return;
                    }
                } catch (StaleElementReferenceException ignored) {
                    // Retry with fresh elements.
                } catch (ElementClickInterceptedException ignored) {
                    // Try another matching element.
                }
            }
        }

        System.out.println(
                "No selectable customization option was found."
        );
    }

    // Add the customized product to the cart.
    public void addCustomizedProductToCart() {
        clickFirstVisible(
                ".bottom-sheet__footer-container .primary-w-200",
                ".bottom-sheet__footer-container .primary",
                ".bottom-sheet__footer .app-btn",
                ".primary-w-200"
        );
    }

    // Read visible text from matching elements.
    public String visibleTextFrom(String... selectors) {
        StringBuilder result = new StringBuilder();

        for (String selector : selectors) {
            List<WebElement> elements =
                    driver.findElements(
                            By.cssSelector(selector)
                    );

            for (WebElement element : elements) {
                try {
                    if (element.isDisplayed()) {
                        String text = element.getText();

                        if (text != null && !text.isBlank()) {
                            if (result.length() > 0) {
                                result.append(" ");
                            }

                            result.append(text.trim());
                        }
                    }
                } catch (StaleElementReferenceException ignored) {
                    // Ignore elements refreshed by the page.
                }
            }
        }

        return result.toString().trim();
    }

    // Count visible elements.
    public int countDisplayed(String... selectors) {
        int count = 0;

        for (String selector : selectors) {
            List<WebElement> elements =
                    driver.findElements(
                            By.cssSelector(selector)
                    );

            for (WebElement element : elements) {
                try {
                    if (element.isDisplayed()) {
                        count++;
                    }
                } catch (StaleElementReferenceException ignored) {
                    // Ignore refreshed elements.
                }
            }
        }

        return count;
    }

    // Open the cart from the cart status bar.
    public void openCart() {
        clickFirstVisible(
                ".cart-status-bar__main--with-banner > "
                        + ".cart-status-bar__cta",
                ".cart-status-bar__cta"
        );
    }
}
