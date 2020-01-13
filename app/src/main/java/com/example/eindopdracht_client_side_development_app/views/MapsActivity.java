package com.example.eindopdracht_client_side_development_app.views;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;


import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.example.eindopdracht_client_side_development_app.R;
import com.example.eindopdracht_client_side_development_app.models.McDonalds;
import com.example.eindopdracht_client_side_development_app.util.DirectionsAPIListener;
import com.example.eindopdracht_client_side_development_app.util.DirectionsAPIManager;
import com.example.eindopdracht_client_side_development_app.util.LocationAPIListener;
import com.example.eindopdracht_client_side_development_app.util.LocationAPIManager;
import com.example.eindopdracht_client_side_development_app.util.MapUtils;
import com.example.eindopdracht_client_side_development_app.util.animations.ShakeAnimationSequence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationAPIListener, DirectionsAPIListener, McDonaldsFragment.OnFragmentInteractionListener
{
    private GoogleMap googleMap;
    private LocationAPIManager locationAPIManager;
    private DirectionsAPIManager directionsAPIManager;

    private McDonalds mcDonalds;

    private Polyline fullRoute;
    private Marker marker;
    private Circle circle;
    private Polyline walkedRoute;

    private final int NOTIFY_RANGE = 100;
    private final int NOTIFY_VIBRATION_TIME_MILLISECONDS = 500;
    private boolean isInRange;

    private McDonaldsFragment mcDonaldsFragment;
    private FragmentManager fragmentManager;
    private ConstraintLayout mcDonaldsLayout;

    private boolean markerIsClicked;
    private ShakeAnimationSequence shakeAnimationSequence;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        this.mcDonalds = (McDonalds)getIntent().getSerializableExtra("mcdonalds");
        this.fullRoute = null;
        this.marker = null;
        this.circle = null;
        this.walkedRoute = null;

        this.isInRange = false;

        this.locationAPIManager = LocationAPIManager.getInstance();
        this.locationAPIManager.setLocationAPIListener(this);
        this.directionsAPIManager = new DirectionsAPIManager(this, this);
        //this.directionsAPIManager.requestRoute(this.locationAPIManager.getLastLocation(), mcDonalds);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.fragmentManager = getSupportFragmentManager();
        this.mcDonaldsFragment = (McDonaldsFragment)this.fragmentManager.findFragmentById(R.id.frg_McDonalds);
        this.mcDonaldsFragment.setMcDonalds(this.mcDonalds);

        this.mcDonaldsLayout = findViewById(R.id.cns_McDonaldsFragmentLayout);
        this.mcDonaldsLayout.setVisibility(View.GONE);

        this.markerIsClicked = false;
        this.shakeAnimationSequence = new ShakeAnimationSequence(this.mcDonaldsLayout, -2, 2, 600, 4);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        this.googleMap = googleMap;
        this.googleMap.setMyLocationEnabled(true);
        this.directionsAPIManager.requestRoute(this.locationAPIManager.getLastLocation(), mcDonalds);

        this.googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker)
            {
                markerIsClicked = true;
                mcDonaldsLayout.setVisibility(View.VISIBLE);

                shakeAnimationSequence.start();

                //fragmentManager.beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).show(mcDonaldsFragment).commit();
                //fragmentManager.beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).hide(mcDonaldsFragment).commit();
                return false;
            }
        });

        this.googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng)
            {
                mcDonaldsLayout.setVisibility(View.GONE);
            }
        });

        this.googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle()
            {
                if(markerIsClicked)
                    markerIsClicked = false;
            }
        });

        this.googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove()
            {
                if(!markerIsClicked)
                    mcDonaldsLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onLocationReceived(LatLng location)
    {
        Log.d("onLocationReceived", "Location: " + location.latitude + " : " + location.longitude);
        if(this.marker != null)
        {
            if(this.walkedRoute != null)
                this.walkedRoute.remove();

            Pair<LatLng, LatLng> closestLine = MapUtils.getClosestLine((ArrayList<LatLng>)this.fullRoute.getPoints(), location);

            if(closestLine != null)
            {
                ArrayList<LatLng> locationsBefore = getLocationsBefore(closestLine.first);
                LatLng closestPointOnLine = MapUtils.projectPoint(closestLine.first, closestLine.second, location);

                locationsBefore.add(closestPointOnLine);
                PolylineOptions polylineOptions = new PolylineOptions().clickable(false).addAll(locationsBefore);
                polylineOptions.color(Color.argb(1.0f, 1.0f, 0.0f, 0.0f)).width(20);
                this.walkedRoute = this.googleMap.addPolyline(polylineOptions);
            }

            if(!this.isInRange && MapUtils.getDistance(location, this.mcDonalds.getLocation()) <= this.NOTIFY_RANGE)
            {
                this.isInRange = true;
                Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    v.vibrate(VibrationEffect.createOneShot(this.NOTIFY_VIBRATION_TIME_MILLISECONDS, VibrationEffect.DEFAULT_AMPLITUDE));
                else
                    v.vibrate(this.NOTIFY_VIBRATION_TIME_MILLISECONDS);

                Toast.makeText(this, getText(R.string.in_range_notification), Toast.LENGTH_LONG).show();
            }
            else if(this.isInRange && MapUtils.getDistance(location, this.mcDonalds.getLocation()) > this.NOTIFY_RANGE)
                this.isInRange = false;
        }
    }

    @Override
    public void onRouteAvailable(ArrayList<LatLng> locations, LatLng northEastBoundry, LatLng southWestBoundry)
    {
        if(this.googleMap != null)
        {
            PolylineOptions polylineOptions = new PolylineOptions().clickable(false).addAll(locations).color(Color.argb(0.5f, 0.0f, 0.0f, 0.0f));
            this.fullRoute = this.googleMap.addPolyline(polylineOptions);

            MarkerOptions markerOptions = new MarkerOptions().position(this.mcDonalds.getLocation())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            this.marker = this.googleMap.addMarker(markerOptions);

            CircleOptions circleOptions = new CircleOptions().center(this.mcDonalds.getLocation()).fillColor(Color.argb(0.2f, 1.0f, 0.0f, 0.0f)).radius(100).strokeColor(Color.argb(0.25f, 1.0f, 0.0f, 0.0f));
            this.circle = this.googleMap.addCircle(circleOptions);

            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(southWestBoundry, northEastBoundry), this.NOTIFY_RANGE));
        }
    }

    private ArrayList<LatLng> getLocationsBefore(LatLng latLng)
    {
        ArrayList<LatLng> locationsBefore = new ArrayList<LatLng>();

        for(LatLng location : this.fullRoute.getPoints())
        {
            locationsBefore.add(location);

            if(location.latitude == latLng.latitude && location.longitude == latLng.longitude)
                break;
        }
        return locationsBefore;
    }

    @Override
    public void onFragmentInteraction(Uri uri)
    {

    }
}
