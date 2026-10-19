# Customization test cases

| # | Test method | Purpose |
|---|---|---|
| 1 | `unavailableCustomizationTest` | Verify behavior for a category with unavailable customization. |
| 2 | `verifyCustomizableItemsAreDisplayedForASelectedProductTest` | Select a product and customization options. |
| 3 | `verifyProductPriceForMultipleCustomizableItemsTest` | Exercise multiple customization selections and price flow. |
| 4 | `verifySelectedCustomizationIsDisplayedInCartTest` | Check the recorded cart customization flow. |
| 5 | `removeCustomizationTest` | Remove customization and confirm the recorded dialog. |

Selectors are the CSS selectors copied from the supplied Selenium IDE recordings.

## Automation notes

- Each test starts from the menu and creates its own cart item when needed; tests no longer assume that a previous test left data in the cart.
- CSS selectors from the Selenium IDE recordings are retained where useful, but guarded with explicit waits and fallback selectors.
- `unavailableCustomizationTest` currently verifies menu/category loading only. To genuinely verify an unavailable customization, select a known product that is unavailable and assert its disabled/unavailable state.
- The price test verifies the customization panel remains available during selection. To assert an exact price change, use a known add-on with a confirmed additional charge and compare the displayed before/after amounts.
- These changes have not been executed against the live website in this environment; verify selectors against the current site before treating the suite as fully validated.
