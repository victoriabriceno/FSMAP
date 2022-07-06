package com.example.logintesting;

import com.google.android.gms.maps.model.Marker;

public class ModelFavorite {

    String markerTitle;
    boolean favorite;

    public ModelFavorite(){

    }

    //Constructors
    public ModelFavorite(String markerTitle, boolean favorite){

        this.markerTitle = markerTitle;
        this.favorite = favorite;
    }



    //Getter and setters
    public String getMarkerTitle(){ return markerTitle;
        }
    public void setMarkerTitle(Marker marker){ this.markerTitle = marker.getTitle();}

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
