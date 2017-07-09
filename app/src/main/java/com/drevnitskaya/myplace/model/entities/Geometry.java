package com.drevnitskaya.myplace.model.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Created by air on 04.07.17.
 */
public class Geometry extends RealmObject implements Parcelable {

    @SerializedName("location")
    Location location;

    public Location getLocation() {
        return location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.location, flags);
    }

    public Geometry() {
    }

    protected Geometry(Parcel in) {
        this.location = in.readParcelable(Location.class.getClassLoader());
    }

    public static final Creator<Geometry> CREATOR = new Creator<Geometry>() {
        @Override
        public Geometry createFromParcel(Parcel source) {
            return new Geometry(source);
        }

        @Override
        public Geometry[] newArray(int size) {
            return new Geometry[size];
        }
    };
}
