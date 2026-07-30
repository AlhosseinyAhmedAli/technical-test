package api.base;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.testng.annotations.BeforeClass;

/**
 * Common setup for all zippopotam.us API tests.
 *
 * Centralising the base URI / logging here keeps the individual test classes
 * focused purely on test cases and assertions (single responsibility).
 */
public class BaseApiTest {

    // Allows the target host to be overridden from the command line, e.g.
    // mvn test -DbaseApiUri=https://api.zippopotam.us
    protected static final String BASE_URI =
            System.getProperty("baseApiUri", "https://api.zippopotam.us");

    @BeforeClass(alwaysRun = true)
    public void setUpApiClient() {
        RestAssured.baseURI = BASE_URI;
        // Verbose logging only kicks in when a validation actually fails,
        // which keeps test output readable while still giving full detail on failure.
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }
}
