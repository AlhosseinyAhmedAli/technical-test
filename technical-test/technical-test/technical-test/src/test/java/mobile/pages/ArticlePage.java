package mobile.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ArticlePage {

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    private final By articleTitle = AppiumBy.id("org.wikipedia:id/view_page_title_text");
    private final By saveButton = AppiumBy.id("org.wikipedia:id/page_save");
    private final By addToListSnackbarAction = AppiumBy.id("org.wikipedia:id/snackbar_action");
    private final By createNewListOption = AppiumBy.xpath(
            "//*[contains(@text,'Create new reading list') or contains(@text,'New list')]");
    private final By newListNameInput = AppiumBy.id("org.wikipedia:id/text_input");
    private final By newListDialogOkButton = AppiumBy.id("android:id/button1");

    public ArticlePage(AndroidDriver driver) {
        this.driver = driver;
        int timeout = Integer.parseInt(util.TestData.get("mobile", "page.timeout", "15"));
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    public String getTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(articleTitle)).getText();
    }

    public ArticlePage saveArticle() {
        wait.until(ExpectedConditions.elementToBeClickable(saveButton)).click();
        return this;
    }

    public ArticlePage openAddToReadingListDialog() {
        wait.until(ExpectedConditions.elementToBeClickable(addToListSnackbarAction)).click();
        return this;
    }


    public ArticlePage createNewReadingList(String listName) {
        wait.until(ExpectedConditions.elementToBeClickable(createNewListOption)).click();

        WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(newListNameInput));
        nameField.clear();
        nameField.sendKeys(listName);

        wait.until(ExpectedConditions.elementToBeClickable(newListDialogOkButton)).click();
        return this;
    }
}
