package api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Place {

    @JsonProperty("place name")
    private String placeName;

    @JsonProperty("longitude")
    private String longitude;

    @JsonProperty("state")
    private String state;

    @JsonProperty("state abbreviation")
    private String stateAbbreviation;

    @JsonProperty("latitude")
    private String latitude;

    public String getPlaceName() {
        return placeName;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getState() {
        return state;
    }

    public String getStateAbbreviation() {
        return stateAbbreviation;
    }

    public String getLatitude() {
        return latitude;
    }

    @Override
    public String toString() {
        return "Place{" +
                "placeName='" + placeName + '\'' +
                ", state='" + state + '\'' +
                ", stateAbbreviation='" + stateAbbreviation + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }
}
