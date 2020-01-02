package com.example.eindopdracht_client_side_development_app.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class LocationAPIManager
{
    private static LocationAPIManager instance;

    private Activity activity;
    private FusedLocationProviderClient fushedLocationProviderClient;

    private LocationAPIListener locationAPIListener;

    private LocationAPIManager(Activity activity)
    {
        this.activity = activity;

        checkLocationPermissions();
    }

    private boolean hasLocationAccess()
    {
        boolean courceLocationAccess = ActivityCompat.checkSelfPermission(this.activity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean fineLocationAccess = ActivityCompat.checkSelfPermission(this.activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        return (courceLocationAccess && fineLocationAccess);
    }

    private void checkLocationPermissions()
    {
        if (!hasLocationAccess())
            ActivityCompat.requestPermissions(this.activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        else
            setupLocationServices();
    }

    private void setupLocationServices()
    {
        this.fushedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.activity.getApplicationContext());
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000).setFastestInterval(5000).setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationCallback locationCallback = new LocationCallback()
        {
            @Override
            public void onLocationResult(LocationResult locationResult)
            {
                if (locationResult == null)
                    return;

                if(locationAPIListener != null)
                {
                    for (Location location : locationResult.getLocations())
                        if (location != null)
                            locationAPIListener.onLocationReceived(new LatLng(location.getLatitude(), location.getLongitude()));
                }
            }
        };
        this.fushedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

//    public void setCurrentActivity(Activity activity)
//    {
//        if(activity != null)
//            this.activity = activity;
//    }

    public void setLocationAPIListener(LocationAPIListener locationAPIListener)
    {
        this.locationAPIListener = locationAPIListener;
    }

    public static LocationAPIManager getInstance(Activity activity)
    {
        if(instance == null)
            instance = new LocationAPIManager(activity);
        return instance;
    }

    public static LocationAPIManager getInstance()
    {
        return instance;
    }
}