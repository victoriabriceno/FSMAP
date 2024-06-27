
package com.example.FSMap;

//This class is used when a user creates a favorite to allow changing the name of the marker
// without losing track of the original name
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
