package com.drevnitskaya.myplace.contract;

import com.drevnitskaya.myplace.presenter.base.BasePresenter;
import com.drevnitskaya.myplace.view.base.BaseView;
import com.google.android.gms.location.FusedLocationProviderClient;

/**
 * Created by air on 30.06.17.
 */

public interface MainContract {

    interface View extends BaseView<Presenter> {

        boolean isGPSEnabled();

        String getApiKey();

    }

    interface Presenter extends BasePresenter {

        void setLocationClient(FusedLocationProviderClient client);

        void getLastKnownLocation();

        void nearbyPlacesRequest(String latitude, String longitude);
    }

}
