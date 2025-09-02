package NiitMonitoring;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // keeps driver alive for all tests
public class BatchAvailabilityCI {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;
    private final List<String> failedUrls = new ArrayList<>();

    // List of course URLs — keep this configurable
    private final List<String> courseUrls = Arrays.asList(
        "https://www.niit.com/india/course/individual/technology/software-engineering/full-stack-development-with-genai-honours-program/",
        "https://www.niit.com/india/course/individual/technology/software-engineering/java-development-certificate-program/",
        "https://www.niit.com/india/course/sql-essentials-with-genai/#apply-how",
        "https://www.niit.com/india/course/programming-using-python/#apply-how",
        "https://www.niit.com/india/course/java-object-oriented-programming/#apply-how",
        "https://www.niit.com/india/course/front-end-dev-with-react-genai-advanced-program-2/#apply-how",
        "https://www.niit.com/india/course/cybersecurity-with-genai-advanced-program/#apply-how",
        "https://www.niit.com/india/course/swift-cybersecurity-homemakers/#apply-how",
        "https://www.niit.com/india/course/swiftcybersecurity-senior-citizens/#apply-how",
        "https://www.niit.com/india/course/swiftcybersecurity-finances-online/#apply-how",
        "https://www.niit.com/india/course/gniit-digital-be-real-world-ready/#apply-how",
        "https://www.niit.com/india/course/it-sysadmin-cloud-computing-advanced-program/#apply-how",
        "https://www.niit.com/india/course/data-analytics-with-python-and-sql/#apply-how",
        "https://www.niit.com/india/course/managing-and-querying-database/#apply-how",
        "https://www.niit.com/india/course/pgp-in-machine-learning-artificial-intelligence/#apply-how",
        "https://www.niit.com/india/course/data-storytelling-and-visualization/#apply-how",
        "https://www.niit.com/india/course/data-science-and-ml-with-genai-advanced-program/#apply-how",
        "https://www.niit.com/india/course/swiftgenai-content-developers/#apply-how",
        "https://www.niit.com/india/course/swiftgenai-teachers/#apply-how",
        "https://www.niit.com/india/course/swiftgenal-students/#apply-how",
        "https://www.niit.com/india/course/digital-communication-and-genai-tools/#apply-how",
        "https://www.niit.com/india/course/swiftgenai-homemakers/#apply-how",
        "https://www.niit.com/india/course/digital-persona-and-media/#apply-how",
        "https://www.niit.com/india/course/social-media-and-influencer-marketing-specialist/#apply-how",
        "https://www.niit.com/india/course/seo-and-growth-marketing-specialist/#apply-how",
        "https://www.niit.com/india/course/digital-marketing-program/#apply-how",
        "https://www.niit.com/india/course/program-in-relationship-management-for-under-graduates-prmug/#apply-how",
        "https://www.niit.com/india/course/financial-planning-and-stock-market-management/#apply-how",
        "https://www.niit.com/india/course/post-graduate-program-in-banking-sales-relationship-management-pgbsr/#apply-how",
        "https://www.niit.com/india/course/post-graduate-program-in-relationship-management-pgprm-2/#apply-how"
        // Add remaining 46 URLs here
    );

    @BeforeAll
    void setUp() {
        System.setProperty("webdriver.chrome.driver",
                "C:\\Users\\sparmar\\Downloads\\Software folder\\chromedriver-win64 (5)\\chromedriver-win64\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        js = (JavascriptExecutor) driver;
    }

    @Test
    @Order(1)
    void checkUpcomingBatchForAllCourses() throws InterruptedException {
        for (String url : courseUrls) {
            driver.get(url);
            wait.until(webDriver -> js.executeScript("return document.readyState").equals("complete"));
            Thread.sleep(2000);

            List<WebElement> upcomingBatch = driver.findElements(
            		By.xpath("//h6[@class='upcoming-batch-heading' and contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'upcoming batch')]"
                            + " | //h3[@class='batch-heading' and contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'upcoming batch')]"));

            if (upcomingBatch.isEmpty()) {
                failedUrls.add(url);
                System.out.println("❌ Upcoming Batch not found for: " + url);
            } else {
                System.out.println("✅ Upcoming Batch found for: " + url);
            }
        }

        // Assert that all URLs have upcoming batch (optional)
        Assertions.assertTrue(failedUrls.isEmpty(), "Some courses are missing Upcoming Batch!");
    }

    @AfterAll
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        generateHtmlReport();
    }

    private void generateHtmlReport() {
        if (failedUrls.isEmpty()) {
            System.out.println("All URLs have Upcoming Batch. No report generated.");
            return;
        }

        String reportFile = "BatchAvailabilityReport.html";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile))) {
            writer.write("<html><head><title>Batch Availability Report</title></head><body>");
            writer.write("<h2>Courses Missing Upcoming Batch</h2>");
            writer.write("<ul>");
            for (String url : failedUrls) {
                writer.write("<li><a href='" + url + "'>" + url + "</a></li>");
            }
            writer.write("</ul>");
            writer.write("</body></html>");
            System.out.println("HTML report generated: " + reportFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}