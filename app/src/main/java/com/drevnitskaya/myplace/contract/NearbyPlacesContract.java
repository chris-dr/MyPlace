package com.drevnitskaya.myplace.contract;

/**
 * Created by air on 02.07.17.
 */

public class NearbyPlacesContract {

    public interface View extends PlacesContract.View {

        String getApiKey();

        void notifyPlaceItemChanged(int position);

        void showProgressDialog();

        void dismissProgressDialog();

        void notifyPlacesRangeChanged(int start, int count);
    }

    public interface Presenter extends PlacesContract.Presenter {

        void nearbyPlacesRequest(String latitude, String longitude);

        void onFavoritePlaceClicked(int position);
    }
}
