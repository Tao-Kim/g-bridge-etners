package com.gbridge.etners.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

/*
    필요 퍼미션
    ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION

    성공 : Double latitude, Double longitude
    실패 : null, null

    example
    new GpsReciever(Activity.Context){
            @NonNull
            @Override
            protected void onReceive(Double latitude, Double longitude) {
                if(latitude != null && longitude != null){
                    성공 작업
                } else {
                    실패 작업
                }
        };
 */
public abstract class GpsReceiver {
    private int count;

    public GpsReceiver(@NonNull  final Context context){
        count = 3;
        final Handler handler = new Handler();
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    return;
                }

                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location == null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                if (location != null) {
                    GpsReceiver.this.onReceive(location.getLatitude(), location.getLongitude());
                    handler.removeCallbacks(this);
                } else {
                    if(count-- > 0){
                        handler.postDelayed(this, 500);
                    } else {
                        GpsReceiver.this.onReceive(null, null);
                        handler.removeCallbacks(this);
                    }
                }
            }
        });
    }

    @NonNull
    protected abstract void onReceive(Double latitude, Double longitude);
}
