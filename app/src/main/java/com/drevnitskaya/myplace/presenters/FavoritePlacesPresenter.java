package com.drevnitskaya.myplace.presenters;

import com.drevnitskaya.myplace.R;
import com.drevnitskaya.myplace.contracts.FavoritePlacesContract;
import com.drevnitskaya.myplace.model.entities.PlaceDetails;
import com.drevnitskaya.myplace.presenters.base.BasePlacesPresenter;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by air on 03.07.17.
 */

public class FavoritePlacesPresenter extends BasePlacesPresenter implements FavoritePlacesContract.Presenter {

    private FavoritePlacesContract.View view;

    public FavoritePlacesPresenter(FavoritePlacesContract.View view) {
        this.view = view;
    }

    @Override
    public FavoritePlacesContract.View getView() {
        return view;
    }

    public void favoritePlacesQuery() {
        favoritePlaces = realm.where(PlaceDetails.class)
                .findAll();
        favoritePlaces.addChangeListener(changeListener);
        wrapPlaces(favoritePlaces);
        view.setupPlacesRecycler();
    }

    private RealmChangeListener<RealmResults<PlaceDetails>> changeListener = new RealmChangeListener<RealmResults<PlaceDetails>>() {
        @Override
        public void onChange(RealmResults<PlaceDetails> places) {
            wrapPlaces(favoritePlaces);
            getView().notifyPlacesChanged();
            setupInfoMsg();
        }
    };

    @Override
    public void disableFavoritePlace(final int position) {
        final PlaceDetails place = getWrappedPlaces().get(position).getPlaceDetails();
        favoritePlaces.removeChangeListener(changeListener);
        view.sendRemoveGeofenceBroadcast(place.getPlaceId());
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                place.deleteFromRealm();
            }
        });
        getWrappedPlaces().remove(position);
        getView().notifyPlaceRemoved(position);
        favoritePlaces.addChangeListener(changeListener);
        setupInfoMsg();
    }

    @Override
    public void setupInfoMsg() {
        if (favoritePlaces == null || favoritePlaces.isEmpty()) {
            view.setInfoMsgText(R.string.favorite_noFavoritePlaces);
        } else {
            view.hideInfoMsg();
        }
    }

}
