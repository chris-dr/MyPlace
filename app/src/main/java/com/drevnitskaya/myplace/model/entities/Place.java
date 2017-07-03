package com.drevnitskaya.myplace.model.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by air on 01.07.17.
 */
public class Place {

    @SerializedName("place_id")
    String placeId;

    public Place() {
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
