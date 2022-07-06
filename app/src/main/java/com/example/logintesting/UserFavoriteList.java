package com.example.logintesting;

import com.google.android.gms.maps.model.Marker;

public class UserFavoriteList {

    Marker markerTitle;

    public String getMarkerTitle() {
        return markerTitle.getTitle();
    }

    public void setMarkerTitle(Marker markerTitle) {
        this.markerTitle = markerTitle;
    }
}
