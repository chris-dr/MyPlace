package com.drevnitskaya.myplace.presenter;

import com.drevnitskaya.myplace.contract.NearbyPlacesContract;

/**
 * Created by air on 01.07.17.
 */

public class NearbyPlacesPresenter implements NearbyPlacesContract.Presenter {

    private NearbyPlacesContract.View view;


    public NearbyPlacesPresenter(NearbyPlacesContract.View view) {
        this.view = view;
    }

}
