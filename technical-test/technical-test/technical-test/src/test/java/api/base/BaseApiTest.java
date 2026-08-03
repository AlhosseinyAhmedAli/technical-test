package api.base;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.testng.annotations.BeforeClass;
import util.TestData;

public class BaseApiTest {

    protected static final String BASE_URI_DEFAULT = "https://api.zippopotam.us";
    @BeforeClass(alwaysRun = true)
    public void setUpApiClient() {
        String baseUri = System.getProperty("baseApiUri");
        if (baseUri == null || baseUri.isBlank()) {
            baseUri = TestData.get("api", "baseApiUri", BASE_URI_DEFAULT);
        }
        RestAssured.baseURI = baseUri;
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }
}
