
package com.example.FSMap;

public class UserFavoriteList {

    String markerTitle;
    String OriginalTitle;


    public UserFavoriteList(String markerTitle,String OriginalTitle) {
        this.markerTitle = markerTitle;
        this.OriginalTitle = OriginalTitle;
    }

    public String getMarkerTitle() {
        return markerTitle;
    }

    public String getOriginalTitle(){return OriginalTitle;}

    public void setMarkerTitle(String markerTitle) { this.markerTitle = markerTitle; }




}
