package com.drevnitskaya.myplace.model.entities;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by air on 03.07.17.
 */

public class PlaceDetails extends RealmObject {

    @PrimaryKey
    @SerializedName("place_id")
    String placeId;

    @SerializedName("name")
    String name;

    @SerializedName("formatted_address")
    String address;

    @SerializedName("url")
    String urlLink;

    public PlaceDetails() {
    }

    public String getId() {
        return placeId;
    }

    public void setId(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlLink() {
        return urlLink;
    }

    public void setUrlLink(String urlLink) {
        this.urlLink = urlLink;
    }

    public String getAddress() {
        return address;
    }
}
