package com.example.FSMap;

import static com.example.FSMap.MapsActivity.COARSE_LOCATION;
import static com.example.FSMap.MapsActivity.FINE_lOCATION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class MarkerTouch extends FrameLayout {
    //Change this to change clickable radius in pixels
    private static final int ClickRadius = 25;

    private GoogleMap mGoogleMap;
    private List<Marker> mMarkers;
    private FusedLocationProviderClient fusedLocationClient;
    private Boolean mLocationPermissionsGranted = false;
    private Context C;


    public MarkerTouch(Context context) {
        super(context);
    }

    public void setGoogleMapAndMarkers(GoogleMap googleMap, List<Marker> markers, Context context) {
        mGoogleMap = googleMap;
        mMarkers = markers;
        C = context;
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

            Marker nearestMarker = null;
            int minDistanceInPixels = Integer.MAX_VALUE;
            for (Marker marker : mMarkers) {
                Point markerScreen = projection.toScreenLocation(marker.getPosition());
                int distanceToMarker = (int) Math.sqrt((screenX - markerScreen.x) * (screenX - markerScreen.x)
                        + (screenY - markerScreen.y) * (screenY - markerScreen.y));
                if (distanceToMarker < minDistanceInPixels) {
                    minDistanceInPixels = distanceToMarker;
                    nearestMarker = marker;
                }
            }
            if (minDistanceInPixels > ClickRadius) {
                nearestMarker = null;
            }

            if (nearestMarker != null) {
                //Do marker stuff here
            }
        }

        return super.dispatchTouchEvent(event);
    }

    private void getDeviceLocation(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(C);
        try {
            if (mLocationPermissionsGranted){
                @SuppressLint("MissingPermission") Task Location = fusedLocationClient.getLastLocation();
                Location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            android.location.Location currentLocation = (Location) task.getResult();
                            Latitude = currentLocation.getLatitude();
                            Longitued = currentLocation.getLongitude();
                        }else{

                        }

                    }
                });
            }

        }catch(SecurityException e){

        }

    }

    private void getLocationPermission(){
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(C,FINE_lOCATION)== PackageManager.PERMISSION_GRANTED){

            if (ContextCompat.checkSelfPermission(C,COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                //SET A BOOLEAN
                mLocationPermissionsGranted = true;

            }else{
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
}
