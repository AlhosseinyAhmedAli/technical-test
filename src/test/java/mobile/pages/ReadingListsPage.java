package mobile.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ReadingListsPage {

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    private final By readingListsTab = AppiumBy.accessibilityId("Reading lists");
    private final By readingListsSearchIcon = AppiumBy.id("org.wikipedia:id/menu_search_lists");
    private final By readingListsSearchInput = AppiumBy.id("org.wikipedia:id/search_src_text");
    private final By readingListEntries = AppiumBy.id("org.wikipedia:id/item_title");
    private final By articleEntriesInsideList = AppiumBy.id("org.wikipedia:id/page_list_item_title");

    public ReadingListsPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public ReadingListsPage navigateToReadingLists() {
        wait.until(ExpectedConditions.elementToBeClickable(readingListsTab)).click();
        return this;
    }

    public ReadingListsPage searchForList(String listName) {
        wait.until(ExpectedConditions.elementToBeClickable(readingListsSearchIcon)).click();
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(readingListsSearchInput));
        input.sendKeys(listName);
        return this;
    }

    public ReadingListsPage openList(String listName) {
        List<WebElement> entries = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(readingListEntries));

        entries.stream()
                .filter(el -> el.getText().trim().equalsIgnoreCase(listName))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Reading list '" + listName + "' was not found"))
                .click();

        return this;
    }

    public boolean isArticlePresent(String articleTitle) {
        List<WebElement> articles = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(articleEntriesInsideList));

        return articles.stream()
                .anyMatch(el -> el.getText().trim().equalsIgnoreCase(articleTitle));
    }
}
