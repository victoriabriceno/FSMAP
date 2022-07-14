package com.example.logintesting;

import com.google.android.gms.maps.model.Marker;

public class UserFavoriteList {

    String markerTitle;

    public UserFavoriteList(String markerTitle) {
        this.markerTitle = markerTitle;
    }

    public String getMarkerTitle() {
        return markerTitle;
    }

    public void setMarkerTitle(String markerTitle) {
        this.markerTitle = markerTitle;
    }
}
