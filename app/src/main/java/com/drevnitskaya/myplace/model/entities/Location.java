package com.drevnitskaya.myplace.model.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by air on 01.07.17.
 */

public class Location {

    @SerializedName("lat")
    String latitude;

    @SerializedName("lng")
    String longitude;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
