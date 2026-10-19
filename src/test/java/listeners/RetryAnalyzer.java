package listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import utilities.ConfigReader;

/**
 * Re-runs a failed test (retryCount in config.properties) because the
 * McDelivery backend intermittently leaves the page on skeleton loaders.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int attempt = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (attempt < ConfigReader.getInt("retryCount")) {
            attempt++;
            System.out.println("Retrying " + result.getName() + " (attempt " + (attempt + 1) + ")");
            return true;
        }
        return false;
    }
}
