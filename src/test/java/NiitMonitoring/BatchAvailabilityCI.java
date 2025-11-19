package NiitMonitoring;


import io.github.bonigarcia.wdm.WebDriverManager;
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
import org.openqa.selenium.support.ui.WebDriverWait;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BatchAvailabilityCI {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    private final List<String[]> failedCourses = new ArrayList<>();

    // Your URL list (same as before)
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
        "https://www.niit.com/india/course/microsoft-azure-administrator/#apply-how",
        "https://www.niit.com/india/course/sql-essentials-with-genai-alumniit/",
        "https://www.niit.com/india/course/front-end-dev-with-react-genai-advanced-program-upgrade/",
        "https://www.niit.com/india/course/full-stack-development-with-genai-honours-program-upgrade-from-pspjp/",
        "https://www.niit.com/india/course/pgp-in-machine-learning-artificial-intelligence-upgrade/",
        "https://www.niit.com/india/course/digital-marketing-with-genai-advanced-program-upgrade/",
        "https://www.niit.com/india/course/professional-program-in-data-analytics-with-genai/",
        "https://www.niit.com/india/course/building-agentic-ai-systems/#apply-how"
    );

    @BeforeAll
    void setUp() {

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

        // Headless mode enabled automatically in Linux (GitHub Actions/Jenkins)
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        }

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
            Thread.sleep(2000);

            String courseCode = "N/A";
            try {
                WebElement codeElement = driver.findElement(By.id("course_code"));
                courseCode = codeElement.getAttribute("value");
            } catch (Exception ignored) {}

            List<WebElement> upcomingBatch = driver.findElements(
                By.xpath("//h6[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'upcoming batch')]"
                       + " | //h3[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'upcoming batch')]"));

            if (upcomingBatch.isEmpty()) {
                failedCourses.add(new String[]{url, courseCode});
                System.out.println("❌ Upcoming Batch NOT found: " + url + " (Code: " + courseCode + ")");
            } else {
                System.out.println("✅ Upcoming Batch found: " + url + " (Code: " + courseCode + ")");
            }
        }

        Assertions.assertTrue(failedCourses.isEmpty(),
            "Some courses are missing Upcoming Batch. Check HTML Report!");
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
            System.out.println("All courses have Upcoming Batch ✔");
            return;
        }

        String reportFile = "BatchAvailabilityReport.html";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile))) {
            writer.write("<html><head><title>Batch Availability Report</title></head><body>");
            writer.write("<h2>Courses Missing Upcoming Batch</h2>");
            writer.write("<table border='1' cellpadding='5' cellspacing='0'>");
            writer.write("<tr><th>Course URL</th><th>Course Code</th></tr>");

            for (String[] entry : failedCourses) {
                writer.write("<tr>");
                writer.write("<td><a href='" + entry[0] + "'>" + entry[0] + "</a></td>");
                writer.write("<td>" + entry[1] + "</td>");
                writer.write("</tr>");
            }

            writer.write("</table></body></html>");
            System.out.println("HTML report generated: " + reportFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}