package utilities;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {

    private static ExtentReports extent;

    private ExtentManager() {
    }

    public static synchronized ExtentReports getInstance() {

        if (extent == null) {

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            String reportPath = System.getProperty("user.dir")
                    + File.separator + "Reports"
                    + File.separator + "CustomizationGrillReport_" + timeStamp + ".html";

            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
            spark.config().setDocumentTitle("McD Customization Grill");
            spark.config().setReportName("McDelivery Location Module - Automation Report");

            extent = new ExtentReports();
            extent.attachReporter(spark);

            extent.setSystemInfo("Application", ConfigReader.getProperty("url"));
            extent.setSystemInfo("Module", "Location");
            extent.setSystemInfo("Framework", "Selenium WebDriver + TestNG");
            extent.setSystemInfo("Browser", ConfigReader.getProperty("browser"));
            extent.setSystemInfo("Tester", "Mahima Yadav");
        }

        return extent;
    }
}
