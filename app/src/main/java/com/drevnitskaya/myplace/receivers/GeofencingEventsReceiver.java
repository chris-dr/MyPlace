package com.drevnitskaya.myplace.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import com.drevnitskaya.myplace.R;
import com.drevnitskaya.myplace.model.entities.PlaceDetails;
import com.drevnitskaya.myplace.ui.activities.MainActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by air on 04.07.17.
 */

public class GeofencingEventsReceiver extends BroadcastReceiver {

    public static final String EXTRA_GEOFENCE_NOTIFICATION = "com.drevnitskaya.myplace.extra_geofence_notification";

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event.hasError()) {
            return;
        }

        int transition = event.getGeofenceTransition();
        switch (transition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                List<Geofence> geofences = event.getTriggeringGeofences();
                Realm realm = Realm.getDefaultInstance();
                List<String> names = new ArrayList<>();
                for (Geofence geofence : geofences) {
                    PlaceDetails placeDetails = realm.where(PlaceDetails.class)
                            .equalTo("placeId", geofence.getRequestId())
                            .findFirst();
                    if (placeDetails != null) {
                        names.add(placeDetails.getName());
                    }
                }
                @StringRes
                int title;
                if (geofences.size() > 1) {
                    title = R.string.notification_favoritePlaces;
                } else {
                    title = R.string.notification_favoritePlace;
                }
                String places = TextUtils.join(", ", names);
                showNotification(context, context.getString(title), places);

                break;
            default:
                break;
        }
    }

    public void showNotification(Context ctx, String title, String names) {
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(ctx, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(EXTRA_GEOFENCE_NOTIFICATION, true);
        intent.putExtras(bundle);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(ctx)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(names)
                .setContentIntent(pendingNotificationIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(names))
                .setAutoCancel(true)
                .build();
        notificationManager.notify(0, notification);
    }
}
