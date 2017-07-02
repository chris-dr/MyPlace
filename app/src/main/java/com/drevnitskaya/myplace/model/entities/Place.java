package com.drevnitskaya.myplace.model.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by air on 01.07.17.
 */

public class Place {

    @SerializedName("id")
    String id;

    @SerializedName("name")
    String name;

    @SerializedName("reference")
    String reference;

    @SerializedName("geometry")
    Geometry geometry;

    public Place() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}
