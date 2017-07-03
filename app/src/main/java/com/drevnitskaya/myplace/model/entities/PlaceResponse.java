package com.drevnitskaya.myplace.model.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by air on 01.07.17.
 */

public class PlaceResponse {

    public static final String STATUS_PLACES_NOT_FOUND = "ZERO_RESULTS";
    public static final String STATUS_PLACES_RECEIVED = "OK";

    @SerializedName("results")
    List<Place> results;

    @SerializedName("status")
    String status;

    public PlaceResponse() {

    }

    public List<Place> getResults() {
        return results;
    }

    public void setResults(List<Place> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
