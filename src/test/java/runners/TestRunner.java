package runners;

import java.util.List;

import org.testng.TestNG;

/**
 * Test runner class: runs the TestNG suite (testng.xml) without Maven,
 * e.g. "Run As > Java Application" in Eclipse.
 *
 * Optional arguments: browser and headless, e.g. "chrome true".
 * From Maven use: mvn clean test [-Dbrowser=chrome] [-Dheadless=true]
 */
public class TestRunner {

    public static void main(String[] args) {
        if (args.length > 0) {
            System.setProperty("browser", args[0]);
        }
        if (args.length > 1) {
            System.setProperty("headless", args[1]);
        }

        TestNG testng = new TestNG();
        testng.setTestSuites(List.of("testng.xml"));
        testng.setOutputDirectory("test-output");
        testng.run();

        System.exit(testng.hasFailure() ? 1 : 0);
    }
}
