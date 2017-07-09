package com.drevnitskaya.myplace.contracts;

import android.location.Geocoder;

import com.drevnitskaya.myplace.presenters.base.BasePresenter;
import com.drevnitskaya.myplace.view.base.BaseView;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by air on 03.07.17.
 */

public class MapContract {

    public interface View extends BaseView {

        void setSelectedAddress(String selectedAddress);

        void showDecodingError();
    }

    public interface Presenter extends BasePresenter {

        void setupGeocoder(Geocoder geocoder);

        LatLng getSelectedLocation();

        void manageSelectedLocation(LatLng selectedLocation);

        void unsubscribe();
    }
}
