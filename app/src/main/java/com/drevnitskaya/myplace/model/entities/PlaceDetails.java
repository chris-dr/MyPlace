package com.drevnitskaya.myplace.model.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * Created by air on 03.07.17.
 */
public class PlaceDetails extends RealmObject implements Parcelable {

    @PrimaryKey
    @SerializedName("place_id")
    String placeId;

    @SerializedName("name")
    String name;

    @SerializedName("formatted_address")
    String address;

    @SerializedName("url")
    String urlLink;

    @SerializedName("geometry")
    Geometry geometry;


    public PlaceDetails() {
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
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

    public Geometry getGeometry() {
        return geometry;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.placeId);
        dest.writeString(this.name);
        dest.writeString(this.address);
        dest.writeString(this.urlLink);
        dest.writeParcelable(this.geometry, flags);
    }

    protected PlaceDetails(Parcel in) {
        this.placeId = in.readString();
        this.name = in.readString();
        this.address = in.readString();
        this.urlLink = in.readString();
        this.geometry = in.readParcelable(Geometry.class.getClassLoader());
    }

    public static final Creator<PlaceDetails> CREATOR = new Creator<PlaceDetails>() {
        @Override
        public PlaceDetails createFromParcel(Parcel source) {
            return new PlaceDetails(source);
        }

        @Override
        public PlaceDetails[] newArray(int size) {
            return new PlaceDetails[size];
        }
    };
}
