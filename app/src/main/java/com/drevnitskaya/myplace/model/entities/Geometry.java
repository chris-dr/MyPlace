package com.drevnitskaya.myplace.model.entities;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by air on 04.07.17.
 */

public class Geometry extends RealmObject {

    @SerializedName("location")
    Location location;

    public Location getLocation() {
        return location;
    }
}
