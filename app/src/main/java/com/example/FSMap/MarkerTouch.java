package com.example.FSMap;


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

import java.util.ArrayList;
import java.util.List;

public class MarkerTouch extends FrameLayout {

    private static final String FINE_lOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;


    //Change this to change clickable radius in pixels
    private static final int ClickRadius = 50;

    private GoogleMap mGoogleMap;
    private List<Marker> mMarkers, Favs, CM, AM;
    ArrayList<Marker> markersClicked = new ArrayList<>();
    public Marker createdMarker, marker2;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean mLocationPermissionsGranted, wasRemoveHit, wasMarkerClicked, csvReady, cReady, fReady = false;
    private Context C;
    private Activity A;
    private double Latitude, Longitude;
    int clickCount = 0;
    List<Polyline> linesShowing;
    MapsActivity mapsActivity = new MapsActivity();

    boolean slideup = mapsActivity.slidepup;


    BottomSheetBehavior bottomSheetBehavior;


    public MarkerTouch(Context context) {
        super(context);
    }

    public void setGoogleMap(GoogleMap googleMap, List<Polyline> lines, Context context, Activity activity) {
        mGoogleMap = googleMap;
        C = context;
        A = activity;
        linesShowing = lines;

    }

    public void MakeList() {
        AM = mMarkers;
        AM.addAll(CM);
    }

    public void CSVMarkers(List<Marker> markers) {
        mMarkers = markers;
        csvReady = true;
        if (cReady && fReady) {
            MakeList();
        }
    }

    public void CustomMarkers(List<Marker> custom) {
        CM = custom;
        cReady = true;
        if (csvReady && fReady) {
            MakeList();
        }
    }

    public void FavoriteMarkers(List<Marker> fav) {
        Favs = fav;
        fReady = true;
        if (csvReady && cReady) {
            MakeList();
        }
    }


    //Marker Click
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
            if (minDistanceInPixels > ClickRadius) {
                marker = null;
            }

            if (marker != null) {
                //Do marker stuff here
                //vvvvvvvvvvvvvvvvvvv
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

                //Favorites star; is filled in or is empty
                ImageButton bntFavoritesRemove = (ImageButton) A.findViewById(R.id.btnRemoveFavorites);
                ImageButton btnFavoritesAdd = (ImageButton) A.findViewById(R.id.btnAddFavorites);

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
                /*marker.showInfoWindow();*/

                //Slide up code

                LinearLayout slideupview = A.findViewById(R.id.design_bottom_sheet);
                bottomSheetBehavior = BottomSheetBehavior.from(slideupview);
                TextView text = slideupview.findViewById(R.id.roomnumber);
                text.setText(marker.getTitle());
                if (!slideup|| bottomSheetBehavior.getState()!=BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetBehavior.setFitToContents(false);

                    //bottomSheetBehavior.setExpandedOffset(10);
                    slideupview.setVisibility(View.VISIBLE);
                    slideup = true;
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
                //hide markers after one is clicked
//                for (Marker m : AM)
//                {
//                    if(!m.getTitle().equals(marker.getTitle())) {
//                        m.setVisible(false);
//                    }
//                }
                marker2 = marker;

            }
        }

        return super.dispatchTouchEvent(event);
    }

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

    private void getLocationPermission() {
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

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

    public boolean isItInMyFavorites(Marker marker) {
        for (int i = 0; i < Favs.size(); i++) {
            if (Favs.get(i).getTitle().equals(marker.getTitle())) {
                return true;
            }
        }
        return false;
    }

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

    private void moveCamera(LatLng latLng) {
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

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
