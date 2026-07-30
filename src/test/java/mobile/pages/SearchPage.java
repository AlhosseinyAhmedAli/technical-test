package mobile.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Represents the app's home screen search entry point and the search results list.
 *
 * NOTE ON LOCATORS: resource-ids below match the current public build of the
 * Wikipedia Android app (org.wikipedia) at the time of writing. App UI ids can
 * change between releases -- if a locator here breaks, re-capture it with
 * Appium Inspector / uiautomatorviewer against the installed build and update
 * this class only; no other class needs to change (that's the point of POM).
 */
public class SearchPage {

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    private final By searchEntryPoint = AppiumBy.id("org.wikipedia:id/search_container");
    private final By searchInput = AppiumBy.id("org.wikipedia:id/search_src_text");
    private final By searchResultsList = AppiumBy.id("org.wikipedia:id/search_results_list");

    public SearchPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public SearchPage openSearch() {
        wait.until(ExpectedConditions.elementToBeClickable(searchEntryPoint)).click();
        return this;
    }

    public SearchPage searchFor(String query) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(searchInput));
        input.click();
        input.sendKeys(query);
        wait.until(ExpectedConditions.presenceOfElementLocated(searchResultsList));
        return this;
    }

    /**
     * Opens the search result whose title exactly matches {@code articleTitle}.
     */
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
