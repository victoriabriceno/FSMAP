package com.example.logintesting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.logintesting.databinding.ActivityFavoritesBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.example.logintesting.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.view.animation.TranslateAnimation;

import org.json.JSONException;
import org.json.JSONObject;
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
    ArrayList<GroundOverlay> groundOverlays = new ArrayList<GroundOverlay>();
    ArrayList<Marker> MarkersList =  new ArrayList<Marker>();
    ArrayList<Polyline> linesShowing =  new ArrayList<Polyline>();
    ArrayList<PolylineOptions> customPolyLines = new ArrayList<>();
    ArrayList<Marker> markersClicked = new ArrayList<>();
    int clickCount = 0;
    ArrayList<String> LinesTitles = new ArrayList<String>();
    List<PatternItem> pattern = Arrays.asList(
            new Dash(30), new Gap(20), new Dot(), new Gap(20));
    double Latitude,Longitued;
    boolean slideup;
    LinearLayout slideupview;
    boolean DarkorLight;

    //Favorites
    ImageButton bntFavoritesRemove;
    ImageButton btnFavoritesAdd;
    Marker marker2;
    boolean isInMyFavorites = false;
    private FirebaseAuth firebaseAuth;



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

        //Favorites
        bntFavoritesRemove = (ImageButton) findViewById(R.id.btnRemoveFavorites);
        bntFavoritesRemove.setOnClickListener(this);

        btnFavoritesAdd = (ImageButton) findViewById(R.id.btnAddFavorites);
        btnFavoritesAdd.setOnClickListener(this);


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
                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),18f);
                            Latitude = currentLocation.getLatitude();
                            Longitued = currentLocation.getLongitude();
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
        PolylineOptions outSideTo119  = new PolylineOptions()
                .add(new LatLng(28.594075,-81.304381))
                .add(new LatLng(28.593989,-81.304386))
                .add(new LatLng(28.593989,-81.304484))
                .add(new LatLng(28.593989,-81.304514));
        PolylineOptions room119to118 = new PolylineOptions()
                .add(new LatLng(28.593989,-81.304514))
                .add(new LatLng(28.593989,-81.304484))
                .add(new LatLng(28.593959,-81.304484))
                .add(new LatLng(28.593959,-81.304514));
        PolylineOptions outSideTo118 = new PolylineOptions()
                .add(new LatLng(28.594075,-81.304381))
                .add(new LatLng(28.593989,-81.304386))
                .add(new LatLng(28.593989,-81.304484))
                .add(new LatLng(28.593959,-81.304484))
                .add(new LatLng(28.593959,-81.304514));
        PolylineOptions room119to117 =  new PolylineOptions()
                .add(new LatLng(28.593989,-81.304514))
                .add(new LatLng(28.593989,-81.304484))
                .add(new LatLng(28.593929,-81.304484))
                .add(new LatLng(28.593929,-81.304514));
        PolylineOptions outSideTo117 = new PolylineOptions()
                .add(new LatLng(28.594075,-81.304381))
                .add(new LatLng(28.593989,-81.304386))
                .add(new LatLng(28.593989,-81.304484))
                .add(new LatLng(28.593929,-81.304484))
                .add(new LatLng(28.593929,-81.304514));
        PolylineOptions room119to116 = new PolylineOptions()
                .add(new LatLng(28.593989,-81.304514))
                .add(new LatLng(28.593989,-81.304484))
                .add(new LatLng(28.593929,-81.304484))
                .add(new LatLng(28.593929,-81.304464))
                .add(new LatLng(28.593898,-81.304464))
                .add(new LatLng(28.593898, -81.304514));
        PolylineOptions room119to115 =new PolylineOptions()
                .add(new LatLng(28.593989,-81.304514))
                .add(new LatLng(28.593989,-81.304484))
                .add(new LatLng(28.593929,-81.304484))
                .add(new LatLng(28.593929,-81.304464))
                .add(new LatLng(28.593858,-81.304464))
                .add(new LatLng(28.593858, -81.304514));
        PolylineOptions room119to113 = new PolylineOptions()
                .add(new LatLng(28.593989,-81.304514))
                .add(new LatLng(28.593989,-81.304484))
                .add(new LatLng(28.593929,-81.304484))
                .add(new LatLng(28.593929,-81.304464))
                .add(new LatLng(28.593898,-81.304464))
                .add(new LatLng(28.593858,-81.304464))
                .add(new LatLng(28.593858,-81.304400))
                .add(new LatLng(28.593838,-81.304400))
                .add(new LatLng(28.593838,-81.304444));
        PolylineOptions room119toWaterZone = new PolylineOptions()
                .add(new LatLng(28.593989,-81.304514))
                .add(new LatLng(28.593989,-81.304484))
                .add(new LatLng(28.593929,-81.304484))
                .add(new LatLng(28.593929,-81.304464))
                .add(new LatLng(28.593898,-81.304464))
                .add(new LatLng(28.593858,-81.304464))
                .add(new LatLng(28.593858,-81.304400))
                .add(new LatLng(28.593818,-81.304400));
        customPolyLines.add(outSideTo119);
        LinesTitles.add("Outsideto119");
        customPolyLines.add(room119to118);
        LinesTitles.add("Meeting 119Meeting 118");
        customPolyLines.add(outSideTo118);
        LinesTitles.add("Outsideto118");
        customPolyLines.add(room119to117);
        LinesTitles.add("Meeting 119Meeting 117");
        customPolyLines.add(outSideTo117);
        LinesTitles.add("Outsideto117");
        customPolyLines.add(room119to117);
        LinesTitles.add("Meeting 119Meeting 117");
        customPolyLines.add(room119to116);
        LinesTitles.add("Meeting 119Meeting 116");
        customPolyLines.add(room119to115);
        LinesTitles.add("Meeting 119Meeting 115");
        customPolyLines.add(room119to113);
        LinesTitles.add("Meeting 119Boys Bathroom");
        customPolyLines.add(room119toWaterZone);
        LinesTitles.add("Meeting 119Water Zone");
        //add Marker
        MarkerOptions Meeting119 =  new MarkerOptions().position(new LatLng(28.593989,-81.304514)).title("Meeting 119");
        Marker room119 = mMap.addMarker(Meeting119);
        MarkersList.add(room119);

        MarkerOptions Meeting118 = new MarkerOptions().position(new LatLng(28.593959,-81.304514)).title("Meeting 118");
        Marker room118 = mMap.addMarker(Meeting118);
        MarkersList.add(room118);

        MarkerOptions meeting117 =  new MarkerOptions().position(new LatLng(28.593929,-81.304514)).title("Meeting 117");
        Marker room117 = mMap.addMarker(meeting117);
        MarkersList.add(room117);

        MarkerOptions Meeting116 = new MarkerOptions().position(new LatLng(28.593898,-81.304514)).title("Meeting 116");
        Marker room116 = mMap.addMarker(Meeting116);
        MarkersList.add(room116);

        MarkerOptions Meeting115 = new MarkerOptions().position(new LatLng(28.593858, -81.304514)).title("Meeting 115");
        Marker room115 = mMap.addMarker(Meeting115);
        MarkersList.add(room115);

        MarkerOptions BoysBathroom113 = new MarkerOptions().position(new LatLng(28.593838,-81.304444)).title("Boys Bathroom").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        Marker boysBathroom113 = mMap.addMarker(BoysBathroom113);
        MarkersList.add(boysBathroom113);

        MarkerOptions WaterZone = new MarkerOptions().position(new LatLng(28.593818,-81.304400)).title("Water Zone").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        Marker waterZone = mMap.addMarker(WaterZone);
        MarkersList.add(waterZone);



        for (Marker marker1: MarkersList)
        {
            marker1.setVisible(false);
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
            for (Marker markers : MarkersList) {
                markers.setVisible(mMap.getCameraPosition().zoom > 16);
            }
        });
        mMap.setOnCameraIdleListener(()->
        {
            for (GroundOverlay Overlay : groundOverlays) {
                Overlay.setVisible(mMap.getCameraPosition().zoom > 18);
            }
            for (Marker markers : MarkersList) {
                markers.setVisible(mMap.getCameraPosition().zoom > 16);
            }
        });
        //Slide up code
        slideupview = findViewById(R.id.slideup);
        slideupview.setVisibility(View.GONE);
        slideup = false;
        //
        NavGo = findViewById(R.id.navgo);
        NavGo.setOnClickListener(this);
        NavDone = findViewById(R.id.NavDone);
        NavDone.setOnClickListener(this);

        Search = findViewById(R.id.input_Search);

        //slide up code
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        //
        SearchReady();

        //Getting Darkmode
        DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference("/Users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/DarkMode/");
        mdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean DarkMode = (boolean) snapshot.getValue();
                    if (DarkMode) {
                        DarkorLight = true;
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(),R.raw.style_json));
                        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        for (PolylineOptions lines1: customPolyLines)
                        {
                            lines1.pattern(pattern);
                            lines1.width(15);
                            lines1.color(Color.BLUE);
                        }
                    } else {
                        DarkorLight = false;
                        mMap.setMapStyle(null);
                        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        for (PolylineOptions lines1: customPolyLines)
                        {
                            lines1.pattern(pattern);
                            lines1.width(15);
                            lines1.color(Color.parseColor("#FFA500"));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if(DarkorLight)
        {
            for (PolylineOptions lines1: customPolyLines)
            {
                lines1.pattern(pattern);
                lines1.width(15);
                lines1.color(Color.BLUE);
            }
        }
        else
        {
            for (PolylineOptions lines1: customPolyLines)
            {
                lines1.pattern(pattern);
                lines1.width(15);
                lines1.color(Color.parseColor("#FFA500"));
            }
        }
    }




    @Override
    public void onClick(View view) {
        //checkISFavorites();

        switch(view.getId()){
            case R.id.user:
                startActivity(new Intent(this,Settings.class));
                break;
            case R.id.navgo:
                //

                AutoCompleteTextView curlocation = findViewById(R.id.From);
                AutoCompleteTextView finaldestination = findViewById(R.id.Destination);
                String stringcurlocation = curlocation.getText().toString();
                String stringfinaldestination = finaldestination.getText().toString();
                String RooomtoRoom = "";

                while(linesShowing.size() != 0){
                    linesShowing.get(0).remove();
                    linesShowing.remove(0);
                }
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
                        linesShowing.add(mMap.addPolyline(customPolyLines.get(i)));
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
                slideupview.setVisibility(View.GONE);
                slideup = false;
                NavDone.setVisibility(View.VISIBLE);
                Search.setVisibility(View.VISIBLE);
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(slideupview.getWindowToken(), 0);

                break;

            case R.id.NavDone:
                while(linesShowing.size() != 0){
                    linesShowing.get(0).remove();
                    linesShowing.remove(0);
                }
                Search.setVisibility(View.VISIBLE);
                NavDone.setVisibility(View.GONE);
                break;

            case R.id.btnRemoveFavorites:
                Favorites.removeFromFavorite(MapsActivity.this,marker2);
                break;

            case R.id.btnAddFavorites:
                Favorites.addToFavorite(MapsActivity.this,marker2);
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
    public boolean onMarkerClick(Marker marker) {
        if(mMap.getCameraPosition().zoom < 22){
            mMap.moveCamera(CameraUpdateFactory.zoomTo(22));
        }
        if(clickCount == 0) {
            clickCount++;
            getDirectionPoly(marker);
            markersClicked.add(marker);
        }
        if(!markersClicked.get(0).equals(marker)) {
            clickCount++;
            for (int i = 0; i < linesShowing.size(); i++) {
                linesShowing.get(0).remove();
                linesShowing.remove(0);
            }
            markersClicked.add(marker);
            getDirectionPoly(marker);
        }
        else  {
            if(clickCount != 1) {
                Toast.makeText(getApplicationContext(), "Clicked On the Same Marker", Toast.LENGTH_SHORT).show();
            }
        }
        if(markersClicked.size()>1)
        {
            markersClicked.remove(0);
        }
        /*marker.showInfoWindow();*/
        //Slide up code
        TextView text = slideupview.findViewById(R.id.roomnumber);
        text.setText(marker.getTitle());
        if (!slideup){
            slideupview.setVisibility(View.VISIBLE);
          //Animation
            /*  TranslateAnimation animate = new TranslateAnimation(0, 0, slideupview.getHeight(), 0);
            animate.setDuration(375);
            animate.setFillAfter(true);
            slideupview.startAnimation(animate);*/
            slideup = true;
        }


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
        destination.setText(markersClicked.get(0).getTitle());
        for (Marker m : MarkersList)
        {
            if(!m.equals(marker)) {
                m.setVisible(false);
            }
        }
        marker2 = marker;
        return false;
    }

    @Override
    public void onMapClick(LatLng point){
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(slideupview.getWindowToken(), 0);
        //slide down
        if (slideup){
            slideupview.setVisibility(View.GONE);
            //Animation stuff that bugs out
            /*TranslateAnimation animate = new TranslateAnimation(0, 0, 0, slideupview.getHeight());
            animate.setDuration(375);
            animate.setFillAfter(true);
            slideupview.startAnimation(animate);*/
            slideup = false;
        }
        for (Marker m : MarkersList)
        {
                m.setVisible(true);
        }
    }

    public void getDirectionPoly(Marker marker)
    {
        String url = getUrl(new LatLng(Latitude, Longitued), marker.getPosition());
        TaskRequestDirections taskRequestDirections = new TaskRequestDirections(marker);
        taskRequestDirections.execute(url);
    }

    private String getUrl(LatLng origin, LatLng dest)
    {
        String str_org = "origin="+ origin.latitude+","+ origin.longitude;

        String str_dest = "destination=" + dest.latitude + ","+ dest.longitude;

        String sensor =  "sensor=false";

        String mode = "mode=walking";

        String param = str_org  + "&" + str_dest + "&" + sensor + "&" + mode;

        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&key=AIzaSyBXJ-PMOg2kDp8jXih-3ME_52znZc6A2ds ";

        return url;
    }

    private String getDirectionsUrl(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader =  new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine())!=null)
            {
                stringBuffer.append(line);
            }
            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (inputStream != null)    {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }
    public class TaskRequestDirections extends AsyncTask<String,Void,String>{
        Marker marker;
        public TaskRequestDirections() {}
        public TaskRequestDirections(Marker _marker) {
            marker = _marker;
        }
        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = getDirectionsUrl(strings[0]);
            }catch (IOException e){
                e.printStackTrace();
            }
            return responseString;
        }
        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            //parse json here
            TaskParser taskParser = new TaskParser(marker);
            taskParser.execute(s);
        }
    }
    public class TaskParser  extends AsyncTask<String, Void,List<List<HashMap<String,String>>>>{
        Marker marker;
        public TaskParser() {}
        public TaskParser(Marker _marker) {
            marker = _marker;
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject= null;
            List<List<HashMap<String, String>>> routes = null;
            try{
                jsonObject = new JSONObject(strings[0]);
                DataParser dataParser = new DataParser();
                routes = dataParser.parse(jsonObject);
            }catch (JSONException e){
                e.printStackTrace();
            }
            return routes;
        }
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists){
            ArrayList points = null;
            PolylineOptions polylineOptions = null;

            for (List<HashMap<String, String>> path: lists){
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String,String>point: path){
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat,lon));
                }

                polylineOptions.addAll(points);
                polylineOptions.width(15);
                if (DarkorLight){
                    polylineOptions.color(Color.BLUE);
                }
                else{
                    polylineOptions.color(Color.parseColor("#FFA500"));
                }
                polylineOptions.geodesic(true);
            }
            if(polylineOptions!= null){
                List<LatLng> outToInPoly = customPolyLines.get(0).getPoints();
                polylineOptions.add(outToInPoly.get(0));
                if (marker != null) {
                    switch (marker.getTitle()) {
                        case "Meeting 119":
                            linesShowing.add(mMap.addPolyline(customPolyLines.get(0)));
                            break;
                        case "Meeting 118":
                            linesShowing.add(mMap.addPolyline(customPolyLines.get(2)));
                            break;
                        case "Meeting 117":
                            linesShowing.add(mMap.addPolyline(customPolyLines.get(4)));
                            break;
                    }
                }
                linesShowing.add(mMap.addPolyline(polylineOptions));
            }else
                Toast.makeText(getApplicationContext(),"Direction Not Found",Toast.LENGTH_SHORT).show();
        }
    }

}

