package com.example.eindopdracht_client_side_development_app.util;

import android.content.Context;
import android.location.Location;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.eindopdracht_client_side_development_app.R;
import com.example.eindopdracht_client_side_development_app.models.McDonalds;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DirectionsAPIManager implements Response.Listener, Response.ErrorListener
{
    private Context context;
    private RequestQueue requestQueue;

    private LatLng northEastBoundary;
    private LatLng southWestBoundary;

    private DirectionsAPIListener directionsAPIListener;

    private boolean firstRequest;

    public DirectionsAPIManager(Context context, DirectionsAPIListener directionsAPIListener)
    {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);

        this.directionsAPIListener = directionsAPIListener;
        this.firstRequest = true;
    }

    public void requestRoute(LatLng userLocation, McDonalds mcDonalds)
    {
        this.firstRequest = true;
        sendRequest(Request.Method.GET, getUrlForWaypoints(userLocation, mcDonalds.getLocation()));
    }

    private void sendRequest(int method, String url)
    {
        JsonRequest jsonRequest = new JsonObjectRequest(method, url, null, this, this);
        //jsonRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.requestQueue.add(jsonRequest);
    }

    private String getUrlForWaypoints(LatLng startLocation, LatLng endLocation)
    {
        String url = "http://145.48.6.80:3000/directions?"
                + "origin=" + startLocation.latitude + "," + startLocation.longitude
                + "&destination=" + endLocation.latitude + "," + endLocation.longitude
                + "&mode=driving"
                + "&key=" + this.context.getResources().getString(R.string.google_directions_key);

        return url;
    }

    private String getUrlForWaypoints(ArrayList<LatLng> locations)
    {
        String url = "http://145.48.6.80:3000/directions?"
                + "origin=" + locations.get(0).latitude + "," + locations.get(0).longitude
                + "&destination=" + locations.get(locations.size() - 1).latitude + "," + locations.get(locations.size() - 1).longitude
                + "&waypoints=";

        for(int i = 1; i < locations.size() - 1; i++)
        {
            url += locations.get(i).latitude + "," + locations.get(i).longitude;
            if(i < locations.size() - 2)
                url += "|";
        }

        url += "&mode=driving"
            + "&key=" + this.context.getResources().getString(R.string.google_directions_key);

        return url;
    }

    @Override
    public void onResponse(Object response)
    {
        JSONObject jsonObject = (JSONObject)response;
        String test = jsonObject.toString();
        try
        {
            ArrayList<LatLng> locations = new ArrayList<LatLng>();

            if(jsonObject.getString("status").toLowerCase().equals("ok"))
            {
                JSONArray routes = jsonObject.getJSONArray("routes");
                JSONObject bounds = routes.getJSONObject(0).getJSONObject("bounds");

                JSONObject northEast = bounds.getJSONObject("northeast");
                JSONObject southWest = bounds.getJSONObject("southwest");
                LatLng northEastBound = new LatLng(northEast.getDouble("lat"), northEast.getDouble("lng"));
                LatLng southWestBound = new LatLng(southWest.getDouble("lat"), southWest.getDouble("lng"));

                JSONArray legs = routes.getJSONObject(0).getJSONArray("legs");

                for(int l = 0; l < legs.length(); l++)
                {
                    JSONObject leg = legs.getJSONObject(l);
                    JSONArray steps = leg.getJSONArray("steps");

                    for(int i = 0; i < steps.length(); i++)
                    {
                        JSONObject step = steps.getJSONObject(i);
                        JSONObject startLocation = step.getJSONObject("start_location");
                        JSONObject endLocation = step.getJSONObject("end_location");

                        LatLng lastLocation = (locations.size() != 0) ? (locations.get(locations.size() - 1)) : null;
                        if(lastLocation == null)
                            locations.add(new LatLng(startLocation.getDouble("lat"), startLocation.getDouble("lng")));
                        locations.add(new LatLng(endLocation.getDouble("lat"), endLocation.getDouble("lng")));
                    }
                }

                this.northEastBoundary = northEastBound;
                this.southWestBoundary = southWestBound;

                this.directionsAPIListener.onRouteAvailable(locations, this.northEastBoundary, this.southWestBoundary);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error)
    {

    }
}
