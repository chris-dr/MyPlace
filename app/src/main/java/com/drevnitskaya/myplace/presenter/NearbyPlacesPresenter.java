package com.drevnitskaya.myplace.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.drevnitskaya.myplace.contract.NearbyPlacesContract;
import com.drevnitskaya.myplace.model.api.PlacesApiRequest;
import com.drevnitskaya.myplace.model.entities.Place;
import com.drevnitskaya.myplace.model.entities.PlaceDetails;
import com.drevnitskaya.myplace.model.entities.PlaceDetailsResponse;
import com.drevnitskaya.myplace.model.entities.PlaceResponse;
import com.drevnitskaya.myplace.model.entities.WrapperPlace;
import com.drevnitskaya.myplace.presenter.base.BasePlacesPresenter;

import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by air on 02.07.17.
 */

public class NearbyPlacesPresenter extends BasePlacesPresenter implements NearbyPlacesContract.Presenter {

    private static final int RADIUS_PLACES_SEARCHING_METRES = 100;

    private NearbyPlacesContract.View view;
    private List<PlaceDetails> places = new ArrayList<>();
    private Subscription subscription = null;

    public NearbyPlacesPresenter(NearbyPlacesContract.View view) {
        this.view = view;
    }


    public void nearbyPlacesRequest(String latitude, String longitude) {
        if (view.isNetworkConnEnabled()) {
            view.showProgressDialog();
            subscription = PlacesApiRequest.getInstance().getApi().getNearestPlaces(TextUtils.concat(latitude, ", ",
                    longitude).toString(), RADIUS_PLACES_SEARCHING_METRES, view.getApiKey())
                    .filter(new Func1<PlaceResponse, Boolean>() {
                        @Override
                        public Boolean call(PlaceResponse searchResponse) {
                            return TextUtils.equals(searchResponse.getStatus(), PlaceResponse.STATUS_PLACES_RECEIVED);
                        }
                    })
                    .flatMap(new Func1<PlaceResponse, Observable<Place>>() {
                        @Override
                        public Observable<Place> call(PlaceResponse searchResponse) {
                            return Observable.from(searchResponse.getResults());
                        }
                    })
                    .flatMap(new Func1<Place, Observable<PlaceDetailsResponse>>() {
                        @Override
                        public Observable<PlaceDetailsResponse> call(Place place) {
                            return PlacesApiRequest.getInstance().getApi().getPlaceDetails(place.getPlaceId(), view.getApiKey());
                        }
                    })
                    .filter(new Func1<PlaceDetailsResponse, Boolean>() {
                        @Override
                        public Boolean call(PlaceDetailsResponse detailsResponse) {
                            return detailsResponse != null && detailsResponse.getResult() != null;
                        }
                    })
                    .flatMap(new Func1<PlaceDetailsResponse, Observable<PlaceDetails>>() {
                        @Override
                        public Observable<PlaceDetails> call(PlaceDetailsResponse detailsResponse) {
                            PlaceDetails placeDetails = detailsResponse.getResult();
                            return Observable.just(placeDetails);
                        }
                    })
                    .toList()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<PlaceDetails>>() {
                        @Override
                        public void call(List<PlaceDetails> details) {
                            view.dismissProgressDialog();
                            setPlaces(details);
                            wrapPlaces(details);
                            view.notifyPlacesChanged();
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            throwable.printStackTrace();
                            view.dismissProgressDialog();
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            Log.d(getClass().getSimpleName(), "Completed!");
                        }
                    });
        } else {
            view.showNetworkConnError();
        }
    }

    private void setPlaces(List<PlaceDetails> places) {
        this.places = places;
    }

    @Override
    public NearbyPlacesContract.View getView() {
        return view;
    }

    @Override
    public void onFavoritePlaceClicked(int position) {
        final WrapperPlace wrapperPlace = getWrappedPlaces().get(position);
        if (wrapperPlace.isFavorite()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    PlaceDetails favoritePlace = realm.where(PlaceDetails.class)
                            .equalTo("placeId", wrapperPlace.getPlaceDetails().getId())
                            .findFirst();
                    if (favoritePlace != null) {
                        favoritePlace.deleteFromRealm();
                    }
                }
            });
        } else {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.insertOrUpdate(wrapperPlace.getPlaceDetails());
                }
            });
        }
    }

    @Override
    public void favoritePlacesQuery() {
        favoritePlaces = realm.where(PlaceDetails.class)
                .findAll();
        favoritePlaces.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<PlaceDetails>>() {
            @Override
            public void onChange(RealmResults<PlaceDetails> results, OrderedCollectionChangeSet changeSet) {
                wrapPlaces(places);
                view.notifyPlacesChanged();
            }
        });
    }

    public void unsubscribe() {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }
}
