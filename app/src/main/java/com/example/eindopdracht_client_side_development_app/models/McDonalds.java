package com.example.eindopdracht_client_side_development_app.models;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class McDonalds implements Serializable
{
    private int id;
    private String address;
    private String phoneNumber;
    private double latitude;
    private double longitude;
    private boolean isFavorite;

    public McDonalds(int id, String address, String phoneNumber, LatLng location, boolean isFavorite)
    {
        this.id = id;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.latitude = location.latitude;
        this.longitude = location.longitude;
        this.isFavorite = isFavorite;
    }

    public McDonalds(String address, String phoneNumber, LatLng location, boolean isFavorite)
    {
        this(-1, address, phoneNumber, location, isFavorite);
    }

    public McDonalds(String address, String phoneNumber, LatLng location)
    {
        this(-1, address, phoneNumber, location, false);
    }

    public McDonalds clone()
    {
        return new McDonalds(this.id, this.address, this.phoneNumber, new LatLng(this.latitude, this.longitude), this.isFavorite);
    }

    public int getId()
    {
        return this.id;
    }

    public String getAddress()
    {
        return this.address;
    }

    public String getPhoneNumber()
    {
        return this.phoneNumber;
    }

    public LatLng getLocation()
    {
        return new LatLng(this.latitude, this.longitude);
    }

    public boolean isFavorite()
    {
        return this.isFavorite;
    }

    public void setFavorite(boolean isFavorite)
    {
        this.isFavorite = isFavorite;
    }
}
