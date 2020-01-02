package com.example.eindopdracht_client_side_development_app.views;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;

import com.example.eindopdracht_client_side_development_app.R;
import com.example.eindopdracht_client_side_development_app.models.McDonalds;
import com.example.eindopdracht_client_side_development_app.util.DirectionsAPIListener;
import com.example.eindopdracht_client_side_development_app.util.DirectionsAPIManager;
import com.example.eindopdracht_client_side_development_app.util.LocationAPIListener;
import com.example.eindopdracht_client_side_development_app.util.LocationAPIManager;
import com.example.eindopdracht_client_side_development_app.util.MapUtils;
import com.google.android.gms.maps.CameraUpdate;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationAPIListener, DirectionsAPIListener
{
    private GoogleMap googleMap;
    private LocationAPIManager locationAPIManager;
    private DirectionsAPIManager directionsAPIManager;

    private McDonalds mcDonalds;

    private Polyline fullRoute;
    private Marker marker;
    private Circle circle;
    private Polyline walkedRoute;

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

        this.locationAPIManager = LocationAPIManager.getInstance();
        this.locationAPIManager.setLocationAPIListener(this);
        this.directionsAPIManager = new DirectionsAPIManager(this, this);
        this.directionsAPIManager.requestRoute(this.locationAPIManager.getLastLocation(), mcDonalds);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        this.googleMap = googleMap;
        this.googleMap.setMyLocationEnabled(true);
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
        }
    }

    @Override
    public void onRouteAvailable(ArrayList<LatLng> locations, LatLng northEastBoundry, LatLng southWestBoundry)
    {
        if(this.googleMap != null)
        {
            PolylineOptions polylineOptions = new PolylineOptions().clickable(false).addAll(locations).color(Color.argb(0.5f, 0.0f, 0.0f, 0.0f));
            this.fullRoute = this.googleMap.addPolyline(polylineOptions);

            MarkerOptions markerOptions = new MarkerOptions().position(this.mcDonalds.getLocation()).title(this.mcDonalds.getAddress())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            this.marker = this.googleMap.addMarker(markerOptions);

            CircleOptions circleOptions = new CircleOptions().center(this.mcDonalds.getLocation()).fillColor(Color.argb(0.5f, 0.0f, 0.0f, 0.4f));
            this.circle = this.googleMap.addCircle(circleOptions);

            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(southWestBoundry, northEastBoundry), 100));
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
}
