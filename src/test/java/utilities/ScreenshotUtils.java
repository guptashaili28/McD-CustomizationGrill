package utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class ScreenshotUtils {

    private ScreenshotUtils() {
    }

    /** Saves a PNG into Screenshots/ and returns its absolute path. */
    public static String takeScreenshot(WebDriver driver, String testName) {

        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        File dir = new File(System.getProperty("user.dir"), "Screenshots");
        dir.mkdirs();
        File target = new File(dir, testName + ".png");

        try {
            Files.copy(src.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return target.getAbsolutePath();
    }
}
