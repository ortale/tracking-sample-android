package br.com.rodolfoortale.myroutes.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

import br.com.rodolfoortale.myroutes.R;
import br.com.rodolfoortale.myroutes.controller.JourneysController;
import br.com.rodolfoortale.myroutes.controller.PositionsController;
import br.com.rodolfoortale.myroutes.model.Journey;
import br.com.rodolfoortale.myroutes.model.Position;
import br.com.rodolfoortale.myroutes.view.activities.MapsActivity;

/**
 * Local Service responsible to store locations from a specific journey in database while
 * app is in background.
 */

public class LocalService extends Service {
    private static final String TAG = LocalService.class.getSimpleName();
    private static final int NOTIFICATION = 100;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 1f;
    private NotificationManager notificationManager;
    private LocationListener locationListener;
    private LocationManager locationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Starts Notification Service.
     * Shows notification.
     * Starts location updates and send the callback data to the Receiver via Broadcast.
     */
    @Override
    public void onCreate() {

        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        showNotification();

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                PositionsController positionsController = new PositionsController(LocalService.this);

                JourneysController journeysController = new JourneysController(LocalService.this);
                Journey journey = journeysController.getLast();

                Position position = new Position();
                position.setJourney(journey);
                position.setLatitude(location.getLatitude());
                position.setLongitude(location.getLongitude());

                positionsController.createNew(position);

                Intent intent = new Intent(getString(R.string.key_location_update));
                intent.putExtra(getString(R.string.key_latitude), location.getLatitude());
                intent.putExtra(getString(R.string.key_longitude), location.getLongitude());
                sendBroadcast(intent);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,LOCATION_INTERVAL,LOCATION_DISTANCE, locationListener);

    }

    /**
     * Stops requesting location updates and cancel notification.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null){
            locationManager.removeUpdates(locationListener);
        }

        notificationManager.cancel(NOTIFICATION);
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, MapsActivity.class), 0);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(getString(R.string.txt_notification))
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getText(R.string.app_name))
                .setContentText(getString(R.string.txt_notification))
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .build();

        notificationManager.notify(NOTIFICATION, notification);
    }
}