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
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import android.view.animation.TranslateAnimation;

import org.w3c.dom.Text;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,View.OnClickListener, GoogleMap.OnMarkerClickListener,
GoogleMap.OnMapClickListener{

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityMapsBinding binding;
    private ImageView userIcon;
    private ImageView mGps;
    private Button Navigation;
    private Button NavGo;
    private Button NavDone;
    private AutoCompleteTextView Search;
    private static final String FINE_lOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationPermissionsGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE= 1234;
    private static final float DEFAULT_ZOOM = 15f;
    ArrayList<GroundOverlay> groundOverlays = new ArrayList<GroundOverlay>();
    ArrayList<Marker> MarkersList =  new ArrayList<Marker>();
    ArrayList<Polyline> LinesList =  new ArrayList<Polyline>();
    ArrayList<String> LinesTitles = new ArrayList<String>();
    boolean slideup;
    LinearLayout slideupview;
    LinearLayout navbarview;
    Marker currentmarker;


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
        //Slide up code
        slideupview = findViewById(R.id.slideup);
        slideupview.setVisibility(View.INVISIBLE);
        slideup = false;
        //
        //navbar code
        navbarview = findViewById(R.id.navbar);
        navbarview.setVisibility(View.INVISIBLE);
        //
        Navigation = findViewById(R.id.NavButton);
        Navigation.setOnClickListener(this);
        NavGo = findViewById(R.id.navgo);
        NavGo.setOnClickListener(this);
        NavDone = findViewById(R.id.NavDone);
        NavDone.setOnClickListener(this);

        Search = findViewById(R.id.input_Search);

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
    private void SearchReady(){
        ArrayList<String> SearchList = new ArrayList<String>();
        for (Marker m : MarkersList){
            if (m.getTitle() != null){
                SearchList.add(m.getTitle());
            }
        }
        ArrayAdapter<String> adapterlist = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, SearchList);
        Search.setAdapter(adapterlist);

        Search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            //Search
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    //Search Method
                    Locate();
                }
                return false;
            }
        });
    }
    private void Locate(){
        String searchstring = Search.getText().toString();
        Marker searched = null;
        for (Marker m: MarkersList){
           if (searchstring.equals(m.getTitle())){
               searched = m;
               break;
           }
       }
        if (null != searched){
            onMarkerClick(searched);
        }
        else{
            Toast.makeText(getApplicationContext(), "No Results Found", Toast.LENGTH_SHORT).show();
        }
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
        LinesTitles.add("Meeting 119Meeting 117");
        LinesList.add(room119to118);
        LinesTitles.add("Meeting 119Meeting 118");
        LinesList.add(line);
        MarkerOptions Meeting118 = new MarkerOptions().position(new LatLng(28.593959,-81.304514)).title("Meeting 118");
        Marker room118 = mMap.addMarker(Meeting118);
        MarkersList.add(room118);
        MarkerOptions meeting117 =  new MarkerOptions().position(new LatLng(28.593929,-81.304514)).title("Meeting 117");
        Marker room117 = mMap.addMarker(meeting117);
        MarkersList.add(room117);
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
        });
        mMap.setOnCameraIdleListener(()->
        {
            for (GroundOverlay Overlay : groundOverlays) {
                Overlay.setVisible(mMap.getCameraPosition().zoom > 18);
            }
            for(Marker markers : MarkersList){
                markers.setVisible(mMap.getCameraPosition().zoom >20);
            }
        });
        //slide up code
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        //
        SearchReady();
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.user:
                startActivity(new Intent(this,Settings.class));
                break;
            case R.id.NavButton:

                Search.setVisibility(View.GONE);

                //Navbar
                navbarview.setVisibility(View.VISIBLE);

                /* TranslateAnimation animatedown = new TranslateAnimation(0, 0, navbarview.getHeight(), 0);
                   animatedown.setDuration(375);
                   animatedown.setFillAfter(true);
                   navbarview.startAnimation(animatedown);*/
                ArrayList<String> listfornav = new ArrayList<String>();

                for (Marker m : MarkersList){
                    if (m.getTitle() != null){
                        listfornav.add(m.getTitle());
                    }
                }
                //Creating Suggestions for text boxes in nav
                ArrayAdapter<String> adapterlist = new ArrayAdapter<String>(this,
                        android.R.layout.simple_dropdown_item_1line, listfornav);
                AutoCompleteTextView from = findViewById(R.id.From);
                from.setText("");
                from.setAdapter(adapterlist);
                AutoCompleteTextView destination = findViewById(R.id.Destination);
                destination.setAdapter(adapterlist);
                //
                //Autofill Destination
                destination.setText(currentmarker.getTitle());
                //

                //Turn all lines invisible
                for(Polyline line: LinesList){
                    line.setVisible(false);
                }
                break;
            case R.id.navgo:
                navbarview.setVisibility(View.GONE);
/*                TranslateAnimation animateup = new TranslateAnimation(0, 0, navbarview.getHeight(), 0);
                animateup.setDuration(375);
                animateup.setFillAfter(true);
                navbarview.startAnimation(animateup);*/
                //

                AutoCompleteTextView curlocation = findViewById(R.id.From);
                AutoCompleteTextView finaldestination = findViewById(R.id.Destination);
                String stringcurlocation = curlocation.getText().toString();
                String stringfinaldestination = finaldestination.getText().toString();
                String RooomtoRoom = "";


                int start = Integer.parseInt(stringcurlocation.replaceAll("[^0-9]", ""));
                int end = Integer.parseInt(stringfinaldestination.replaceAll("[^0-9]", ""));
                if (start > end) {
                    RooomtoRoom += curlocation.getText().toString();
                    RooomtoRoom += finaldestination.getText().toString();
                }
                else {
                    RooomtoRoom += finaldestination.getText().toString();
                    RooomtoRoom += curlocation.getText().toString();
                }
                Boolean wasFound = false;
                for (int i = 0; i<LinesTitles.size(); i++){
                    if (RooomtoRoom.equals(LinesTitles.get(i))) {
                        LinesList.get(i).setVisible(true);
                        wasFound = true;
                        break;
                    }
                }
                if (!wasFound){
                    Toast.makeText(getApplicationContext(), "Invalid route", Toast.LENGTH_SHORT).show();
                    break;
                }
                Marker curlocationmarker;
                for (Marker marker: MarkersList){
                    if (stringcurlocation.equals(marker.getTitle())){
                        curlocationmarker = marker;
                        break;
                    }
                }
                Marker finaldestinationmarker;
                for (Marker marker: MarkersList){
                    if (stringcurlocation.equals(marker.getTitle())){
                        finaldestinationmarker = marker;
                        break;
                    }
                }
                NavDone.setVisibility(View.VISIBLE);
                Search.setVisibility(View.VISIBLE);

                break;

            case R.id.NavDone:
                for (Polyline line: LinesList){
                    line.setVisible(false);
                }
                Search.setVisibility(View.VISIBLE);
                NavDone.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        if(mMap.getCameraPosition().zoom < 22){
            mMap.moveCamera(CameraUpdateFactory.zoomTo(22));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        marker.showInfoWindow();
        //Slide up code
        TextView text = slideupview.findViewById(R.id.roomnumber);
        text.setText(marker.getTitle());
        if (!slideup){
            slideupview.setVisibility(View.VISIBLE);
            TranslateAnimation animate = new TranslateAnimation(0, 0, slideupview.getHeight(), 0);
            animate.setDuration(375);
            animate.setFillAfter(true);
            slideupview.startAnimation(animate);
            slideup = true;
        }
        //
        currentmarker = marker;
        return false;
    }

    @Override
    public void onMapClick(LatLng point){
        //slide down
        if (slideup){
            slideupview.setVisibility(View.INVISIBLE);
            TranslateAnimation animate = new TranslateAnimation(0, 0, 0, slideupview.getHeight());
            animate.setDuration(375);
            animate.setFillAfter(true);
            slideupview.startAnimation(animate);
            slideup = false;
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
