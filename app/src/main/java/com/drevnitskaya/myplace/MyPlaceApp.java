package com.drevnitskaya.myplace;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.rx.RealmObservableFactory;

/**
 * Created by air on 02.07.17.
 */

public class MyPlaceApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(RealmConfiguration.DEFAULT_REALM_NAME)
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
//                .rxFactory(new RealmObservableFactory())
                .build();
        Realm.setDefaultConfiguration(config);
    }
}
