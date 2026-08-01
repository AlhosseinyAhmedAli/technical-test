package api.base;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.testng.annotations.BeforeClass;


public class BaseApiTest {

   
    protected static final String BASE_URI =
            System.getProperty("baseApiUri", "https://api.zippopotam.us");

    @BeforeClass(alwaysRun = true)
    public void setUpApiClient() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }
}
