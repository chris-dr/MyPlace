package com.drevnitskaya.myplace.contract;

import com.drevnitskaya.myplace.model.entities.PlaceDetails;
import com.drevnitskaya.myplace.model.entities.WrapperPlace;
import com.drevnitskaya.myplace.presenter.base.BasePresenter;
import com.drevnitskaya.myplace.view.base.BaseView;

import java.util.List;


/**
 * Created by air on 01.07.17.
 */

public class PlacesContract {

    public interface View extends BaseView<PlacesContract.Presenter> {
        void notifyPlacesChanged();

        void setupPlacesRecycler(List<WrapperPlace> places);
    }

    public interface Presenter extends BasePresenter {
        List<WrapperPlace> wrapPlaces(List<PlaceDetails> places);

        List<WrapperPlace> getWrappedPlaces();

        void favoritePlacesQuery();
    }
}
