package com.drevnitskaya.myplace.contracts;

import android.location.Location;

import com.drevnitskaya.myplace.presenters.base.BasePresenter;
import com.drevnitskaya.myplace.view.base.BaseView;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by air on 03.07.17.
 */

public class MapContract {

    public interface View extends BaseView<MainContract.Presenter> {
        boolean isGPSEnabled();

        void loadMap();
    }

    public interface Presenter extends BasePresenter {

        void getLastKnownLocation();

        Location getCurrentLocation();

        void connectGoogleApiClient(int result, GoogleApiClient.Builder builder);
    }
}
