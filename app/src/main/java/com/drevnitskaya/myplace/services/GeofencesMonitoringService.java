package com.drevnitskaya.myplace.services;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.drevnitskaya.myplace.model.entities.Location;
import com.drevnitskaya.myplace.model.entities.PlaceDetails;
import com.drevnitskaya.myplace.receivers.GeofencingEventsReceiver;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

/**
 * Created by air on 04.07.17.
 */

public class GeofencesMonitoringService extends Service {

    private static final float GEOFENCE_RADIUS_METERS = 100;
    private static final int REQUEST_CODE_GEOFENCE = 200;

    public static final String ACTION_ADD_MONITORING_GEOFENCE = "com.drevnitskaya.myplace.action_add_monitoring_geofence";
    public static final String ACTION_REMOVE_MONITORING_GEOFENCE = "com.drevnitskaya.myplace.action_remove_monitoring_geofence";

    public static final String EXTRA_PLACE_ID = "com.drevnitskaya.myplace.extra_place_id";
    public static final String EXTRA_PLACE_LATITUDE = "com.drevnitskaya.myplace.extra_place_latitude";
    public static final String EXTRA_PLACE_LONGITUDE = "com.drevnitskaya.myplace.extra_place_longitude";

    private GoogleApiClient googleApiClient;
    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;
    private Realm realm;
    private RealmResults<PlaceDetails> detailsResults;
    private List<Geofence> geofences = new ArrayList<>();


    private ManageGeofencesReceiver receiver = new ManageGeofencesReceiver();

    @Override
    public void onCreate() {
        super.onCreate();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopSelf();
            return;
        }
        registerReceiver(receiver, getIntentFilter());

        realm = Realm.getDefaultInstance();
        detailsResults = realm.where(PlaceDetails.class)
                .findAll();
        connectGoogleApiClient();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void connectGoogleApiClient() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result == ConnectionResult.SUCCESS) {
            if (googleApiClient == null) {
                googleApiClient = new GoogleApiClient.Builder(this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(@Nullable Bundle bundle) {
                                setupGeofences();

                            }

                            @Override
                            public void onConnectionSuspended(int i) {

                            }
                        })
                        .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                Log.e(getClass().getSimpleName(), "Google Api Client connection failed");
                            }
                        })
                        .build();
            }
            googleApiClient.connect();
        } else {
            stopSelf();
        }
    }

    private void setupGeofences() {
        geofences.clear();
        if (!detailsResults.isEmpty()) {
            for (PlaceDetails place : detailsResults) {
                Location location = place.getGeometry().getLocation();
                geofences.add(buildGeofence(place.getPlaceId(), location.getLatitude(), location.getLongitude()));
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (geofencingClient == null) {
                    geofencingClient = LocationServices.getGeofencingClient(this);
                }

                addGeofences();

            }
        }
    }

    private void addGeofences() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            geofencingClient.addGeofences(
                    getGeofencingRequest(),
                    createGeofencePendingIntent());
        }
    }

    private Geofence buildGeofence(String placeId, double latitude, double longitude) {
        return new Geofence.Builder()
                .setRequestId(placeId)
                .setCircularRegion(latitude, longitude, GEOFENCE_RADIUS_METERS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setNotificationResponsiveness(15000)
                .build();
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofences);

        return builder.build();
    }

    private PendingIntent createGeofencePendingIntent() {
        if (geofencePendingIntent == null) {
            Intent intent = new Intent(this, GeofencingEventsReceiver.class);
            geofencePendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE_GEOFENCE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        return geofencePendingIntent;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receiver);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        if (realm != null) {
            try {
                realm.close();
            } catch (RealmException ex) {
                ex.printStackTrace();
            }
        }
    }

    class ManageGeofencesReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String placeId = intent.getStringExtra(EXTRA_PLACE_ID);
            switch (action) {
                case ACTION_ADD_MONITORING_GEOFENCE:
                    double latitude = intent.getDoubleExtra(EXTRA_PLACE_LATITUDE, 0);
                    double longitude = intent.getDoubleExtra(EXTRA_PLACE_LONGITUDE, 0);

                    geofences.add(buildGeofence(placeId, latitude, longitude));
                    addGeofences();
                    break;
                case ACTION_REMOVE_MONITORING_GEOFENCE:

                    Geofence removedGeo = null;
                    for (Geofence geofence : geofences) {
                        if (TextUtils.equals(placeId, geofence.getRequestId())) {
                            removedGeo = geofence;
                        }
                    }

                    if (removedGeo != null) {
                        geofences.remove(removedGeo);
                    }

                    geofencingClient.removeGeofences(geofencePendingIntent)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    if (geofences.isEmpty()) {
                                        stopSelf();
                                    } else {
                                        addGeofences();
                                    }
                                }
                            });
                    break;
                default:
                    break;
            }
        }
    }

    private IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_ADD_MONITORING_GEOFENCE);
        intentFilter.addAction(ACTION_REMOVE_MONITORING_GEOFENCE);

        return intentFilter;
    }
}
