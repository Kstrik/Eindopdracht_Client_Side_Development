package com.example.eindopdracht_client_side_development_app.util;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public interface DirectionsAPIListener
{
    void onRouteAvailable(ArrayList<LatLng> locations, LatLng northEastBoundry, LatLng southWestBoundry);
}
