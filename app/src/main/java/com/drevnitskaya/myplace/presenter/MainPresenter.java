package com.drevnitskaya.myplace.presenter;

import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import com.drevnitskaya.myplace.contract.MainContract;
import com.drevnitskaya.myplace.model.api.PlacesApiRequest;
import com.drevnitskaya.myplace.model.entities.PlaceResponce;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by air on 30.06.17.
 */

public class MainPresenter implements MainContract.Presenter {

    private static final int RADIUS_PLACES_SEARCHING_METRES = 10000;

    private MainContract.View view;
    private FusedLocationProviderClient fusedLocationClient;

    public MainPresenter(MainContract.View view) {
        this.view = view;
    }

    public void setLocationClient(FusedLocationProviderClient client) {
        this.fusedLocationClient = client;
    }

    public void getLastKnownLocation() {
        Observable.create(new Observable.OnSubscribe<Location>() {
            @Override
            public void call(final Subscriber<? super Location> subscriber) {
                if (view.isGPSEnabled()) {
                    try {
                        fusedLocationClient.getLastLocation()
                                .addOnSuccessListener(new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        subscriber.onNext(location);
                                        subscriber.onCompleted();
                                    }
                                });
                    } catch (SecurityException ex) {
                        subscriber.onError(ex);
                    }
                } else {
                    subscriber.onError(new Throwable("GPS is off!"));
                }
            }
        })
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<Location>() {
                    @Override
                    public void call(Location location) {
                        if (location != null) {
                            nearbyPlacesRequest(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                            Log.d(getClass().getSimpleName(), "Location is null!!");
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    public void nearbyPlacesRequest(String latitude, String longitude) {
        PlacesApiRequest.getInstance().getApi().getNearestPlaces(TextUtils.concat(latitude, ", ",
                longitude).toString(), RADIUS_PLACES_SEARCHING_METRES, view.getApiKey())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<PlaceResponce>() {
                    @Override
                    public void call(PlaceResponce placeResponce) {
                        Log.d(getClass().getSimpleName(), "Received places: " + placeResponce);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }
}
