package com.drevnitskaya.myplace.presenter;

import com.drevnitskaya.myplace.contract.FavoritePlacesContract;
import com.drevnitskaya.myplace.model.entities.PlaceDetails;
import com.drevnitskaya.myplace.presenter.base.BasePlacesPresenter;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
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

    private OrderedRealmCollectionChangeListener<RealmResults<PlaceDetails>> changeListener = new OrderedRealmCollectionChangeListener<RealmResults<PlaceDetails>>() {
        @Override
        public void onChange(RealmResults<PlaceDetails> places, OrderedCollectionChangeSet changeSet) {
            OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
            if (insertions.length >= 1) {
                wrapPlaces(favoritePlaces);
                getView().notifyPlacesChanged();
            }
        }
    };

    @Override
    public void disableFavoritePlace(final int position) {
        final PlaceDetails place = getWrappedPlaces().get(position).getPlaceDetails();
        favoritePlaces.removeChangeListener(changeListener);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                PlaceDetails favoritePlace = favoritePlaces.where()
                        .equalTo("placeId", place.getId())
                        .findFirst();
                if (favoritePlace != null) {
                    getWrappedPlaces().remove(position);
                    getView().notifyPlaceRemoved(position);
                    favoritePlace.deleteFromRealm();
                }
            }
        });
        favoritePlaces.addChangeListener(changeListener);
    }
}
