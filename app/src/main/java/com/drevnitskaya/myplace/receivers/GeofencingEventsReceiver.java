package com.drevnitskaya.myplace.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

                String title;
                if (geofences.size() > 1) {
                    title = "Favorite places are nearby!";
                } else {
                    title = "Favorite place is nearby!";
                }
                String places = TextUtils.join(", ", names);
                showNotification(context, title, places);

                break;
            default:
                break;
        }
    }

    public void showNotification(Context ctx, String title, String names) {
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        //todo open favorites set launche mode at manifest!
        Intent intent = new Intent(ctx, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
