package com.example.user.testproject.models;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

public class Placeinfo {
    private String name;
    private String address;
    private  String phoneno;
    private String id;
    private Uri webUri;
    private float rating;
    private LatLng latLng;
    private String attributions;

    public Placeinfo() {
    }

    public Placeinfo(String name, String address, String phoneno, String id, Uri webUri, float rating, LatLng latLng, String attributions) {
        this.name = name;
        this.address = address;
        this.phoneno = phoneno;
        this.id = id;
        this.webUri = webUri;
        this.rating = rating;
        this.latLng = latLng;
        this.attributions = attributions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Uri getWebUri() {
        return webUri;
    }

    public void setWebUri(Uri webUri) {
        this.webUri = webUri;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getAttributions() {
        return attributions;
    }

    public void setAttributions(String attributions) {
        this.attributions = attributions;
    }
}
