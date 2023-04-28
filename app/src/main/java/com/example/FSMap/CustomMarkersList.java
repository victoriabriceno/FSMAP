package com.example.FSMap;

import com.google.android.gms.maps.model.LatLng;

public class CustomMarkersList {
    String markerTitle;

    String orginalNameCustom;

    int floor;

    LatLng position;

    public CustomMarkersList(String markerTitle,String originalNameCustom,int floor,LatLng position) {
        this.markerTitle = markerTitle;
        this.orginalNameCustom = originalNameCustom;
        this.floor = floor;
        this.position = position;
    }
    public String getMarkerTitle() {
        return markerTitle;
    }

    public String getOrginalNameCustom(){
        return orginalNameCustom;
    }

    public void setMarkerTitle(String markerTitle) {
        this.markerTitle = markerTitle;
    }
}
