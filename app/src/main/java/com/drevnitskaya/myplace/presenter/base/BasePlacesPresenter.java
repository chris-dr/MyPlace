package com.drevnitskaya.myplace.presenter.base;

import com.drevnitskaya.myplace.contract.BasePlacesContract;
import com.drevnitskaya.myplace.model.entities.PlaceDetails;
import com.drevnitskaya.myplace.model.entities.WrapperPlace;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

/**
 * Created by air on 01.07.17.
 */

public abstract class BasePlacesPresenter implements BasePlacesContract.Presenter {

    protected Realm realm;
    private List<WrapperPlace> wrappedPlaces = new ArrayList<>();
    protected RealmResults<PlaceDetails> favoritePlaces;

    public abstract BasePlacesContract.View getView();

    public void initRealm() {
        realm = Realm.getDefaultInstance();
    }

    public void closeRealm() {
        try {
            realm.removeAllChangeListeners();
            realm.close();
        } catch (RealmException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<WrapperPlace> wrapPlaces(List<PlaceDetails> places) {
        wrappedPlaces.clear();
        for (PlaceDetails place : places) {
            long count = realm.where(PlaceDetails.class)
                    .equalTo("placeId", place.getId())
                    .count();
            if (count == 1) {
                wrappedPlaces.add(new WrapperPlace(place, true));
            } else {
                wrappedPlaces.add(new WrapperPlace(place, false));
            }

        }
        return wrappedPlaces;
    }

    public List<WrapperPlace> getWrappedPlaces() {
        return wrappedPlaces;
    }

    public abstract void favoritePlacesQuery();

}
