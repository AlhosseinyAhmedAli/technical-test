package mobile.tests;

import mobile.base.BaseMobileTest;
import mobile.pages.ArticlePage;
import mobile.pages.ReadingListsPage;
import mobile.pages.SearchPage;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class SaveArticleToReadingListTest extends BaseMobileTest {

    private static final String ARTICLE_TITLE = "Artificial intelligence";

    @Test(description = "Saving an article to a newly created reading list makes it discoverable there")
    public void savedArticle_appearsInNewlyCreatedReadingList() {
        String readingListName = "AI Reading List " + System.currentTimeMillis();
        ArticlePage articlePage = new SearchPage(driver)
                .openSearch()
                .searchFor("Artificial Intelligence")
                .openResult(ARTICLE_TITLE);

        assertEquals(articlePage.getTitle(), ARTICLE_TITLE, "Opened article title should match the search term");

        articlePage.saveArticle();

                .openAddToReadingListDialog()
                .createNewReadingList(readingListName);
        ReadingListsPage readingListsPage = new ReadingListsPage(driver)
                .navigateToReadingLists()
                .searchForList(readingListName)
                .openList(readingListName);
        assertTrue(readingListsPage.isArticlePresent(ARTICLE_TITLE),
                "Expected '" + ARTICLE_TITLE + "' to be listed in reading list '" + readingListName + "'");
    }
}
