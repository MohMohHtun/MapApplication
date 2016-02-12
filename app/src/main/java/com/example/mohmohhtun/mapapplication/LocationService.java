package com.example.mohmohhtun.mapapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

/**
 * Created by mohmohhtun on 2/2/16.
 */
public class LocationService extends Service implements LocationListener{
    private static int notificationID = 0;



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    @Override
    public void onLocationChanged(Location location) {
            sendNotification("asfasf","fasfs");
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void sendNotification(String message, String extra) {
        Intent intent = new Intent(this, Main.class);
        intent.putExtra("Message",message);
        intent.putExtra("Extra",extra);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);



        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("location APP")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri).setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(++notificationID, notificationBuilder.build());
    }
}
