package com.example.eindopdracht_client_side_development_app.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.eindopdracht_client_side_development_app.R;
import com.example.eindopdracht_client_side_development_app.models.Objective;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DirectionsAPIManager implements Response.Listener, Response.ErrorListener
{
    private Context context;
    private RequestQueue requestQueue;

    private ArrayList<Objective> objectives;
    private ArrayList<LatLng> locations;
    private int currentObjectiveIndex;

    private LatLng northEastBoundary;
    private LatLng southWestBoundary;

    private DirectionsAPIListener directionsAPIListener;

    public DirectionsAPIManager(Context context, DirectionsAPIListener directionsAPIListener)
    {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);

        this.objectives = new ArrayList<Objective>();
        this.locations = new ArrayList<LatLng>();
        this.currentObjectiveIndex = 0;

        this.directionsAPIListener = directionsAPIListener;
    }

    public void requestRoute(ArrayList<Objective> objectives)
    {
        reset();
        this.objectives.addAll(objectives);
        sendRequest(Request.Method.GET, getUrlForWaypoints(this.objectives.get(this.currentObjectiveIndex), this.objectives.get(this.currentObjectiveIndex + 1)));
    }

    private void sendRequest(int method, String url)
    {
        JsonRequest jsonRequest = new JsonObjectRequest(method, url, null, this, this);
        //jsonRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        this.requestQueue.add(jsonRequest);
    }

    private String getUrlForWaypoints(Objective objectiveA, Objective objectiveB)
    {
        String url = "http://145.48.6.80:3000/directions?"
                + "origin=" + objectiveA.getLocation().latitude + "," + objectiveA.getLocation().longitude
                + "&destination=" + objectiveB.getLocation().latitude + "," + objectiveB.getLocation().longitude
                + "&mode=walking"
                + "&key=" + this.context.getResources().getString(R.string.google_directions_key);

        return url;
    }

    private void reset()
    {
        this.objectives.clear();
        this.locations.clear();
        this.currentObjectiveIndex = 0;
    }

    @Override
    public void onResponse(Object response)
    {
        JSONObject jsonObject = (JSONObject)response;
        try
        {
            if(jsonObject.getString("status").toLowerCase().equals("ok"))
            {
                JSONArray routes = jsonObject.getJSONArray("routes");
                JSONObject bounds = routes.getJSONObject(0).getJSONObject("bounds");

                JSONObject northEast = bounds.getJSONObject("northeast");
                JSONObject southWest = bounds.getJSONObject("southwest");
                LatLng northEastBound = new LatLng(northEast.getDouble("lat"), northEast.getDouble("lng"));
                LatLng southWestBound = new LatLng(southWest.getDouble("lat"), southWest.getDouble("lng"));

                JSONArray legs = routes.getJSONObject(0).getJSONArray("legs");
                JSONObject leg = legs.getJSONObject(0);
                JSONArray steps = leg.getJSONArray("steps");

                for(int i = 0; i < steps.length(); i++)
                {
                    JSONObject step = steps.getJSONObject(i);
                    JSONObject startLocation = step.getJSONObject("start_location");
                    JSONObject endLocation = step.getJSONObject("end_location");

                    LatLng lastLocation = (this.locations.size() != 0) ? (this.locations.get(this.locations.size() - 1)) : null;
                    if(lastLocation == null)
                        this.locations.add(new LatLng(startLocation.getDouble("lat"), startLocation.getDouble("lng")));
                    this.locations.add(new LatLng(endLocation.getDouble("lat"), endLocation.getDouble("lng")));
                }

                if(this.currentObjectiveIndex == 0)
                {
                    this.northEastBoundary = northEastBound;
                    this.southWestBoundary = southWestBound;
                }
                else
                {
                    if(northEastBound.latitude > this.northEastBoundary.latitude || northEastBound.longitude < this.northEastBoundary.longitude)
                        this.northEastBoundary = northEastBound;
                    if(southWestBound.latitude > this.southWestBoundary.latitude || southWestBound.longitude < this.southWestBoundary.longitude)
                        this.southWestBoundary = southWestBound;
                }

                this.currentObjectiveIndex++;

                if(this.currentObjectiveIndex == this.objectives.size() - 1)
                    this.directionsAPIListener.onRouteAvailable(this.locations, this.objectives, this.northEastBoundary, this.southWestBoundary);
                else
                    sendRequest(Request.Method.GET, getUrlForWaypoints(this.objectives.get(this.currentObjectiveIndex), this.objectives.get(this.currentObjectiveIndex + 1)));
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
        String test = "";
    }
}
