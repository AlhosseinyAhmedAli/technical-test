package mobile.tests;

import mobile.base.BaseMobileTest;
import mobile.pages.ArticlePage;
import mobile.pages.ReadingListsPage;
import mobile.pages.SearchPage;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * End-to-end scenario (Task 2):
 *   1. Search for "Artificial Intelligence"
 *   2. Open the article
 *   3. Save it
 *   4-6. Add it to a brand-new reading list
 *   7-8. Navigate to Reading Lists and find the new list
 *   9. Verify the article is present in it
 *
 * This test is tagged "mobile" (see testng.xml) and excluded from the default
 * `mvn test` run because it requires a running Appium server plus an emulator
 * or device with the Wikipedia app installed -- see README.md for setup.
 */
public class SaveArticleToReadingListTest extends BaseMobileTest {

    private static final String ARTICLE_TITLE = "Artificial intelligence";

    @Test(description = "Saving an article to a newly created reading list makes it discoverable there")
    public void savedArticle_appearsInNewlyCreatedReadingList() {
        String readingListName = "AI Reading List " + System.currentTimeMillis();

        // 1-3. Search for and open the article, then save it.
        ArticlePage articlePage = new SearchPage(driver)
                .openSearch()
                .searchFor("Artificial Intelligence")
                .openResult(ARTICLE_TITLE);

        assertEquals(articlePage.getTitle(), ARTICLE_TITLE, "Opened article title should match the search term");

        articlePage.saveArticle();

        // 4-6. Add the article to a brand-new reading list.
        articlePage
                .openAddToReadingListDialog()
                .createNewReadingList(readingListName);

        // 7-8. Navigate to Reading Lists and locate the new list.
        ReadingListsPage readingListsPage = new ReadingListsPage(driver)
                .navigateToReadingLists()
                .searchForList(readingListName)
                .openList(readingListName);

        // 9. Verify the saved article is displayed inside the reading list.
        assertTrue(readingListsPage.isArticlePresent(ARTICLE_TITLE),
                "Expected '" + ARTICLE_TITLE + "' to be listed in reading list '" + readingListName + "'");
    }
}
