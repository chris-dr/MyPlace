package com.drevnitskaya.myplace.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.drevnitskaya.myplace.R;
import com.drevnitskaya.myplace.contract.NearbyPlacesContract;
import com.drevnitskaya.myplace.model.entities.PlaceDetails;
import com.drevnitskaya.myplace.presenter.NearbyPlacesPresenter;
import com.drevnitskaya.myplace.ui.fragments.base.BasePlacesFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


/**
 * Created by air on 02.07.17.
 */

public class NearbyPlacesFragment extends BasePlacesFragment implements NearbyPlacesContract.View {
    private NearbyPlacesContract.Presenter presenter;
    private ProgressDialog dialogProgress;

    public static NearbyPlacesFragment newInstance() {
        Bundle args = new Bundle();
        NearbyPlacesFragment fragment = new NearbyPlacesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_places_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPresenter().favoritePlacesQuery();
        //todo быдлокод!
        setupPlacesRecycler(getPresenter().wrapPlaces(new ArrayList<PlaceDetails>()));
    }

    @Override
    public NearbyPlacesContract.Presenter getPresenter() {
        return presenter;
    }

    @Override
    public void onStop() {
        super.onStop();
        dismissProgressDialog();
    }

    @Override
    protected void createPresenter() {
        presenter = new NearbyPlacesPresenter(this);
    }

    @Override
    public String getApiKey() {
        return getString(R.string.apiKey_googleMaps);
    }

    @Override
    public void notifyPlaceItemChanged(int position) {
        adapterPlaces.notifyItemChanged(position);
    }

    public void getNearbyPlaces(LatLng selectedLoc) {
        getPresenter().nearbyPlacesRequest(String.valueOf(selectedLoc.latitude), String.valueOf(selectedLoc.longitude));
    }

    @Override
    protected void manageOnFavoriteClick(int position) {
        getPresenter().onFavoritePlaceClicked(position);
    }

    public void showProgressDialog() {
        if (dialogProgress == null) {
            dialogProgress = ProgressDialog.show(getContext(), "", "Please wait", true);
        }
    }

    public void dismissProgressDialog() {
        if (dialogProgress != null && dialogProgress.isShowing()) {
            dialogProgress.dismiss();
        }
    }

    @Override
    public void notifyPlacesRangeChanged(int start, int count) {
        adapterPlaces.notifyItemRangeChanged(start, count);
    }
}
