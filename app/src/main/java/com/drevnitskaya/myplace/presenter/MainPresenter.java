package com.drevnitskaya.myplace.presenter;

import com.drevnitskaya.myplace.contract.MainContract;

import io.realm.Realm;
import io.realm.exceptions.RealmException;

/**
 * Created by air on 30.06.17.
 */

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View view;
    private Realm realm;

    public MainPresenter(MainContract.View view) {
        this.view = view;
    }

    @Override
    public void initRealm() {
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void closeRealm() {
        try {
            realm.close();
        } catch (RealmException ex) {
            ex.printStackTrace();
        }
    }
}
