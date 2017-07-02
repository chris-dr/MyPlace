package com.drevnitskaya.myplace.contract;

import com.drevnitskaya.myplace.model.entities.Place;
import com.drevnitskaya.myplace.presenter.base.BasePresenter;
import com.drevnitskaya.myplace.view.base.BaseView;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.List;

/**
 * Created by air on 01.07.17.
 */

public class NearbyPlacesContract {

    public interface View extends BaseView<NearbyPlacesContract.Presenter> {
        void setupPlacesList(List<Place> places);
    }

    public interface Presenter extends BasePresenter {

    }
}
