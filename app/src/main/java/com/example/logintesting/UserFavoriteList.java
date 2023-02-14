package com.example.logintesting;

import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.model.Marker;

public class UserFavoriteList {

    String markerTitle;
    String OriginalTitle;

    public UserFavoriteList(String markerTitle,String originalName) {
        this.markerTitle = markerTitle;
        this.OriginalTitle = originalName;
    }

    public String getMarkerTitle() {
        return markerTitle;
    }
    public String getOriginalTitle(){return  OriginalTitle;}
    public void setMarkerTitle(String markerTitle) { this.markerTitle = markerTitle; }




}
