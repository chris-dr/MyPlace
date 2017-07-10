package com.drevnitskaya.myplace.ui.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
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

    private static final String SAVED_INSTANSE_STATE_PLACES = "com.drevnitskaya.myplace.saved_instance_state_places";

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_INSTANSE_STATE_PLACES)) {
            getPresenter().onRestoreState((NearbyPlacesContract.State) savedInstanceState.getParcelable(SAVED_INSTANSE_STATE_PLACES));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        writeStateToBundle(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissProgressDialog();
        getPresenter().unsubscribe();
    }

    private void writeStateToBundle(Bundle outState) {
        outState.putParcelable(SAVED_INSTANSE_STATE_PLACES, (Parcelable) getPresenter().getState());
    }

    @Override
    protected void createPresenter() {
        presenter = new NearbyPlacesPresenter(this);
    }

    @Override
    public NearbyPlacesContract.Presenter getPresenter() {
        return presenter;
    }

    public void findNearbyPlaces(LatLng selectedLoc) {
        getPresenter().nearbyPlacesRequest(selectedLoc);
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
