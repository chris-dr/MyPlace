package com.drevnitskaya.myplace.contract;

/**
 * Created by air on 02.07.17.
 */

public class NearbyPlacesContract {

    public interface View extends BasePlacesContract.View {

        String getApiKey();

        void notifyPlaceItemChanged(int position);

        void showProgressDialog();

        void dismissProgressDialog();

        void notifyPlacesRangeChanged(int start, int count);

        boolean isNetworkConnEnabled();

        void showNetworkConnError();

    }

    public interface Presenter extends BasePlacesContract.Presenter {

        void nearbyPlacesRequest(String latitude, String longitude);

        void onFavoritePlaceClicked(int position);

        void unsubscribe();
    }
}
