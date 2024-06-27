package com.example.FSMap;

//Broken Imports as of 6/18/24
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.objectweb.asm.Handle;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

//This class handles the functionality
public class MarkerTouch extends FrameLayout {

    private static final String FINE_lOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;


    //Change this to change clickable radius in pixels
    private static final int ClickRadius = 50;

    private GoogleMap mGoogleMap;
    public List<Marker> mMarkers, Favs, CM, AM;
    ArrayList<Marker> markersClicked = new ArrayList<>();
    public Marker createdMarker, marker2;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean mLocationPermissionsGranted, wasRemoveHit, csvReady, cReady, fReady = false;
    public boolean markerready,wasMarkerClicked = false;
    private Context C;
    private Activity A;
    private double Latitude, Longitude;
    int clickCount = 0;
    List<Polyline> linesShowing;
    MapsActivity mapsActivity = new MapsActivity();

    boolean slideup = mapsActivity.slidepup;

    public Button FilterMarker;


    BottomSheetBehavior bottomSheetBehavior;


    public MarkerTouch(Context context) {
        super(context);
    }

    //Passing in Map information
    public void setGoogleMap(GoogleMap googleMap, List<Polyline> lines, Context context, Activity activity) {
        mGoogleMap = googleMap;
        C = context;
        A = activity;
        linesShowing = lines;

    }

    //Add all markers to marker list. Only runs once all other three have been completed.
    public void MakeList() {
        AM = mMarkers;
        AM.addAll(CM);
    }

    //Add the markers from the CSV to marker list and wait for CustomMarkers and FavoriteMarkers to be ready
    public void CSVMarkers(List<Marker> markers) {
        mMarkers = markers;
        csvReady = true;
        if (cReady && fReady) {
            MakeList();
        }
    }

    //Add the markers from the custom list to marker list and wait for CSVMarkers and FavoriteMarkers to be ready
    public void CustomMarkers(List<Marker> custom) {
        CM = custom;
        cReady = true;
        if (csvReady && fReady) {
            MakeList();
        }
    }

    //Add the markers from the Favorites list to marker list and wait for CSVMarkers and CustomMarkers to be ready
    public void FavoriteMarkers(List<Marker> fav) {
        Favs = fav;
        fReady = true;
        if (csvReady && cReady) {
            MakeList();
        }
    }


    //This function handles what happens on Marker Click
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mGoogleMap == null) return super.dispatchTouchEvent(event);

        int screenX = (int) event.getX();
        int screenY = (int) event.getY();

        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
            // find marker nearest to touch position
            Projection projection = mGoogleMap.getProjection();

            Marker marker = null;
            int minDistanceInPixels = Integer.MAX_VALUE;
            if (AM != null) {
                for (Marker markers : AM) {
                    Point markerScreen = projection.toScreenLocation(markers.getPosition());
                    int distanceToMarker = (int) Math.sqrt((screenX - markerScreen.x) * (screenX - markerScreen.x)
                            + (screenY - markerScreen.y) * (screenY - markerScreen.y));
                    if (distanceToMarker < minDistanceInPixels) {
                        minDistanceInPixels = distanceToMarker;
                        marker = markers;
                    }
                }
            }
            //If the nearest marker is outside of the radius, "unclick" the current marker
            if (minDistanceInPixels > ClickRadius) {
                marker = null;
            }
            //Check to see if the user clicked a marker
            if (marker != null) {

                //Do marker stuff below

                //If marker is visible: Load favorite information, hide all other markers,
                //Load navigation from user location to point, show navigation buttons, attempt to
                //route, create slideup, prepare filter, prepare navigation textboxes, and save the
                //clicked marker
                //If marker is not visible, do nothing.
                if (marker.isVisible()){
                    wasMarkerClicked = true;
                    getLocationPermission();
                    //If you have location permission, get device location
                    if (mLocationPermissionsGranted) {
                        getDeviceLocation();
                    }

                    HideMarkersExcept(marker);

                    if (!wasRemoveHit) {
                        wasRemoveHit = true;
                    } else {
                        marker.remove();
                    }

                    //Favorite Star Section
                    //Loading images
                    ImageButton bntFavoritesRemove = (ImageButton) A.findViewById(R.id.btnRemoveFavorites);
                    ImageButton btnFavoritesAdd = (ImageButton) A.findViewById(R.id.btnAddFavorites);

                    //Check if the marker is in favorites. If yes, fill in the star. If no empty it.
                    if (isItInMyFavorites(marker)) {

                        btnFavoritesAdd.setVisibility(View.GONE);
                        bntFavoritesRemove.setVisibility(View.VISIBLE);
                    } else {

                        bntFavoritesRemove.setVisibility(View.GONE);
                        btnFavoritesAdd.setVisibility(View.VISIBLE);
                    }

                    wasRemoveHit = false;

                    wasMarkerClicked = true;
                    //NavDone button
                    Button NavDone = A.findViewById(R.id.NavDone);
                    NavDone.setVisibility(View.VISIBLE);
                    //check for marker in original Marker list
                    Button RemovePoint = A.findViewById(R.id.RemoveSpot);
                    if (isCreatedMarker(marker)) {
                        RemovePoint.setVisibility(View.VISIBLE);
                        createdMarker = marker;
                    } else {
                        RemovePoint.setVisibility(View.GONE);
                    }
                    //Zoom camera out if the camera is zoomed too far in.
                    if (mGoogleMap.getCameraPosition().zoom < 18) {
                        moveCamera(marker.getPosition(), 20f);
                    } else {
                        moveCamera(marker.getPosition());
                    }
                    //Keep track of how many times a marker is clicked
                    if (clickCount == 0) {
                        clickCount++;
                        //add marker to markersClicked
                        markersClicked.add(marker);

                    }
                    //If clicking another marker, switch marker and line
                    if (markersClicked.size() != 0) {
                        if (!markersClicked.get(0).equals(marker)) {
                            clickCount++;
                            markersClicked.add(marker);
                        }
                        //triggers if user clicks on same marker twice
                        else {
                        }
                    } else {
//                  //If no other marker is clicked, simply note the marker as clicked
                        markersClicked.add(marker);
                    }
                    //Remove marker from markers clicked when more than 1 marker has been clicked
                    if (markersClicked.size() > 1) {
                        markersClicked.remove(0);
                    }

                    //Controlling the Filter button
                    FilterMarker = A.findViewById(R.id.FilterButton);
                    //Slide up creation and filling information
                    LinearLayout slideupview = A.findViewById(R.id.design_bottom_sheet);
                    bottomSheetBehavior = BottomSheetBehavior.from(slideupview);
                    TextView text = slideupview.findViewById(R.id.roomnumber);
                    text.setText(marker.getTitle());

                    //If the slideup is not collapsed, fill in the information on the slide up with
                    // the marker's information and disable the filter button
                    if (!slideup || bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        bottomSheetBehavior.setFitToContents(false);
                        slideupview.setVisibility(View.VISIBLE);
                        slideup = true;
                        FilterMarker.setEnabled(false);

                        //Enable Filter button if the slideup is active
                    }else{
                        FilterMarker.setEnabled(true);
                    }


                    //Creates list of all marker titles for navigation text boxes
                    ArrayList<String> listfornav = new ArrayList<String>();
                    for (Marker m : AM) {
                        if (m.getTitle() != null) {
                            listfornav.add(m.getTitle());
                        }
                    }

                    //Setting up the textviews from the slideup
                    AutoCompleteTextView from = A.findViewById(R.id.From);
                    AutoCompleteTextView To = A.findViewById(R.id.Destination);

                    //If the marker is not a user created marker, enable the textboxes for navigation
                    // Else disable their interaction and fill in the navigation with their current
                    // location to the clicked marker
                    if (!isCreatedMarker(marker)) {
                        from.setEnabled(true);
                        from.setFocusableInTouchMode(true);
                        from.setBackgroundColor(Color.GRAY);
                        To.setBackgroundColor(Color.GRAY);
                        To.setEnabled(true);
                        To.setFocusableInTouchMode(true);
                        //Creating Suggestions for text boxes in nav from listfornav
                        ArrayAdapter<String> adapterlist = new ArrayAdapter<String>(A, android.R.layout.simple_dropdown_item_1line, listfornav);
                        from = A.findViewById(R.id.From);
                        from.setText("");//Blank "from"
                        from.setAdapter(adapterlist);//set dropdown
                        AutoCompleteTextView destination = A.findViewById(R.id.Destination);
                        destination.setAdapter(adapterlist);//set dropdown
                        //Autofill Destination
                        destination.setText(markersClicked.get(0).getTitle());
                    } else {
                        from.setEnabled(false);
                        from.setText("Current Location");
                        from.setFocusable(false);
                        from.setBackgroundColor(Color.TRANSPARENT);
                        To.setEnabled(false);
                        To.setText(marker.getTitle());
                        To.setFocusable(false);
                        To.setBackgroundColor(Color.TRANSPARENT);
                    }
                    //Keep track of the marker in marker2 and signify we are ready to do more
                    // marker interaction
                    marker2 = marker;
                    markerready = true;
                } else {
                    //If marker is not visible, do nothing.
                    return true;
                }

            }
        }

        return super.dispatchTouchEvent(event);
    }

    //ManualTouch is used for when we want to click a marker without the user clicking
    //Performs most of the MarkerTouch method, but with pre filled in information.
    //Unlick MarkerTouch, it can click and make invisible markers visible
    public void ManualTouch(Marker marker) {
        if (!marker.isVisible()){
            marker.setVisible(true);
        }
        wasMarkerClicked = true;
        HideMarkersExcept(marker);
        getLocationPermission();
        //If you have location, get device location
        if (mLocationPermissionsGranted) {
            getDeviceLocation();
        }

        if (!wasRemoveHit) {
            wasRemoveHit = true;
        } else {
            marker.remove();
        }


        ImageButton bntFavoritesRemove = (ImageButton) A.findViewById(R.id.btnRemoveFavorites);
        ImageButton btnFavoritesAdd = (ImageButton) A.findViewById(R.id.btnAddFavorites);
        //Change the Favorite star depending on favorite status
        if (isItInMyFavorites(marker)) {

            btnFavoritesAdd.setVisibility(View.GONE);
            bntFavoritesRemove.setVisibility(View.VISIBLE);
        } else {

            bntFavoritesRemove.setVisibility(View.GONE);
            btnFavoritesAdd.setVisibility(View.VISIBLE);
        }

        wasRemoveHit = false;

        wasMarkerClicked = true;
        //NavDone button
        Button NavDone = A.findViewById(R.id.NavDone);
        NavDone.setVisibility(View.VISIBLE);
        //check for marker in original Marker list
        Button RemovePoint = A.findViewById(R.id.RemoveSpot);
        if (isCreatedMarker(marker)) {
            RemovePoint.setVisibility(View.VISIBLE);
            createdMarker = marker;
        } else {
            RemovePoint.setVisibility(View.GONE);
        }
        //Change camera, zoom if needed
        if (mGoogleMap.getCameraPosition().zoom < 18) {
            moveCamera(marker.getPosition(), 20f);
        } else {
            moveCamera(marker.getPosition());
        }
        //Keep track of how many times a marker is clicked
        if (clickCount == 0) {
            clickCount++;
            //add marker to markersClicked
            markersClicked.add(marker);

        }
        //If clicking another marker, switch marker and line
        if (markersClicked.size() != 0) {
            if (!markersClicked.get(0).equals(marker)) {
                clickCount++;
//                        RemoveAllLines();
                markersClicked.add(marker);
            }
            //triggers if user clicks on same marker twice
            else {
            }
        } else {
//                    RemoveAllLines();
            markersClicked.add(marker);
        }
        //Remove marker from markers clicked when more than 1 marker has been clicked
        if (markersClicked.size() > 1) {
            markersClicked.remove(0);
        }

        //Slide up code

        FilterMarker = A.findViewById(R.id.FilterButton);
        LinearLayout slideupview = A.findViewById(R.id.design_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(slideupview);
        TextView text = slideupview.findViewById(R.id.roomnumber);
        text.setText(marker.getTitle());

        if (!slideup || bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            bottomSheetBehavior.setFitToContents(false);
            slideupview.setVisibility(View.VISIBLE);
            slideup = true;
           FilterMarker.setEnabled(false);

        }else{
            FilterMarker.setEnabled(true);
        }
        //Creates list of all marker titles
        ArrayList<String> listfornav = new ArrayList<String>();
        for (Marker m : AM) {
            if (m.getTitle() != null) {
                listfornav.add(m.getTitle());
            }
        }

        AutoCompleteTextView from = A.findViewById(R.id.From);
        AutoCompleteTextView To = A.findViewById(R.id.Destination);

        if (!isCreatedMarker(marker)) {
            from.setEnabled(true);
            from.setFocusableInTouchMode(true);
            from.setBackgroundColor(Color.GRAY);
            To.setBackgroundColor(Color.GRAY);
            To.setEnabled(true);
            To.setFocusableInTouchMode(true);
            //Creating Suggestions for text boxes in nav
            ArrayAdapter<String> adapterlist = new ArrayAdapter<String>(A, android.R.layout.simple_dropdown_item_1line, listfornav);
            from = A.findViewById(R.id.From);
            from.setText("");//Blank "from"
            from.setAdapter(adapterlist);//set dropdown
            AutoCompleteTextView destination = A.findViewById(R.id.Destination);
            destination.setAdapter(adapterlist);//set dropdown
            //Autofill Destination
            destination.setText(markersClicked.get(0).getTitle());
        } else {
            from.setEnabled(false);
            from.setText("Current Location");
            from.setFocusable(false);
            from.setBackgroundColor(Color.TRANSPARENT);
            To.setEnabled(false);
            To.setText(marker.getTitle());
            To.setFocusable(false);
            To.setBackgroundColor(Color.TRANSPARENT);
        }

        marker2 = marker;
        markerready = true;
    }

    //Grabs the devices current location and sets it across the app
    //Should only be run after verifying we have location permissions
    private void getDeviceLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(C);
        try {
            if (mLocationPermissionsGranted) {
                @SuppressLint("MissingPermission") Task Location = fusedLocationClient.getLastLocation();
                Location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            android.location.Location currentLocation = (Location) task.getResult();
                            Latitude = currentLocation.getLatitude();
                            Longitude = currentLocation.getLongitude();
                        } else {

                        }

                    }
                });
            }

        } catch (SecurityException e) {

        }

    }

    //Getting the location permission
    private void getLocationPermission() {
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        //Check if the user previously gave location permission to the app
        //If not, ask for permission
        if (ContextCompat.checkSelfPermission(C, FINE_lOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(C, COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //SET A BOOLEAN
                mLocationPermissionsGranted = true;

            } else {
                ActivityCompat.requestPermissions(A, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(A, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    //function to Loop through the Favorites list to check if the passed in marker is in the list
    public boolean isItInMyFavorites(Marker marker) {
        if (Favs !=null){
            for (int i = 0; i < Favs.size(); i++) {
                if (Favs.get(i).getTitle().equals(marker.getTitle())) {
                    return true;
                }
            }
        }
        return false;
    }

    //Function to Loop through the Created Markers list to check if passed in marker is in the list
    public boolean isCreatedMarker(Marker marker) {
        boolean isItCreatedMarker = false;
        for (Marker m : CM) {
            if (m.getTitle().equals(marker.getTitle())) {
                isItCreatedMarker = true;
                break;
            }
        }
        return isItCreatedMarker;
    }

    //Hide all markers except for marker passed in (m)
    public void HideMarkersExcept(Marker m){
        for(Marker marker: CM){
            if(!marker.equals(m)){
                marker.setVisible(false);
            }
        }
        for(Marker marker: AM){
            if(!marker.equals(m)){
                marker.setVisible(false);
            }
        }
        for(Marker marker: Favs){
            if(!marker.equals(m)){
                marker.setVisible(false);
            }
        }
        for (Marker marker: mMarkers){
            if(!marker.equals(m)){
                marker.setVisible(false);
            }
        }
    }

    //Manually move camera
    public void moveCamera(LatLng latLng) {

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    //Manually move camera and change zoom
    private void moveCamera(LatLng latlng, float zoom) {
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
    }

    public void RemoveAllLines() {
        while (linesShowing.size() > 0) {
            linesShowing.get(0).remove();
            linesShowing.remove(0);
        }
    }
}


