package com.drevnitskaya.myplace.model.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by air on 03.07.17.
 */

public class PlaceDetailsResponse {

    @SerializedName("result")
    PlaceDetails result;

    public PlaceDetails getResult() {
        return result;
    }

    public void setResult(PlaceDetails result) {
        this.result = result;
    }
}
