package mobile.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;


public class SearchPage {

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    private final By searchEntryPoint = AppiumBy.id("org.wikipedia:id/nav_tab_search");
    private final By searchInput = AppiumBy.id("org.wikipedia:id/search_src_text");
    private final By searchResultsList = AppiumBy.id("org.wikipedia:id/search_results_list");

    public SearchPage(AndroidDriver driver) {
        this.driver = driver;
        int timeout = Integer.parseInt(util.TestData.get("mobile", "page.timeout", "15"));
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    public SearchPage openSearch() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(searchEntryPoint));
            org.openqa.selenium.WebElement el = wait.until(ExpectedConditions.elementToBeClickable(searchEntryPoint));
            el.click();
            return this;
        } catch (org.openqa.selenium.TimeoutException e) {
            try {
                System.err.println("openSearch failed. Current package/activity: " + driver.getCurrentPackage() + "/" + driver.currentActivity());
                String pageSource = driver.getPageSource();
                System.err.println("Page source length: " + (pageSource == null ? 0 : pageSource.length()));
                // attempt to save a screenshot to a temp file
                java.io.File scr = ((org.openqa.selenium.TakesScreenshot) driver).getScreenshotAs(org.openqa.selenium.OutputType.FILE);
                java.nio.file.Path dest = java.nio.file.Files.createTempFile("openSearch-failure-", ".png");
                java.nio.file.Files.copy(scr.toPath(), dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                System.err.println("Screenshot saved to: " + dest.toString());
            } catch (Exception ex) {
                System.err.println("Failed to capture diagnostics: " + ex.getMessage());
            }
            throw new RuntimeException("Failed to open search: element '" + searchEntryPoint + "' not found or not clickable", e);
        }
    }

    public SearchPage searchFor(String query) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput));
        input.click();
        input.sendKeys(query);
        wait.until(ExpectedConditions.presenceOfElementLocated(searchResultsList));
        return this;
    }

    public ArticlePage openResult(String articleTitle) {
        List<WebElement> results = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        AppiumBy.id("org.wikipedia:id/page_list_item_title")));

        results.stream()
                .filter(el -> el.getText().trim().equalsIgnoreCase(articleTitle))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "No search result titled '" + articleTitle + "' was found"))
                .click();

        return new ArticlePage(driver);
    }
}
