package api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Maps the top-level JSON response returned by
 * GET https://api.zippopotam.us/{country}/{postal-code}
 */
public class LocationResponse {

    @JsonProperty("post code")
    private String postCode;

    @JsonProperty("country")
    private String country;

    @JsonProperty("country abbreviation")
    private String countryAbbreviation;

    @JsonProperty("places")
    private List<Place> places;

    public String getPostCode() {
        return postCode;
    }

    public String getCountry() {
        return country;
    }

    public String getCountryAbbreviation() {
        return countryAbbreviation;
    }

    public List<Place> getPlaces() {
        return places;
    }
}
