package com.drevnitskaya.myplace.model.entities;

/**
 * Created by air on 02.07.17.
 */

public class WrapperPlace {

    private PlaceDetails placeDetails;

    private boolean isFavorite;

    public PlaceDetails getPlaceDetails() {
        return placeDetails;
    }

    public WrapperPlace() {
    }

    public WrapperPlace(PlaceDetails placeDetails, boolean isFavorite) {
        this.placeDetails = placeDetails;
        this.isFavorite = isFavorite;
    }

    public void setPlace(PlaceDetails placeDetails) {
        this.placeDetails = placeDetails;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
