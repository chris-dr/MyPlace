package com.drevnitskaya.myplace.contracts;

import com.drevnitskaya.myplace.model.entities.PlaceDetails;
import com.drevnitskaya.myplace.model.entities.WrapperPlace;
import com.drevnitskaya.myplace.presenters.base.BasePresenter;
import com.drevnitskaya.myplace.view.base.BaseView;

import java.util.List;


/**
 * Created by air on 01.07.17.
 */

public class BasePlacesContract {

    public interface View extends BaseView<BasePlacesContract.Presenter> {
        void notifyPlacesChanged();

        void setupPlacesRecycler();

        void sendRemoveGeofenceBroadcast(String placeId);
    }

    public interface Presenter extends BasePresenter {
        List<WrapperPlace> wrapPlaces(List<PlaceDetails> places);

        List<WrapperPlace> getWrappedPlaces();

        void favoritePlacesQuery();
    }
}
