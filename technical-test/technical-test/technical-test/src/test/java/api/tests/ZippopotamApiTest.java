package api.tests;

import api.base.BaseApiTest;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import util.TestData;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

public class ZippopotamApiTest extends BaseApiTest {

    private static final String ENDPOINT = "/{country}/{postalCode}";
    private static final String DEFAULT_COUNTRY = TestData.get("api", "default.country", "us");
    private static final String DEFAULT_POSTAL = TestData.get("api", "default.postal", "90210");

    @Test(description = "A known country + postal code combination returns 200 with the correct content type")
    public void validRequest_returnsOkWithJsonContentType() {
        given()
            .pathParam("country", DEFAULT_COUNTRY)
            .pathParam("postalCode", DEFAULT_POSTAL)
        .when()
            .get(ENDPOINT)
        .then()
            .statusCode(200)
            .contentType("application/json");
    }

    @Test(description = "A known country + postal code combination returns a body matching the documented schema")
    public void validRequest_matchesExpectedSchema() {
        given()
            .pathParam("country", DEFAULT_COUNTRY)
            .pathParam("postalCode", DEFAULT_POSTAL)
        .when()
            .get(ENDPOINT)
        .then()
            .statusCode(200)
            .body(matchesJsonSchemaInClasspath("schemas/location-response-schema.json"));
    }

    @Test(description = "Field values for a well-known postal code are correct (not just present)")
    public void validRequest_returnsCorrectLocationData() {
        api.models.LocationResponse response =
            given()
                .pathParam("country", DEFAULT_COUNTRY)
                .pathParam("postalCode", DEFAULT_POSTAL)
            .when()
                .get(ENDPOINT)
            .then()
                .statusCode(200)
                .extract().as(api.models.LocationResponse.class);

        assertEquals(response.getCountry(), "United States", "country");
        assertEquals(response.getCountryAbbreviation(), "US", "country abbreviation");
        assertEquals(response.getPostCode(), DEFAULT_POSTAL, "post code should echo back the request");
        assertFalse(response.getPlaces().isEmpty(), "places array should not be empty");
        assertEquals(response.getPlaces().get(0).getPlaceName(), "Beverly Hills", "place name");
        assertEquals(response.getPlaces().get(0).getStateAbbreviation(), "CA", "state abbreviation");
    }

    @Test(description = "Latitude and longitude returned for a place are valid, parseable coordinates")
    public void validRequest_latitudeAndLongitudeAreValidCoordinates() {
        api.models.LocationResponse response =
            given()
                .pathParam("country", DEFAULT_COUNTRY)
                .pathParam("postalCode", DEFAULT_POSTAL)
            .when()
                .get(ENDPOINT)
            .then()
                .statusCode(200)
                .extract().as(api.models.LocationResponse.class);

        double lat = Double.parseDouble(response.getPlaces().get(0).getLatitude());
        double lon = Double.parseDouble(response.getPlaces().get(0).getLongitude());

        assertTrue(lat >= -90 && lat <= 90, "latitude out of range: " + lat);
        assertTrue(lon >= -180 && lon <= 180, "longitude out of range: " + lon);
    }


    @DataProvider(name = "validCountryPostalCodes")
    public Object[][] validCountryPostalCodes() {
        return new Object[][]{
                {"us", "90210", "United States", "Beverly Hills"},
                {"de", "10115", "Germany", "Berlin"},
                {"ca", "B2A", "Canada", "North Sydney South Central"},
        };
    }

    @Test(dataProvider = "validCountryPostalCodes",
          description = "The endpoint resolves correctly across a range of supported countries")
    public void validRequest_resolvesAcrossDifferentCountries(String country, String postalCode,
                                                                String expectedCountry, String expectedPlaceContains) {
        Response response =
            given()
                .pathParam("country", country)
                .pathParam("postalCode", postalCode)
            .when()
                .get(ENDPOINT);

        response.then().statusCode(200);
        assertEquals(response.jsonPath().getString("country"), expectedCountry);
        String actualPlace = response.jsonPath().getString("places[0].'place name'");
        assertTrue(actualPlace != null && !actualPlace.isEmpty(), "place name should not be empty");
        assertTrue(actualPlace.contains(expectedPlaceContains),
                "expected place name to contain '" + expectedPlaceContains + "' but was '" + actualPlace + "'");
    }

    @Test(description = "A well-formed but non-existent postal code returns 404")
    public void nonExistentPostalCode_returns404() {
        given()
            .pathParam("country", DEFAULT_COUNTRY)
            .pathParam("postalCode", "00000")
        .when()
            .get(ENDPOINT)
        .then()
            .statusCode(404);
    }

    @Test(description = "An unsupported/invalid country code returns 404")
    public void invalidCountryCode_returns404() {
        given()
            .pathParam("country", "xx")
            .pathParam("postalCode", DEFAULT_POSTAL)
        .when()
            .get(ENDPOINT)
        .then()
            .statusCode(404);
    }

    @Test(description = "A postal code containing letters where the country expects digits returns 404")
    public void malformedPostalCode_returns404() {
        given()
            .pathParam("country", DEFAULT_COUNTRY)
            .pathParam("postalCode", "ABCDE")
        .when()
            .get(ENDPOINT)
        .then()
            .statusCode(404);
    }

    @Test(description = "Omitting the postal code entirely (trailing slash only) does not resolve a location")
    public void missingPostalCode_returnsNonOkStatus() {
        given()
            .pathParam("country", DEFAULT_COUNTRY)
        .when()
            .get("/{country}/")
        .then()
            .statusCode(not(200));
    }

    @Test(description = "An empty country segment does not resolve a location")
    public void emptyCountry_returnsNonOkStatus() {
        given()
        .when()
            .get("//90210")
        .then()
            .statusCode(not(200));
    }

    @Test(description = "A postal code with a leading zero is preserved and resolved correctly (not treated as a number)")
    public void postalCodeWithLeadingZero_isResolvedCorrectly() {
        given()
            .pathParam("country", DEFAULT_COUNTRY)
            .pathParam("postalCode", "00501")
        .when()
            .get(ENDPOINT)
        .then()
            .statusCode(200)
            .body("'post code'", equalTo("00501"));
    }

    @Test(description = "Country code lookup is case-insensitive (US and us both resolve)")
    public void countryCodeIsCaseInsensitive() {
        Response lower =
            given().pathParam("country", DEFAULT_COUNTRY).pathParam("postalCode", DEFAULT_POSTAL)
            .when().get(ENDPOINT);

        Response upper =
            given().pathParam("country", DEFAULT_COUNTRY.toUpperCase()).pathParam("postalCode", DEFAULT_POSTAL)
            .when().get(ENDPOINT);

        assertEquals(lower.statusCode(), 200);
        assertEquals(upper.statusCode(), 200);
        assertEquals(lower.jsonPath().getString("country"), upper.jsonPath().getString("country"));
    }

    @Test(description = "A postal code that maps to multiple places returns all of them, each with required fields populated")
    public void postalCodeWithMultiplePlaces_returnsAllPlacesFullyPopulated() {

        Response response =
            given()
                .pathParam("country", DEFAULT_COUNTRY)
                .pathParam("postalCode", DEFAULT_POSTAL)
            .when()
                .get(ENDPOINT);

        response.then()
            .statusCode(200)
            .body("places", not(empty()))
            .body("places.every { it.'place name' != null && it.'place name'.length() > 0 }", is(true))
            .body("places.every { it.latitude != null }", is(true))
            .body("places.every { it.longitude != null }", is(true));
    }

    @Test(description = "The endpoint responds within an acceptable time budget")
    public void response_isReturnedWithinAcceptableTime() {
        given()
            .pathParam("country", DEFAULT_COUNTRY)
            .pathParam("postalCode", DEFAULT_POSTAL)
        .when()
            .get(ENDPOINT)
        .then()
            .statusCode(200)
            .time(lessThan(3000L));
    }
}
