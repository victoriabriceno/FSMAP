package com.example.logintesting;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.example.logintesting.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityMapsBinding binding;
    private ImageView userIcon;
    private ImageView mGps;
    private static final String FINE_lOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationPermissionsGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE= 1234;
    private static final float DEFAULT_ZOOM = 15f;
    ArrayList<GroundOverlay> groundOverlays = new ArrayList<GroundOverlay>();
    ArrayList<Marker> MarkersList =  new ArrayList<Marker>();
    ArrayList<Polyline> LinesList =  new ArrayList<Polyline>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        userIcon = (ImageView)findViewById(R.id.user);
        userIcon.setOnClickListener(this);

        mGps = (ImageView) findViewById(R.id.gps);

        getLocationPermission();

    }

    private void getDeviceLocation(){

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted){
                @SuppressLint("MissingPermission") Task Location = fusedLocationClient.getLastLocation();
                Location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),DEFAULT_ZOOM);
                        }else{
                            Toast.makeText(MapsActivity.this, "Unable to get current Location", Toast.LENGTH_SHORT).show();

                        }



                    }
                });
            }

        }catch(SecurityException e){

        }

    }
    private void moveCamera(LatLng latLng,float zoom){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }
    private void Init(){
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        //get latlong for corners for specified place
        LatLng one = new LatLng( 28.5899089565466,-81.30689695755838);
        LatLng two = new LatLng(28.597315583066404,-81.29914504373565);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        //add them to builder
        builder.include(one);
        builder.include(two);

        LatLngBounds bounds = builder.build();

        //get width and height to current display screen
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        //20% Padding
        int padding = (int)(width * 0.18);

        //set latlong bounds
        mMap.setLatLngBoundsForCameraTarget(bounds);

        //move camera to fill the bound to screen
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds,width,height,padding));

        //set zoom
        mMap.setMinZoomPreference(mMap.getCameraPosition().zoom);

        if (mLocationPermissionsGranted){
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this,Manifest.permission.ACCESS_COARSE_LOCATION )!= PackageManager.PERMISSION_GRANTED){
                return;

            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            Init();
        }
        //add Marker
        MarkerOptions Meeting119 =  new MarkerOptions().position(new LatLng(28.593989,-81.304514)).title("Meeting 119");
        Meeting119.visible(false);
        Marker room119 = mMap.addMarker(Meeting119);
        MarkersList.add(room119);
        //add PathLine
        Polyline line  = mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(28.594075,-81.304381))
                .add(new LatLng(28.593989,-81.304386))
                .add(new LatLng(28.593989,-81.304484))
                .add(new LatLng(28.593989,-81.304514)));
        Polyline room119to118 =  mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(28.593989,-81.304514))
                .add(new LatLng(28.593989,-81.304484))
                .add(new LatLng(28.593959,-81.304484))
                .add(new LatLng(28.593959,-81.304514)));
        Polyline room119to117 =  mMap.addPolyline(new PolylineOptions()
                .add(new LatLng(28.593989,-81.304514))
                .add(new LatLng(28.593989,-81.304484))
                .add(new LatLng(28.593929,-81.304484))
                .add(new LatLng(28.593929,-81.304514)));
        LinesList.add(room119to117);
        LinesList.add(line);
        LinesList.add(room119to118);
        MarkerOptions meeting117 =  new MarkerOptions().position(new LatLng(28.593929,-81.304514)).title("Meeting 117");
        Marker room117 = mMap.addMarker(meeting117);
        MarkersList.add(room117);
        MarkerOptions Meeting118 = new MarkerOptions().position(new LatLng(28.593959,-81.304514)).title("Meeting 118");
        Marker room118 = mMap.addMarker(Meeting118);
        MarkersList.add(room118);
        for (Marker marker1: MarkersList)
        {
            marker1.setVisible(false);
        }
        for (Polyline lines1: LinesList)
        {
            lines1.setVisible(false);
        }
        //Set the bounds for overlay
        LatLngBounds buildLibrary = new LatLngBounds(
                new LatLng(28.59379993356988, -81.30450729197996),
                new LatLng(28.594005193975605, -81.30415971195876));

        //create map overlap
        GroundOverlayOptions buildLibraryOverlay = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.buildinglibrary))
                .anchor(0.468f,0.45f)
                .position(new LatLng(28.593907678091824, -81.3043584293843),38,28);

        //add groundOverlay and create reference.
        GroundOverlay buildLibraryOverlayed = mMap.addGroundOverlay(buildLibraryOverlay);

        //make it so overlay doesnt appear originally
        buildLibraryOverlayed.setVisible(false);

        //add the overlay to overlay array.
        groundOverlays.add(buildLibraryOverlayed);

        //set visibile after certain zoom level is reached.
        mMap.setOnCameraMoveListener(()->
        {
            for (GroundOverlay Overlay : groundOverlays) {
                Overlay.setVisible(mMap.getCameraPosition().zoom > 18);
            }
            for(Marker markers : MarkersList){
                markers.setVisible(mMap.getCameraPosition().zoom >20);
            }
            for(Polyline curline : LinesList) {
                curline.setVisible(mMap.getCameraPosition().zoom>20);
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.user:
                startActivity(new Intent(this,Settings.class));
                break;
        }
    }

    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_lOCATION)== PackageManager.PERMISSION_GRANTED){

            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){

                //SET A BOOLEAN
                mLocationPermissionsGranted = true;




            }else{
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        mLocationPermissionsGranted =false;
        switch(requestCode){

            case LOCATION_PERMISSION_REQUEST_CODE:{
                if (grantResults.length>0 ){
                    for (int i = 0 ; i < grantResults.length; i++){
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            return;
                        }

                    }
                    mLocationPermissionsGranted=true;
                    //initialize our map
                    onMapReady(mMap);


                }

            }

        }


    }
}
