package com.drevnitskaya.myplace.contract;


/**
 * Created by air on 03.07.17.
 */

public interface FavoritePlacesContract {

    interface View extends PlacesContract.View {

        void notifyPlaceRemoved(int position);
    }

    interface Presenter extends PlacesContract.Presenter {

        void disableFavoritePlace(int position);
    }

}
