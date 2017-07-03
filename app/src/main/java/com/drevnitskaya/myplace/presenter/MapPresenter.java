package com.drevnitskaya.myplace.presenter;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.drevnitskaya.myplace.contract.MapContract;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by air on 03.07.17.
 */

public class MapPresenter implements MapContract.Presenter, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private MapContract.View view;
    private GoogleApiClient googleApiClient;
    private Location currentLocation;

    public MapPresenter(MapContract.View view) {
        this.view = view;
    }

    public void connectGoogleApiClient(int result, GoogleApiClient.Builder builder) {
        if (result == ConnectionResult.SUCCESS) {
            googleApiClient = builder
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            googleApiClient.connect();
        } else {
            view.loadMap();
        }

    }

    @Override
    public void getLastKnownLocation() {
        Observable.create(new Observable.OnSubscribe<Location>() {
            @Override
            public void call(final Subscriber<? super Location> subscriber) {
                if (view.isGPSEnabled()) {
                    try {
                        subscriber.onNext(LocationServices.FusedLocationApi.getLastLocation(googleApiClient));
                        subscriber.onCompleted();

                    } catch (SecurityException ex) {
                        subscriber.onError(ex);
                    }
                } else {
                    subscriber.onError(new Throwable("GPS is off!"));
                }
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Location>() {
                    @Override
                    public void call(Location location) {
                        currentLocation = location;
                        view.loadMap();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        view.loadMap();
                    }
                });
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }


    @Override
    public void initRealm() {

    }

    @Override
    public void closeRealm() {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLastKnownLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(getClass().getSimpleName(), "Google API client connection was suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(getClass().getSimpleName(), "Google API client connection was failed");
        view.loadMap();
    }
}
