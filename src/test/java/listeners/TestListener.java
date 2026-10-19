package listeners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.openqa.selenium.WebDriver;
import org.testng.IAnnotationTransformer;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;

import com.aventstack.extentreports.ExtentTest;

import utilities.DriverFactory;
import utilities.ExtentManager;
import utilities.ScreenshotUtils;

/**
 * Writes every TestNG result to the Extent report, attaches a screenshot on
 * failure and attaches the RetryAnalyzer to every test method.
 */
public class TestListener implements ITestListener, ISuiteListener, IAnnotationTransformer {

    private static final ThreadLocal<ExtentTest> TEST = new ThreadLocal<>();

    @Override
    @SuppressWarnings("rawtypes")
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }

    @Override
    public void onTestStart(ITestResult result) {
        String name = result.getMethod().getMethodName();
        if (result.getParameters().length > 0) {
            name += " " + Arrays.toString(result.getParameters());
        }
        ExtentTest test = ExtentManager.getInstance()
                .createTest(name, result.getMethod().getDescription())
                .assignCategory(result.getMethod().getGroups());
        TEST.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        TEST.get().pass("Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = TEST.get();
        test.fail(result.getThrowable());
        WebDriver driver = DriverFactory.getDriver();
        if (driver != null) {
            String path = ScreenshotUtils.takeScreenshot(driver, result.getMethod().getMethodName());
            test.addScreenCaptureFromPath(path);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest test = TEST.get();
        if (test == null) {
            return;
        }
        if (result.wasRetried()) {
            test.skip("Failed on first attempt and was retried: " + result.getThrowable());
        } else {
            test.skip(result.getThrowable() == null ? "Test skipped" : result.getThrowable().toString());
        }
    }

    @Override
    public void onFinish(ISuite suite) {
        ExtentManager.getInstance().flush();
    }

    public static ExtentTest getTest() {
        return TEST.get();
    }
}
