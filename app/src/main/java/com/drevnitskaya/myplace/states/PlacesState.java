package com.drevnitskaya.myplace.states;

import android.os.Parcel;
import android.os.Parcelable;

import com.drevnitskaya.myplace.contracts.NearbyPlacesContract;
import com.drevnitskaya.myplace.model.entities.PlaceDetails;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by air on 09.07.17.
 */

public class PlacesState implements NearbyPlacesContract.State, Parcelable {

    private List<PlaceDetails> places;

    private LatLng selectedLoc;

    public PlacesState(List<PlaceDetails> places, LatLng selectedLoc) {
        this.places = places;
        this.selectedLoc = selectedLoc;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.places);
        dest.writeParcelable(this.selectedLoc, flags);
    }

    protected PlacesState(Parcel in) {
        this.places = in.createTypedArrayList(PlaceDetails.CREATOR);
        this.selectedLoc = in.readParcelable(LatLng.class.getClassLoader());
    }

    public static final Creator<PlacesState> CREATOR = new Creator<PlacesState>() {
        @Override
        public PlacesState createFromParcel(Parcel source) {
            return new PlacesState(source);
        }

        @Override
        public PlacesState[] newArray(int size) {
            return new PlacesState[size];
        }
    };

    @Override
    public LatLng getSelectedLoc() {
        return selectedLoc;
    }

    @Override
    public List<PlaceDetails> getPlaces() {
        return places;
    }
}
