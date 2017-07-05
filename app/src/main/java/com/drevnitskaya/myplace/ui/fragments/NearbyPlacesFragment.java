package com.drevnitskaya.myplace.ui.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.drevnitskaya.myplace.R;
import com.drevnitskaya.myplace.contracts.NearbyPlacesContract;
import com.drevnitskaya.myplace.presenters.NearbyPlacesPresenter;
import com.drevnitskaya.myplace.services.GeofencesMonitoringService;
import com.drevnitskaya.myplace.ui.fragments.base.BasePlacesFragment;
import com.google.android.gms.maps.model.LatLng;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.drevnitskaya.myplace.services.GeofencesMonitoringService.ACTION_ADD_MONITORING_GEOFENCE;
import static com.drevnitskaya.myplace.services.GeofencesMonitoringService.EXTRA_PLACE_ID;
import static com.drevnitskaya.myplace.services.GeofencesMonitoringService.EXTRA_PLACE_LATITUDE;
import static com.drevnitskaya.myplace.services.GeofencesMonitoringService.EXTRA_PLACE_LONGITUDE;


/**
 * Created by air on 02.07.17.
 */

public class NearbyPlacesFragment extends BasePlacesFragment implements NearbyPlacesContract.View {
    private NearbyPlacesContract.Presenter presenter;
    private ProgressDialog dialogProgress;

    public static NearbyPlacesFragment newInstance() {
        NearbyPlacesFragment fragment = new NearbyPlacesFragment();
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
        setupPlacesRecycler();
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
    public void onDestroyView() {
        super.onDestroyView();
        getPresenter().unsubscribe();
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
            dialogProgress = new ProgressDialog(getContext());
            dialogProgress.setMessage(getString(R.string.progressDialog_wait));
            dialogProgress.setCancelable(false);
        }
        dialogProgress.show();
    }

    public void dismissProgressDialog() {
        if (dialogProgress != null) {
            dialogProgress.dismiss();
        }
    }

    @Override
    public void notifyPlacesRangeChanged(int start, int count) {
        adapterPlaces.notifyItemRangeChanged(start, count);
    }

    @Override
    public boolean isNetworkConnEnabled() {
        ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    @Override
    public void showNetworkConnError() {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.alertDialog_titleError)
                .setMessage(R.string.alertDialog_errorMsg)
                .create();
        dialog.show();
    }

    public void startGeofenceMonitoring() {
        Intent intent = new Intent(getContext(), GeofencesMonitoringService.class);
        getContext().startService(intent);
    }

    public void sendAddGeofenceBroadcast(String placeId, double latitude, double longitude) {
        Intent intent = new Intent();
        intent.setAction(ACTION_ADD_MONITORING_GEOFENCE);
        intent.putExtra(EXTRA_PLACE_ID, placeId);
        intent.putExtra(EXTRA_PLACE_LATITUDE, latitude);
        intent.putExtra(EXTRA_PLACE_LONGITUDE, longitude);
        getContext().sendBroadcast(intent);

    }
}
