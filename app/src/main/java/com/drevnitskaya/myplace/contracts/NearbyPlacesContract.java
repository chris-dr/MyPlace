package com.drevnitskaya.myplace.contracts;

import com.drevnitskaya.myplace.model.entities.PlaceDetails;
import com.drevnitskaya.myplace.presenters.base.BaseStatefulPresenter;
import com.drevnitskaya.myplace.states.base.BaseState;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by air on 02.07.17.
 */

public class NearbyPlacesContract {

    public interface View extends BasePlacesContract.View {

        void showProgressDialog();

        void dismissProgressDialog();

        boolean isNetworkConnEnabled();

        void showNetworkConnError();

        void startGeofenceMonitoring();

        void sendAddGeofenceBroadcast();
    }

    public interface State extends BaseState {

        LatLng getSelectedLoc();

        List<PlaceDetails> getPlaces();

    }

    public interface Presenter extends BaseStatefulPresenter<View, State> {

        void nearbyPlacesRequest(LatLng selectedLoc);

        void onFavoritePlaceClicked(int position);

        void onRestoreState(NearbyPlacesContract.State state);

        void unsubscribe();
    }

}
