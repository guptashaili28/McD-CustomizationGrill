
package testModules.customization.tests;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CustomizationGrillTest extends base.BaseTest {

    private WebDriverWait getWait() {
        return new WebDriverWait(
            driver,
            Duration.ofSeconds(20)
        );
    }

    // Test 1: Verify homepage opens
    @Test
    public void verifyHomePageOpensTest() {

        logStep("Verifying McDelivery homepage");

        Assert.assertTrue(
            driver.getTitle().contains("McDelivery"),
            "McDelivery homepage did not open"
        );

        System.out.println("Homepage title: " + driver.getTitle());
        System.out.println("Homepage URL: " + driver.getCurrentUrl());
    }

    // Test 2: Check whether menu items are present
    @Test
    public void verifyMenuItemsDisplayedTest() {

        logStep("Checking menu items on homepage");

        System.out.println("Page URL: " + driver.getCurrentUrl());
        System.out.println("Page title: " + driver.getTitle());

        System.out.println("===== VISIBLE PAGE TEXT =====");

        System.out.println(
            driver.findElement(By.tagName("body")).getText()
        );

        int menuCardCount = driver.findElements(
            By.cssSelector(".menu-card")
        ).size();

        int appMenuCount = driver.findElements(
            By.cssSelector("app-menu-card-v4")
        ).size();

        int customizationCount = driver.findElements(
            By.cssSelector(".menu__lable-customised > div")
        ).size();

        System.out.println("menu-card count: " + menuCardCount);
        System.out.println("app-menu-card-v4 count: " + appMenuCount);
        System.out.println(
            "Customization control count: " + customizationCount
        );

        Assert.assertTrue(
            menuCardCount > 0 || appMenuCount > 0,
            "Menu cards are not present in the current page DOM. " +
            "Inspect the Firefox window and check location/restaurant selection."
        );
    }

    // Test 3: Verify customizable product is available
    @Test
    public void verifyCustomizableProductAvailableTest() {

        logStep("Checking customizable products");

        By customizableProducts = By.cssSelector(
            ".cart-details__menu-card:nth-child(1) " +
            ".menu__lable-customised > div, " +
            ".menu__lable-customised > div, " +
            ".menu__customised"
        );

        boolean found;

        try {
            found = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(
                    customizableProducts
                )
            ).isDisplayed();

        } catch (TimeoutException e) {
            found = false;
        }

        Assert.assertTrue(
            found,
            "No customizable product was found. " +
            "Confirm that menu items have loaded first."
        );
    }

    // Test 4: Verify customization panel opens
    @Test
    public void verifyCustomizationPanelOpensTest() {

        logStep("Opening a customizable product");

        By customizableButton = By.cssSelector(
            ".menu__lable-customised > div, .menu__customised"
        );

        WebElement button = getWait().until(
            ExpectedConditions.elementToBeClickable(
                customizableButton
            )
        );

        button.click();

        By customizationPanel = By.cssSelector(
            ".bottom-sheet__customisation, " +
            ".bottom-sheet__footer-container, " +
            ".bottom-sheet__footer"
        );

        boolean panelOpened;

        try {
            panelOpened = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(
                    customizationPanel
                )
            ).isDisplayed();

        } catch (TimeoutException e) {
            panelOpened = false;
        }

        Assert.assertTrue(
            panelOpened,
            "Customization panel did not open"
        );
    }

    // Test 5: Check remove control in cart
    @Test
    public void verifyRemoveCustomizationControlTest() {

        logStep("Checking remove control in cart");

        By cartButton = By.cssSelector(
            ".cart-status-bar__main--with-banner > " +
            ".cart-status-bar__cta"
        );

        WebElement cart = getWait().until(
            ExpectedConditions.elementToBeClickable(cartButton)
        );

        cart.click();

        By removeButton = By.cssSelector(
            "app-add-to-cart-btn:nth-child(2) " +
            ".add-to-cart__cta-btn--miuns > img"
        );

        boolean removeFound;

        try {
            removeFound = getWait().until(
                ExpectedConditions.visibilityOfElementLocated(
                    removeButton
                )
            ).isDisplayed();

        } catch (TimeoutException e) {
            removeFound = false;
        }

        Assert.assertTrue(
            removeFound,
            "Remove control was not found. A product may need to be added to the cart first."
        );
    }
}
