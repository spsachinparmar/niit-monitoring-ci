package NiitMonitoring;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeOptions;
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

    // URLs missing upcoming batch info (with course codes)
    private final List<String[]> failedCourses = new ArrayList<>();

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
        "https://www.niit.com/india/course/post-graduate-program-in-relationship-management-pgprm-2/#apply-how",
        "https://www.niit.com/india/course/generative-ai-essentials-on-aws/#apply-how",
        "https://www.niit.com/india/course/developing-on-aws/#apply-how",
        "https://www.niit.com/india/course/itil-4-foundation/#apply-how",
        "https://www.niit.com/india/course/certified-cloud-security-professional/#apply-how",
        "https://www.niit.com/india/course/certified-information-system-security-professional/#apply-how",
        "https://www.niit.com/india/course/certified-information-system-auditor/#apply-how",
        "https://www.niit.com/india/course/certified-information-security-management/#apply-how",
        "https://www.niit.com/india/course/certified-in-risk-and-information-systems-control/#apply-how",
        "https://www.niit.com/india/course/comptia-cybersecurity-analyst/#apply-how",
        "https://www.niit.com/india/course/comptia-security/#apply-how",
        "https://www.niit.com/india/course/certified-ethical-hacking-v13/#apply-how",
        "https://www.niit.com/india/course/architecting-on-aws/#apply-how",
        "https://www.niit.com/india/course/microsoft-security-compliance-and-identity-fundamentals/#apply-how",
        "https://www.niit.com/india/course/microsoft-power-bi-data-analyst/#apply-how",
        "https://www.niit.com/india/course/microsoft-azure-administrator/#apply-how"
        // Add remaining URLs here if needed
    );

    @BeforeAll
    void setUp() {
        System.setProperty("webdriver.chrome.driver",
                "C:\\Users\\sparmar\\Downloads\\Software folder\\chromedriver-win64 (5)\\chromedriver-win64\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);
        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(60));
        js = (JavascriptExecutor) driver;
    }

    @Test
    @Order(1)
    void checkUpcomingBatchForAllCourses() throws InterruptedException {
        for (String url : courseUrls) {
            driver.get(url);

            // Small pause to allow dynamic content to settle
            Thread.sleep(2000);

            // Try to get course code
            String courseCode = "";
            try {
                WebElement codeElement = driver.findElement(By.id("course_code"));
                courseCode = codeElement.getAttribute("value");
            } catch (Exception e) {
                courseCode = "N/A";
            }

            List<WebElement> upcomingBatch = driver.findElements(
                By.xpath("//h6[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'upcoming batch')]"
                        + " | //h3[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'upcoming batch')]"));

            if (upcomingBatch.isEmpty()) {
                failedCourses.add(new String[]{url, courseCode});
                System.out.println("❌ Upcoming Batch not found for: " + url + " (CourseCode: " + courseCode + ")");
            } else {
                System.out.println("✅ Upcoming Batch found for: " + url + " (CourseCode: " + courseCode + ")");
            }
        }

        // Assert that all URLs have upcoming batch (optional)
        Assertions.assertTrue(failedCourses.isEmpty(), "Some courses are missing Upcoming Batch!");
    }

    @AfterAll
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        generateHtmlReport();
    }

    private void generateHtmlReport() {
        if (failedCourses.isEmpty()) {
            System.out.println("All URLs have Upcoming Batch. No report generated.");
            return;
        }

        String reportFile = "BatchAvailabilityReport.html";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile))) {
            writer.write("<html><head><title>Batch Availability Report</title></head><body>");
            writer.write("<h2>Courses Missing Upcoming Batch</h2>");
            writer.write("<table border='1' cellpadding='5' cellspacing='0'>");
            writer.write("<tr><th>Course URL</th><th>Course Code</th></tr>");

            for (String[] entry : failedCourses) {
                String url = entry[0];
                String courseCode = entry[1];
                writer.write("<tr>");
                writer.write("<td><a href='" + url + "'>" + url + "</a></td>");
                writer.write("<td>" + courseCode + "</td>");
                writer.write("</tr>");
            }

            writer.write("</table>");
            writer.write("</body></html>");
            System.out.println("HTML report generated: " + reportFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}