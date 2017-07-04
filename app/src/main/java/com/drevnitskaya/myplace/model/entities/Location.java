package com.drevnitskaya.myplace.model.entities;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by air on 04.07.17.
 */

public class Location extends RealmObject {

    @SerializedName("lat")
    double latitude;

    @SerializedName("lng")
    double longitude;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
