package mobile.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Represents an open article page: the save/bookmark action and the
 * "add to reading list" dialog that follows it.
 */
public class ArticlePage {

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    private final By articleTitle = AppiumBy.id("org.wikipedia:id/view_page_title_text");
    private final By saveButton = AppiumBy.id("org.wikipedia:id/page_save");
    // Snackbar/dialog shown immediately after the first save, offering to change the target list.
    private final By addToListSnackbarAction = AppiumBy.id("org.wikipedia:id/snackbar_action");
    private final By createNewListOption = AppiumBy.xpath(
            "//*[contains(@text,'Create new reading list') or contains(@text,'New list')]");
    private final By newListNameInput = AppiumBy.id("org.wikipedia:id/text_input");
    private final By newListDialogOkButton = AppiumBy.id("android:id/button1");

    public ArticlePage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public String getTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(articleTitle)).getText();
    }

    /**
     * Taps the save/bookmark icon in the article toolbar. On first save this
     * adds the article to the default reading list and surfaces a snackbar
     * with an action to change/add lists.
     */
    public ArticlePage saveArticle() {
        wait.until(ExpectedConditions.elementToBeClickable(saveButton)).click();
        return this;
    }

    /**
     * From the "Added to reading lists" snackbar, opens the list picker so a
     * different/new list can be chosen for the article.
     */
    public ArticlePage openAddToReadingListDialog() {
        wait.until(ExpectedConditions.elementToBeClickable(addToListSnackbarAction)).click();
        return this;
    }

    /**
     * Creates a brand-new reading list with the given name and confirms the
     * article should be saved into it.
     */
    public ArticlePage createNewReadingList(String listName) {
        wait.until(ExpectedConditions.elementToBeClickable(createNewListOption)).click();

        WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(newListNameInput));
        nameField.clear();
        nameField.sendKeys(listName);

        wait.until(ExpectedConditions.elementToBeClickable(newListDialogOkButton)).click();
        return this;
    }
}
