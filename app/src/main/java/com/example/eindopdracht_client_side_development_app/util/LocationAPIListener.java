package com.example.eindopdracht_client_side_development_app.util;

import com.google.android.gms.maps.model.LatLng;

public interface LocationAPIListener
{
    void onLocationReceived(LatLng location);
}
