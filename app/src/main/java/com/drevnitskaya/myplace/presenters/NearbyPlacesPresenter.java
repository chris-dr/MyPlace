package com.drevnitskaya.myplace.presenters;

import android.text.TextUtils;

import com.drevnitskaya.myplace.BuildConfig;
import com.drevnitskaya.myplace.R;
import com.drevnitskaya.myplace.contracts.NearbyPlacesContract;
import com.drevnitskaya.myplace.model.api.PlacesApiRequest;
import com.drevnitskaya.myplace.model.entities.Location;
import com.drevnitskaya.myplace.model.entities.Place;
import com.drevnitskaya.myplace.model.entities.PlaceDetails;
import com.drevnitskaya.myplace.model.entities.PlaceDetailsResponse;
import com.drevnitskaya.myplace.model.entities.PlaceResponse;
import com.drevnitskaya.myplace.model.entities.WrapperPlace;
import com.drevnitskaya.myplace.presenters.base.BasePlacesPresenter;
import com.drevnitskaya.myplace.states.PlacesState;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
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
    private LatLng selectedLocation = null;

    public NearbyPlacesPresenter(NearbyPlacesContract.View view) {
        this.view = view;
    }

    public void nearbyPlacesRequest(LatLng selectedLoc) {
        if (view.isNetworkConnEnabled()) {
            selectedLocation = selectedLoc;
            view.showProgressDialog();
            subscription = PlacesApiRequest.getInstance().getApi().getNearestPlaces(TextUtils.concat(String.valueOf(selectedLoc.latitude), ", ",
                    String.valueOf(selectedLoc.longitude)).toString(), RADIUS_PLACES_SEARCHING_METRES,
                    String.valueOf(BuildConfig.GOOGLE_MAPS_API_KEY))
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
                            return PlacesApiRequest.getInstance().getApi().getPlaceDetails(place.getPlaceId(),
                                    String.valueOf(BuildConfig.GOOGLE_MAPS_API_KEY));
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
                            if (places.isEmpty()) {
                                view.setInfoMsgText(R.string.nearby_placesNotFound);
                            } else {
                                view.hideInfoMsg();
                            }
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            throwable.printStackTrace();
                            view.dismissProgressDialog();
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
        final PlaceDetails place = wrapperPlace.getPlaceDetails();
        if (wrapperPlace.isFavorite()) {
            view.sendRemoveGeofenceBroadcast(place.getPlaceId());
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    PlaceDetails favoritePlace = realm.where(PlaceDetails.class)
                            .equalTo("placeId", wrapperPlace.getPlaceDetails().getPlaceId())
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
                    realm.insertOrUpdate(place);
                }
            });
            Location location = place.getGeometry().getLocation();
            view.sendAddGeofenceBroadcast(place.getPlaceId(), location.getLatitude(), location.getLongitude());
            view.startGeofenceMonitoring();
        }
    }

    @Override
    public void favoritePlacesQuery() {
        favoritePlaces = realm.where(PlaceDetails.class)
                .findAll();
        favoritePlaces.addChangeListener(new RealmChangeListener<RealmResults<PlaceDetails>>() {
            @Override
            public void onChange(RealmResults<PlaceDetails> results) {
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

    @Override
    public void setupInfoMsg() {
        if (selectedLocation == null && getWrappedPlaces().isEmpty()) {
            view.setInfoMsgText(R.string.nearby_locNotSelected);
        } else if (selectedLocation != null && getWrappedPlaces().isEmpty()) {
            view.setInfoMsgText(R.string.nearby_placesNotFound);
        } else {
            view.hideInfoMsg();
        }
    }

    @Override
    public NearbyPlacesContract.State getState() {
        return new PlacesState(places, selectedLocation);
    }

    public void onRestoreState(NearbyPlacesContract.State state) {
        if (state != null) {
            selectedLocation = state.getSelectedLoc();
            places = state.getPlaces();
            wrapPlaces(places);
            view.notifyPlacesChanged();
            setupInfoMsg();
        }
    }
}

