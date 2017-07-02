package com.drevnitskaya.myplace.model.entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by air on 01.07.17.
 */

public class PlaceResponce {

    @SerializedName("results")
    List<Place> results;

    public PlaceResponce() {

    }

    public List<Place> getResults() {
        return results;
    }

    public void setResults(List<Place> results) {
        this.results = results;
    }
}
