package com.drevnitskaya.myplace.presenters;

import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;

import com.drevnitskaya.myplace.contracts.MapContract;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by air on 03.07.17.
 */

public class MapPresenter implements MapContract.Presenter {

    private MapContract.View view;
    private LatLng selectedLocation;
    private Geocoder geocoder;

    public MapPresenter(MapContract.View view) {
        this.view = view;
    }

    @Override
    public void setupGeocoder(Geocoder geocoder) {
        this.geocoder = geocoder;
    }

    private Subscription subscription = null;

    private void decodeSelectedAddress() {
        if (subscription != null) {
            subscription.unsubscribe();
        }

        subscription = Observable.defer(new Func0<Observable<LatLng>>() {
            @Override
            public Observable<LatLng> call() {
                return Observable.just(selectedLocation);
            }
        })
                .flatMap(new Func1<LatLng, Observable<List<Address>>>() {
                    @Override
                    public Observable<List<Address>> call(LatLng latLng) {
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return Observable.just(addresses);
                    }
                })
                .filter(new Func1<List<Address>, Boolean>() {
                    @Override
                    public Boolean call(List<Address> addresses) {
                        return addresses != null && !addresses.isEmpty();
                    }
                })
                .flatMap(new Func1<List<Address>, Observable<Address>>() {
                    @Override
                    public Observable<Address> call(List<Address> addresses) {
                        return Observable.from(addresses);
                    }
                })
                .first()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Address>() {
                    @Override
                    public void call(Address address) {
                        List<String> addressLines = new ArrayList<>();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            addressLines.add(address.getAddressLine(i));
                        }
                        String fullAddress = TextUtils.join(", ", addressLines);
                        String country = address.getCountryName();
                        if (!TextUtils.isEmpty(fullAddress)) {
                            view.setSelectedAddress(fullAddress);
                        } else {
                            view.setSelectedAddress(country);
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        view.showDecodingError();
                    }
                });
    }

    public LatLng getSelectedLocation() {
        return selectedLocation;
    }

    public void manageSelectedLocation(LatLng selectedLocation) {
        this.selectedLocation = selectedLocation;
        decodeSelectedAddress();
    }

    @Override
    public void unsubscribe() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    @Override
    public void initRealm() {

    }

    @Override
    public void closeRealm() {

    }
}
