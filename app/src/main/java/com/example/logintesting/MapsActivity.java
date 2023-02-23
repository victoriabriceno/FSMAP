package com.example.logintesting;
//Woohoo, imports

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.logintesting.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.PolyUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

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

import de.hdodenhof.circleimageview.CircleImageView;

//Main Maps Screen

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,View.OnClickListener, GoogleMap.OnMarkerClickListener,
GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {
    private static final long GPS_UPDATE_TIME = 10;
    //Global Variables used throughout the program,
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    protected ActivityMapsBinding binding;
    private CircleImageView userIconMaps;
    private ImageView mGps;
    private Button Set;
    private Button RemovePoint;
    private ImageView ZoomIn;
    private ImageView ZoomOut;
    private Button NavGo;
    private Button NavDone;
    private Button NacLock;
    private AutoCompleteTextView Search;
    private static final String FINE_lOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationPermissionsGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE= 1234;
    ArrayList<GroundOverlay> groundOverlaysf1 = new ArrayList<GroundOverlay>();
    ArrayList<GroundOverlay> groundOverlaysf2 = new ArrayList<GroundOverlay>();
    ArrayList<Marker> MarkersList =  new ArrayList<Marker>();
    ArrayList<Polyline> linesShowing =  new ArrayList<Polyline>();
    ArrayList<PolylineOptions> customPolyLines = new ArrayList<>();
    ArrayList<Marker> markersClicked = new ArrayList<>();
    ArrayList<Marker> createdMarkers = new ArrayList<>();
    ArrayList<Marker> secondFloorMarkersList = new ArrayList<Marker>();
    ArrayList<Marker> favoritedMarkers;
    ArrayList<Marker> BathroomMarkers = new ArrayList<>();
    ArrayList<Marker> ClassRoomMarkers = new ArrayList<>();
    ArrayList<Marker> AdminMarkers = new ArrayList<>();
    ArrayList<Marker> WaterZones = new ArrayList<>();
    int clickCount = 0;
    ArrayList<String> LinesTitles = new ArrayList<String>();
    List<PatternItem> pattern = Arrays.asList(
            new Dash(30), new Gap(20), new Dot(), new Gap(20));
    double Latitude,Longitued;
    float zoom;
    boolean slideup;
    double mLastAltitude;
    RelativeLayout slideupview;
    ArrayList<String> nameslist = new ArrayList<String>() {};
    boolean DarkorLight;
    RelativeLayout saveSpotLayout;
    //Favorites
    ImageButton bntFavoritesRemove;
    ImageButton btnFavoritesAdd;
    Button upFloor;
    Button downFloor;
    Marker marker2;
    boolean isInMyFavorites = false;
    private FirebaseAuth firebaseAuth;
    Marker createdMarker;
    boolean wasRemoveHit = false;
    private boolean FollowUser= false;
    boolean wasMarkerClicked = false;
    String SquidCheck = "SquidWard Community College";
    LatLng LongClickPoint;
    LatLng checkDistPoint;
    //Variables from profile Picture
    Snackbar snack;
    FirebaseAuth fAuth;
    StorageReference storageReference;
    LocationManager mLocationManager;
    String markerTitle2;
    boolean isNOTfUCKED = false;
    int floorPicked = 1;
    CameraPosition cameraLoad;
    int altitudesCollectedNumber;


    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            ComparePoints(location);

        }
    };

    OnNmeaMessageListener onNmeaMessageListener = (nmea, timestamp) -> {
        if (nmea.startsWith("$")) {
            String[] Tokens = nmea.split(",");
            String token = Tokens[0];
            if (token.endsWith("GGA")) {
                if (Tokens.length > 10) {
                    if (Tokens[9].length() > 0) {
                        try {
                            mLastAltitude = Float.parseFloat(Tokens[9]);
                        } catch (NumberFormatException ex) {
                            mLastAltitude = 0;
                        }
                    } else {
                        mLastAltitude = 0.0f;
                    }
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraPosition mMyCam = mMap.getCameraPosition();
        double longitude = mMyCam.target.longitude;
        double latitude = mMyCam.target.latitude;

        SharedPreferences settings = getSharedPreferences("SOME_NAME", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat("longitude",(float) longitude);
        editor.putFloat("latitude",(float) latitude);
        editor.putInt("floorPicked",floorPicked);
        editor.putFloat("zoom",mMyCam.zoom);
        editor.commit();


    }

    //onCreate gets rebuilt each time the map is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                //Extra bundle is null
                isNOTfUCKED = false;
            } else {
                markerTitle2 = extras.getString("marker_ToMap");
                isNOTfUCKED = true;
            }
        }


        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        userIconMaps = findViewById(R.id.userMaps);
        userIconMaps.setOnClickListener(this);

        mGps = (ImageView) findViewById(R.id.gps);

        getLocationPermission();
        //Slide up code
        slideupview = findViewById(R.id.slideup);
        slideupview.setVisibility(View.INVISIBLE);
        slideup = false;

        //save spot code
        saveSpotLayout = findViewById(R.id.saveSpotLayout);
        //Favorites
        bntFavoritesRemove = (ImageButton) findViewById(R.id.btnRemoveFavorites);
        bntFavoritesRemove.setOnClickListener(this);

        btnFavoritesAdd = (ImageButton) findViewById(R.id.btnAddFavorites);
        btnFavoritesAdd.setOnClickListener(this);


        //PROFILE PICTURE
        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("Users/"+fAuth.getCurrentUser().getUid()+"/ProfilePicture.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(userIconMaps);
            }
        });
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                        .RequestMultiplePermissions(), result -> {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        Boolean fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                    }
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        Boolean coarseLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION, false);
                    }
                });

    }
    @SuppressLint("MissingPermission")
    public void registerLocationManager() {
        mLocationManager = (LocationManager) MapsActivity.this.getSystemService(LOCATION_SERVICE);
        if (mLocationPermissionsGranted) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mLocationManager.addNmeaListener(onNmeaMessageListener);
            }
        }
    }
    //Function to obtain device location and store in Latitude and Longitued
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
    public void ComparePoints(Location location)
    {
        Polyline line;
        int ixLastPoint = 0;
        List<LatLng> points;

       if(linesShowing.size() >0) {
               line = linesShowing.get(0);
                points = line.getPoints();
            for (int i = 0; i < points.size()-1; i++) {
                LatLng point1 = points.get(i);
                LatLng point2 = points.get(i+1);
                List<LatLng> currentSegment = new ArrayList<>();
                currentSegment.add(point1);
                currentSegment.add(point2);
                if(PolyUtil.isLocationOnPath(new LatLng(location.getLatitude(),location.getLongitude()), currentSegment,true,30))
                {
                  LatLng snappedtOSegment = getMarkerProjectionOnSegment(new LatLng(location.getLatitude(),location.getLongitude()),currentSegment,mMap.getProjection());
                  break;
                }
            }
             List<LatLng> pathPoints = points;
            for (int i = 0; i < ixLastPoint; i++) {
                pathPoints.remove(0);
            }
//            line.setPoints(pathPoints);
//            double earthRadius = 3958.75;
//            double dLat = Math.toRadians(checkDistPoint.latitude-location.getLatitude());
//            double dLng = Math.toRadians(checkDistPoint.longitude-location.getLongitude());
//            double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
//                    Math.cos(Math.toRadians(Latitude)) * Math.cos(Math.toRadians(checkDistPoint.latitude)) *
//                            Math.sin(dLng/2) * Math.sin(dLng/2);
//            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
//            double dist = earthRadius * c;
//            if(dist>0.001)
//            {
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Latitude,Longitued)));
//                checkDistPoint = new LatLng(Latitude,Longitued);
//            }

        }


    }
    private LatLng getMarkerProjectionOnSegment(LatLng carPos, List<LatLng> segment, Projection projection) {
        LatLng markerProjection = null;

        Point carPosOnScreen = projection.toScreenLocation(carPos);
        Point p1 = projection.toScreenLocation(segment.get(0));
        Point p2 = projection.toScreenLocation(segment.get(1));
        Point carPosOnSegment = new Point();

        float denominator = (p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y);
        // p1 and p2 are the same
        if (Math.abs(denominator) <= 1E-10) {
            markerProjection = segment.get(0);
        } else {
            float t = (carPosOnScreen.x * (p2.x - p1.x) - (p2.x - p1.x) * p1.x
                    + carPosOnScreen.y * (p2.y - p1.y) - (p2.y - p1.y) * p1.y) / denominator;
            carPosOnSegment.x = (int) (p1.x + (p2.x - p1.x) * t);
            carPosOnSegment.y = (int) (p1.y + (p2.y - p1.y) * t);
            markerProjection = projection.fromScreenLocation(carPosOnSegment);
        }
        return markerProjection;
    }

    private void getDeviceLocationCameraMove(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted){
                @SuppressLint("MissingPermission") Task Location = fusedLocationClient.getLastLocation();
                Location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
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

    //Functions for moving the cammer, overload for zoom option
    private void moveCamera(LatLng latLng){
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }
    private void moveCamera(LatLng latlng,float zoom){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
    }
    //Init runs on map start, used for currentlocation button
    private void Init(){
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocationCameraMove();
            }
        });
    }

    //Runs on map ready, used for search bar
    private void SearchReady(){
        ArrayList<String> SearchList = new ArrayList<String>();
        for (Marker m : MarkersList){
            if (m.getTitle() != null){
                SearchList.add(m.getTitle());
            }
        }
        ArrayAdapter<String> adapterlist = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, SearchList);
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
    //used for searchbar search function
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


    private void LoadFavoriteMarkers()
    {
        favoritedMarkers= new ArrayList<>();
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("/Users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/Favorites/");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Marker found = FindTheMarker(dataSnapshot.getKey());
                    if(found != null)
                    {
                        if(CheckMarkerType(found))
                        {
                            for (Marker m: createdMarkers)
                            {
                                if(m.getTitle().equals(found.getTitle()))
                                {
                                    favoritedMarkers.add(found);
                                }
                            }
                        }
                        else
                        {
                            for(Marker m: MarkersList)
                            {
                                if(m.getTitle().equals(found.getTitle()))
                                {
                                    favoritedMarkers.add(found);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
    //On map ready sets up the map and many variables (200 lines lmao)
    //There are comments inside the function
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        SharedPreferences settings = getSharedPreferences("SOME_NAME", 0);
        float longitude = settings.getFloat("longitude",(float) Longitued);
        float latitude = settings.getFloat("latitude",(float)Latitude);
        float zoomload = settings.getFloat("zoom", zoom);
        floorPicked = settings.getInt("floorPicked",floorPicked);


        LatLng startPosition = new LatLng(latitude,longitude);
        cameraLoad = new CameraPosition.Builder()
                .target(startPosition)
                .zoom(zoomload)
                .build();
        registerLocationManager();

        if(mLocationPermissionsGranted) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    GPS_UPDATE_TIME, 1, locationListener);
        }
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
        moveCamera(mMap.getCameraPosition().target,16f);
        //Location tracking
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
        //Creating polylines
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
        PolylineOptions room118to117 = new PolylineOptions()
                .add(new LatLng(28.593959,-81.304514))
                .add(new LatLng(28.593959,-81.304484))
                .add(new LatLng(28.593929,-81.304484))
                .add(new LatLng(28.593929,-81.304514));
        PolylineOptions room118to116 = new PolylineOptions()
                .add(new LatLng(28.593959,-81.304514))
                .add(new LatLng(28.593959,-81.304484))
                .add(new LatLng(28.593929,-81.304484))
                .add(new LatLng(28.593929,-81.304464))
                .add(new LatLng(28.593898,-81.304464))
                .add(new LatLng(28.593898, -81.304514));
        PolylineOptions room118to115 = new PolylineOptions()
                .add(new LatLng(28.593959,-81.304514))
                .add(new LatLng(28.593959,-81.304484))
                .add(new LatLng(28.593929,-81.304484))
                .add(new LatLng(28.593929,-81.304464))
                .add(new LatLng(28.593858,-81.304464))
                .add(new LatLng(28.593858, -81.304514));
        PolylineOptions room118to113 = new PolylineOptions()
                .add(new LatLng(28.593959,-81.304514))
                .add(new LatLng(28.593959,-81.304484))
                .add(new LatLng(28.593929,-81.304484))
                .add(new LatLng(28.593929,-81.304464))
                .add(new LatLng(28.593898,-81.304464))
                .add(new LatLng(28.593858,-81.304464))
                .add(new LatLng(28.593858,-81.304400))
                .add(new LatLng(28.593818,-81.304400));
        PolylineOptions room118toWaterZone = new PolylineOptions()
                .add(new LatLng(28.593959,-81.304514))
                .add(new LatLng(28.593959,-81.304484))
                .add(new LatLng(28.593929,-81.304484))
                .add(new LatLng(28.593929,-81.304464))
                .add(new LatLng(28.593898,-81.304464))
                .add(new LatLng(28.593858,-81.304464))
                .add(new LatLng(28.593858,-81.304400))
                .add(new LatLng(28.593818,-81.304400));
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
        PolylineOptions room117To116 = new PolylineOptions()
                .add(new LatLng(28.593929,-81.304514))
                .add(new LatLng(28.593929,-81.304464))
                .add(new LatLng(28.593898,-81.304464))
                .add(new LatLng(28.593898, -81.304514));
        PolylineOptions room117to115 = new PolylineOptions()
                .add(new LatLng(28.593929,-81.304514))
                .add(new LatLng(28.593929,-81.304484))
                .add(new LatLng(28.593929,-81.304464))
                .add(new LatLng(28.593858,-81.304464))
                .add(new LatLng(28.593858, -81.304514));
        PolylineOptions room117to113 = new PolylineOptions()
                .add(new LatLng(28.593929,-81.304514))
                .add(new LatLng(28.593929,-81.304484))
                .add(new LatLng(28.593929,-81.304464))
                .add(new LatLng(28.593898,-81.304464))
                .add(new LatLng(28.593858,-81.304464))
                .add(new LatLng(28.593858,-81.304400))
                .add(new LatLng(28.593838,-81.304400))
                .add(new LatLng(28.593838,-81.304444));
        PolylineOptions room117toWater = new PolylineOptions()
                .add(new LatLng(28.593929,-81.304514))
                .add(new LatLng(28.593929,-81.304484))
                .add(new LatLng(28.593929,-81.304464))
                .add(new LatLng(28.593898,-81.304464))
                .add(new LatLng(28.593858,-81.304464))
                .add(new LatLng(28.593858,-81.304400))
                .add(new LatLng(28.593818,-81.304400));
        PolylineOptions room119to116 = new PolylineOptions()
                .add(new LatLng(28.593989,-81.304514))
                .add(new LatLng(28.593989,-81.304484))
                .add(new LatLng(28.593929,-81.304484))
                .add(new LatLng(28.593929,-81.304464))
                .add(new LatLng(28.593898,-81.304464))
                .add(new LatLng(28.593898, -81.304514));
        PolylineOptions room116to115 = new PolylineOptions()
                .add(new LatLng(28.593898, -81.304514))
                .add(new LatLng(28.593898,-81.304464))
                .add(new LatLng(28.593858,-81.304464))
                .add(new LatLng(28.593858, -81.304514));
        PolylineOptions room116to113 = new PolylineOptions()
                .add(new LatLng(28.593898, -81.304514))
                .add(new LatLng(28.593898,-81.304464))
                .add(new LatLng(28.593858,-81.304464))
                .add(new LatLng(28.593858,-81.304400))
                .add(new LatLng(28.593838,-81.304400))
                .add(new LatLng(28.593838,-81.304444));
        PolylineOptions room116toWaterZone = new PolylineOptions()
                .add(new LatLng(28.593898, -81.304514))
                .add(new LatLng(28.593898,-81.304464))
                .add(new LatLng(28.593898,-81.304464))
                .add(new LatLng(28.593858,-81.304464))
                .add(new LatLng(28.593858,-81.304400))
                .add(new LatLng(28.593818,-81.304400));
        PolylineOptions outsideTo116 = new PolylineOptions()
                .add(new LatLng(28.594075,-81.304381))
                .add(new LatLng(28.593989,-81.304386))
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
        PolylineOptions room115to113 = new PolylineOptions()
                .add(new LatLng(28.593858, -81.304514))
                .add(new LatLng(28.593858,-81.304464))
                .add(new LatLng(28.593858,-81.304464))
                .add(new LatLng(28.593858,-81.304400))
                .add(new LatLng(28.593838,-81.304400))
                .add(new LatLng(28.593838,-81.304444));
        PolylineOptions room115toWaterZone = new PolylineOptions()
                .add(new LatLng(28.593858, -81.304514))
                .add(new LatLng(28.593858,-81.304464))
                .add(new LatLng(28.593929,-81.304464))
                .add(new LatLng(28.593898,-81.304464))
                .add(new LatLng(28.593858,-81.304464))
                .add(new LatLng(28.593858,-81.304400))
                .add(new LatLng(28.593818,-81.304400));
        PolylineOptions outsideTo115 = new PolylineOptions()
                .add(new LatLng(28.594075,-81.304381))
                .add(new LatLng(28.593989,-81.304386))
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
        PolylineOptions outsideTo113 = new PolylineOptions()
                .add(new LatLng(28.594075,-81.304381))
                .add(new LatLng(28.593989,-81.304386))
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
        PolylineOptions room113ToWaterZone = new PolylineOptions()
                .add(new LatLng(28.593838,-81.304444))
                .add(new LatLng(28.593838,-81.304400))
                .add(new LatLng(28.593858,-81.304400))
                .add(new LatLng(28.593818,-81.304400));
        PolylineOptions outsideToWaterZone = new PolylineOptions()
                .add(new LatLng(28.594075,-81.304381))
                .add(new LatLng(28.593989,-81.304386))
                .add(new LatLng(28.593989,-81.304484))
                .add(new LatLng(28.593929,-81.304484))
                .add(new LatLng(28.593929,-81.304464))
                .add(new LatLng(28.593898,-81.304464))
                .add(new LatLng(28.593858,-81.304464))
                .add(new LatLng(28.593858,-81.304400))
                .add(new LatLng(28.593818,-81.304400));
        //Adding polylines to list for lines and string list for searching
        customPolyLines.add(outSideTo119);
        LinesTitles.add("outsideToMeeting 119");
        customPolyLines.add(room119to118);
        LinesTitles.add("Meeting 119Meeting 118");
        customPolyLines.add(outSideTo118);
        LinesTitles.add("outsideToMeeting 118");
        customPolyLines.add(room118to117);
        LinesTitles.add("Meeting 118Meeting 117");
        customPolyLines.add(room118to116);
        LinesTitles.add("Meeting 118Meeting 116");
        customPolyLines.add(room118to115);
        LinesTitles.add("Meeting 118Meeting 115");
        customPolyLines.add(room118to113);
        LinesTitles.add("Meeting 118Boys Bathroom (113)");
        customPolyLines.add(room118toWaterZone);
        LinesTitles.add("Meeting 118Water Zone(112)");
        customPolyLines.add(room119to117);
        LinesTitles.add("Meeting 119Meeting 117");
        customPolyLines.add(outSideTo117);
        LinesTitles.add("outsideToMeeting 117");
        customPolyLines.add(room119to117);
        LinesTitles.add("Meeting 119Meeting 117");
        customPolyLines.add(room117To116);
        LinesTitles.add("Meeting 117Meeting 116");
        customPolyLines.add(room117to115);
        LinesTitles.add("Meeting 117Meeting 115");
        customPolyLines.add(room117to113);
        LinesTitles.add("Meeting 117Boys Bathroom (113)");
        customPolyLines.add(room117toWater);
        LinesTitles.add("Meeting 117Water Zone (112)");
        customPolyLines.add(room116to115);
        LinesTitles.add("Meeting 116Meeting 115");
        customPolyLines.add(room116to113);
        LinesTitles.add("Meeting 116Boys Bathroom (113)");
        customPolyLines.add(room116toWaterZone);
        LinesTitles.add("Meeting 116Water Zone (112)");
        customPolyLines.add(room119to116);
        LinesTitles.add("Meeting 119Meeting 116");
        customPolyLines.add(outsideTo116);
        LinesTitles.add("outsideToMeeting 116");
        customPolyLines.add(outsideTo115);
        LinesTitles.add("outsideToMeeting 115");
        customPolyLines.add(room115to113);
        LinesTitles.add("Meeting 115Boys Bathroom (113)");
        customPolyLines.add(room115toWaterZone);
        LinesTitles.add("Meeting 115Water Zone (112)");
        customPolyLines.add(room119to115);
        LinesTitles.add("Meeting 119Meeting 115");
        customPolyLines.add(room119to113);
        LinesTitles.add("Meeting 119Boys Bathroom (113)");
        customPolyLines.add(outsideTo113);
        LinesTitles.add("outsideToBoys Bathroom (113)");
        customPolyLines.add(room113ToWaterZone);
        LinesTitles.add("Boys Bathroom (113)Water Zone (112)");
        customPolyLines.add(room119toWaterZone);
        LinesTitles.add("Meeting 119Water Zone (112)");
        customPolyLines.add(outsideToWaterZone);
        LinesTitles.add("outsideToWater Zone (112)");
        //add Marker
        MarkerOptions Meeting119 =  new MarkerOptions().position(new LatLng(28.593974,-81.304508)).title("Meeting 119");
        Marker room119 = mMap.addMarker(Meeting119);
        ClassRoomMarkers.add(room119);
        MarkersList.add(room119);

        MarkerOptions Meeting118 = new MarkerOptions().position(new LatLng(28.593945,-81.304514)).title("Meeting 118");
        Marker room118 = mMap.addMarker(Meeting118);
        ClassRoomMarkers.add(room118);
        MarkersList.add(room118);

        MarkerOptions meeting117 =  new MarkerOptions().position(new LatLng(28.593919,-81.304514)).title("Meeting 117");
        Marker room117 = mMap.addMarker(meeting117);
        ClassRoomMarkers.add(room117);
        MarkersList.add(room117);

        MarkerOptions Meeting116 = new MarkerOptions().position(new LatLng(28.593890,-81.304514)).title("Meeting 116");
        Marker room116 = mMap.addMarker(Meeting116);
        ClassRoomMarkers.add(room116);
        MarkersList.add(room116);

        MarkerOptions Meeting115 = new MarkerOptions().position(new LatLng(28.593858, -81.304514)).title("Meeting 115");
        Marker room115 = mMap.addMarker(Meeting115);
        ClassRoomMarkers.add(room115);
        MarkersList.add(room115);

        MarkerOptions BoysBathroom113 = new MarkerOptions().position(new LatLng(28.593818,-81.304444)).title("Boys Bathroom (113)");
        Marker boysBathroom113 = mMap.addMarker(BoysBathroom113);
        BathroomMarkers.add(boysBathroom113);
        MarkersList.add(boysBathroom113);

        MarkerOptions WaterZone = new MarkerOptions().position(new LatLng(28.593818,-81.304400)).title("Water Zone (112)");
        Marker waterZone = mMap.addMarker(WaterZone);
        WaterZones.add(waterZone);
        MarkersList.add(waterZone);

        MarkerOptions SCC = new MarkerOptions().position(new LatLng(28.595085, -81.308305)).title("Squidward Community College");
        Marker SquidCC = mMap.addMarker(SCC);
        MarkersList.add(SquidCC);

        MarkerOptions secondFloorTestMarker =  new MarkerOptions().position(new LatLng(28.59557353825031, -81.30422210103595)).title("Second");
        Marker secondFloorTestMarkerMarker = mMap.addMarker(secondFloorTestMarker);
        secondFloorMarkersList.add(secondFloorTestMarkerMarker);


        for (Marker marker1: MarkersList)
        {
            if(!marker1.getTitle().equals(SquidCheck)) {
                marker1.setVisible(false);
            }
        }
        for(Marker marker:secondFloorMarkersList)
        {
            marker.setVisible(false);
        }
        BitmapDescriptor build3aF1BitMap = BitmapDescriptorFactory.fromResource(R.drawable.building_3a_blackmoore_1f_rotated);

        //Set the bounds for overlay
        LatLngBounds buildLibrary = new LatLngBounds(
                new LatLng(28.59379993356988, -81.30450729197996),
                new LatLng(28.594005193975605, -81.30415971195876));
        LatLngBounds build3A =  new LatLngBounds(
                new LatLng(28.595392200538452, -81.30425629914613),
                new LatLng(28.59565596435769, -81.30393979848783));
        LatLngBounds build3AF2 =  new LatLngBounds(
                new LatLng(28.595392200538452, -81.30425629914613),
                new LatLng(28.59565596435769, -81.30393979848783));
//        LatLngBounds build3B =  new LatLngBounds(
//                new LatLng(28.59489939800887, -81.30421001414925),
//                new LatLng(28.595410442208898, -81.30359042388629));
//        LatLngBounds build3BConnect = new LatLngBounds(
//                new LatLng(28.594658645277548, -81.30423153222328),
//                new LatLng(28.594876487499718, -81.30377019229515));
//        LatLngBounds build3C = new LatLngBounds(
//                new LatLng(28.594253533934957, -81.3042093605151),
//                new LatLng(28.59463740843301, -81.30378020710396));
//        LatLngBounds build3CMP = new LatLngBounds(
//                new LatLng(28.59401920494417, -81.30419863168258),
//                new LatLng(28.59422291811299, -81.30397734945726));
//        LatLngBounds build3F = new LatLngBounds(
//                new LatLng(28.593322903500177, -81.30542386327811),
//                new LatLng(28.59398467985881, -81.30454409878536));
//        LatLngBounds build4C = new LatLngBounds(
//                new LatLng(28.591369859267225, -81.3042459017761),
//                new LatLng(28.591409896421553, -81.30413526066346));
//        LatLngBounds build4B = new LatLngBounds(
//                new LatLng(28.591588937000083, -81.30422856031794),
//                new LatLng(28.592005130777046, -81.30363824532108));
//        LatLngBounds build4A = new LatLngBounds(
//                new LatLng(28.5926339005131, -81.30546789052039),
//                new LatLng(28.592869410133137, -81.30476246959658));
//        LatLngBounds build4AWD2 = new LatLngBounds(
//                new LatLng(28.592030993472754, -81.30421731065695),
//                new LatLng(28.592066320152245, -81.30417908918166));
//        LatLngBounds build4AFC =  new LatLngBounds(
//                new LatLng(28.59226717692391, -81.30473598372107),
//                new LatLng(28.592838288837115, -81.30406274931435));
//        LatLngBounds build4D =  new LatLngBounds(
//                new LatLng(28.59059641684985, -81.30456270361756),
//                new LatLng(28.590932024410755, -81.30418853548505));
//        LatLngBounds build4E = new LatLngBounds(
//                new LatLng(28.590101835337443, -81.30483226561324),
//                new LatLng(28.59086254784375, -81.30463378216082));
        //create map overlap

        GroundOverlayOptions buildLibraryOverlay = new GroundOverlayOptions()
                .positionFromBounds(buildLibrary)
                .image(BitmapDescriptorFactory.fromResource(R.drawable.buildinglibrary_rotated_1_left))
                .anchor(0.43f,0.45f);
        GroundOverlayOptions build3aOverlay =  new GroundOverlayOptions()
                .positionFromBounds(build3A)
                .image(build3aF1BitMap)
                .anchor(1.0f,-0.1f)
                .bearing(-2);
        GroundOverlayOptions build3aF2Overlay =  new GroundOverlayOptions()
                .positionFromBounds(build3A)
                .image(BitmapDescriptorFactory.fromResource(R.drawable.building_3a_blackmoore_2f))
                .anchor(1.0f,-0.1f)
                .bearing(-2);
//        GroundOverlayOptions building3BOverlay = new GroundOverlayOptions()
//                .positionFromBounds(build3B)
//                .image(BitmapDescriptorFactory.fromResource(R.drawable.building_3b_fishbowl))
//                .anchor(0.45f,0.45f);
//        GroundOverlayOptions build3BConnected = new GroundOverlayOptions()
//                .positionFromBounds(build3BConnect)
//                .image(BitmapDescriptorFactory.fromResource(R.drawable.building_3b_gd));
//        GroundOverlayOptions build3COverlay = new GroundOverlayOptions()
//                .positionFromBounds(build3C)
//                .image(BitmapDescriptorFactory.fromResource(R.drawable.building_3c_gd));
//        GroundOverlayOptions build3CMPOverlay = new GroundOverlayOptions()
//                .positionFromBounds(build3CMP)
//                .image(BitmapDescriptorFactory.fromResource(R.drawable.building_3c_mp));
//        GroundOverlayOptions build3FOverlay = new GroundOverlayOptions()
//                .positionFromBounds(build3F)
//                .image(BitmapDescriptorFactory.fromResource(R.drawable.building_3f_1f));
//        GroundOverlayOptions build4COverlay = new GroundOverlayOptions()
//                .positionFromBounds(build4C)
//                .image(BitmapDescriptorFactory.fromResource(R.drawable.building_4c))
//                .bearing(140);
//        GroundOverlayOptions build4BOverlay = new GroundOverlayOptions()
//                .positionFromBounds(build4B)
//                .image(BitmapDescriptorFactory.fromResource(R.drawable.building_4b_1f));
//        GroundOverlayOptions build4AOverlay = new GroundOverlayOptions()
//                .positionFromBounds(build4A)
//                .image(BitmapDescriptorFactory.fromResource(R.drawable.building_4a_wd1));
//        GroundOverlayOptions build4AWD2Overlay = new GroundOverlayOptions()
//                .positionFromBounds(build4AWD2)
//                .image(BitmapDescriptorFactory.fromResource(R.drawable.building_4a_wd2))
//                .bearing(42);
//        GroundOverlayOptions build4AFCOverlay = new GroundOverlayOptions()
//                .positionFromBounds(build4AFC)
//                .image(BitmapDescriptorFactory.fromResource(R.drawable.building_4a_fc))
//                .bearing(-46);
//        GroundOverlayOptions build4DOverlay = new GroundOverlayOptions()
//                .positionFromBounds(build4D)
//                .image(BitmapDescriptorFactory.fromResource(R.drawable.building_4d_1f))
//                .bearing(45);
//        GroundOverlayOptions build4EOverlay = new GroundOverlayOptions()
//                .positionFromBounds(build4E)
//                .image(BitmapDescriptorFactory.fromResource(R.drawable.building_4e_distrubution))
//                .bearing(45);
        //add groundOverlay and create reference.
        GroundOverlay buildLibraryOverlayed = mMap.addGroundOverlay(buildLibraryOverlay);
        GroundOverlay build3aF1 = mMap.addGroundOverlay(build3aOverlay);
        GroundOverlay build3aF2 = mMap.addGroundOverlay(build3aF2Overlay);
//        GroundOverlay  build3bF1 = mMap.addGroundOverlay(building3BOverlay);
//        GroundOverlay build3bConnect = mMap.addGroundOverlay(build3BConnected);
//        GroundOverlay build3COverlayOption = mMap.addGroundOverlay(build3COverlay);
//        GroundOverlay build3CMPOverlayOption =  mMap.addGroundOverlay(build3CMPOverlay);
//        GroundOverlay build3FOverlayOption = mMap.addGroundOverlay(build3FOverlay);
//        GroundOverlay build4COverlayOption = mMap.addGroundOverlay(build4COverlay);
//        GroundOverlay build4BOverlayOption = mMap.addGroundOverlay(build4BOverlay);
//        GroundOverlay build4AOverlayOption = mMap.addGroundOverlay(build4AOverlay);
//        GroundOverlay build4AWD2OverlayOption = mMap.addGroundOverlay(build4AWD2Overlay);
//        GroundOverlay build4AFCOverlayOption = mMap.addGroundOverlay(build4AFCOverlay);
//        GroundOverlay build4DOverlayOption = mMap.addGroundOverlay(build4DOverlay);
//        GroundOverlay build4EOverlayOption = mMap.addGroundOverlay(build4EOverlay);
        build3aF1.setDimensions(34,28);
        build3aF2.setDimensions(34,28);
        buildLibraryOverlayed.setDimensions(37,28);
//        build3bF1.setDimensions(84,62);
//        build3bConnect.setDimensions(64,30);
//        build3COverlayOption.setDimensions(40,42);
//        build3CMPOverlayOption.setDimensions(20,25);
//        build3FOverlayOption.setDimensions(100,80);
/*        build4COverlayOption.setDimensions(14,10);
        build4BOverlayOption.setDimensions(68,48);
        build4AOverlayOption.setDimensions(90,50);
        build4AWD2OverlayOption.setDimensions(14,10);
        build4AFCOverlayOption.setDimensions(70,50);
        build4DOverlayOption.setDimensions(100,60);
        build4EOverlayOption.setDimensions(100,60);*/
        //make it so overlay doesnt appear originally
//        build3bConnect.setVisible(false);
//        build3bF1.setVisible(false);
        buildLibraryOverlayed.setVisible(false);
        build3aF1.setVisible(false);
//        build3COverlayOption.setVisible(false);
//        build3CMPOverlayOption.setVisible(false);
//        build3FOverlayOption.setVisible(false);
//        build4COverlayOption.setVisible(false);
//        build4BOverlayOption.setVisible(false);
//        build4AOverlayOption.setVisible(false);
//        build4DOverlayOption.setVisible(true);
//        build4EOverlayOption.setVisible(true);
        build3aF2.setVisible(false);
        //add the overlay to overlay array.
//        groundOverlaysf1.add(build3bConnect);
        groundOverlaysf1.add(buildLibraryOverlayed);
        groundOverlaysf1.add(build3aF1);
//        groundOverlaysf1.add(build3bF1);
//        groundOverlaysf1.add(build3COverlayOption);
//        groundOverlaysf1.add(build3CMPOverlayOption);
//        groundOverlaysf1.add(build3FOverlayOption);
//        groundOverlaysf1.add(build4COverlayOption);
//        groundOverlaysf1.add(build4BOverlayOption);
//        groundOverlaysf1.add(build4AOverlayOption);
//        groundOverlaysf1.add(build4AWD2OverlayOption);
//        groundOverlaysf1.add(build4AFCOverlayOption);
//        groundOverlaysf1.add(build4DOverlayOption);
//        groundOverlaysf1.add(build4EOverlayOption);
        groundOverlaysf2.add(build3aF2);
        //Markers for classrooms
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.pixilart_drawing);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false);

        //Markers for SquidWard
        BitmapDrawable bitmapdrawSCC=(BitmapDrawable)getResources().getDrawable(R.drawable.squidward_community_college);
        Bitmap bSCC=bitmapdrawSCC.getBitmap();
        Bitmap smallMarkerSCC = Bitmap.createScaledBitmap(bSCC, 340, 400, false);

        //Markers for Bathrooms
        BitmapDrawable bitmapdraw2=(BitmapDrawable)getResources().getDrawable(R.drawable.pixil_frame_0);
        Bitmap b2=bitmapdraw2.getBitmap();
        Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, 100, 100, false);

        //Markers for WaterZones
        BitmapDrawable bitmapdraw3=(BitmapDrawable)getResources().getDrawable(R.drawable.pixilart_drawing__1_);
        Bitmap b3=bitmapdraw3.getBitmap();
        Bitmap smallMarker3 = Bitmap.createScaledBitmap(b3, 100, 100, false);

        //Markers for AdminRooms (Unused as of now)
        BitmapDrawable bitmapdraw4=(BitmapDrawable)getResources().getDrawable(R.drawable.admin_rooms_marker_expanded);
        Bitmap b4=bitmapdraw4.getBitmap();
        Bitmap smallMarker4 = Bitmap.createScaledBitmap(b4, 140, 200, false);

        SquidCC.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarkerSCC));
        //Set Markers image for classrooms
        for (Marker ClassRoom: ClassRoomMarkers)
        {
            ClassRoom.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        }

        //Set Markers image for bathrooms
        for(Marker Bathrooms: BathroomMarkers)
        {
            Bathrooms.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker2));
        }

        //Set Markers image for Waterzones
        for(Marker WaterStation: WaterZones)
        {
            WaterStation.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker3));
        }
        //set overlays and markers visibile after certain zoom level is reached.
        //For camera moving
        mMap.setOnCameraMoveListener(()->
        {
            if(wasRemoveHit)
            {
                for (int i = 0; i <createdMarkers.size() ; i++)
                {
                    if(createdMarkers.get(i).getTitle().equals(createdMarker.getTitle()))
                    {
                        createdMarkers.get(i).remove();
                        createdMarkers.remove(i);
                    }
                }
            }
            for (GroundOverlay Overlay : groundOverlaysf1) {
                Overlay.setVisible(mMap.getCameraPosition().zoom > 18 && floorPicked == 1);
            }
            for(GroundOverlay Overlay: groundOverlaysf2){
                Overlay.setVisible(mMap.getCameraPosition().zoom >18 && floorPicked == 2);
            }
            if(floorPicked == 1) {
                for (Marker markers : MarkersList) {
                    if (marker2 != null) {
                        if (markers.getTitle().equals(marker2.getTitle())) {
                            markers.setVisible(mMap.getCameraPosition().zoom > 18);
                        }
                    } else {
                        markers.setVisible(mMap.getCameraPosition().zoom > 18);
                    }
                }
                for(Marker marker: secondFloorMarkersList)
                {
                    marker.setVisible(false);
                }
            }
            else
            {
                for (Marker markers : secondFloorMarkersList) {
                    if (marker2 != null) {
                        if (markers.getTitle().equals(marker2.getTitle())) {
                            markers.setVisible(mMap.getCameraPosition().zoom > 18);
                        }
                    } else {
                        markers.setVisible(mMap.getCameraPosition().zoom > 18);
                    }
                }
                for (Marker marker : MarkersList){
                    marker.setVisible(false);
                }
            }
        });
        //when camera is still (used for searchbar since it doesn't count as camera moving)
        mMap.setOnCameraIdleListener(()->
        {
            if(FollowUser || wasMarkerClicked)
            {
                navloc();
            }
            if(wasRemoveHit)
            {
                for (int i = 0; i <createdMarkers.size() ; i++)
                {
                    if(createdMarkers.get(i).getTitle().equals(createdMarker.getTitle()))
                    {
                        createdMarkers.get(i).remove();
                        createdMarkers.remove(i);
                    }
                }
                for (int i = 0; i < favoritedMarkers.size(); i++) {
                    if(favoritedMarkers.get(i).getTitle().equals(createdMarker.getTitle()))
                    {
                        favoritedMarkers.remove(i);
                    }
                }
            }
            for (GroundOverlay Overlay : groundOverlaysf1) {
                Overlay.setVisible(mMap.getCameraPosition().zoom > 18 && floorPicked == 1);
            }
            for(GroundOverlay Overlay: groundOverlaysf2){
                Overlay.setVisible(mMap.getCameraPosition().zoom >18 && floorPicked == 2);
            }
            if(floorPicked == 1) {
                for (Marker markers : MarkersList) {
                    if (marker2 != null) {
                        if (markers.getTitle().equals(marker2.getTitle())) {
                            markers.setVisible(mMap.getCameraPosition().zoom > 18);
                        }
                    } else {
                        markers.setVisible(mMap.getCameraPosition().zoom > 18);
                    }
                }
                for(Marker marker: secondFloorMarkersList)
                {
                    marker.setVisible(false);
                }
            }
            else
            {
                for (Marker markers : secondFloorMarkersList) {
                    if (marker2 != null) {
                        if (markers.getTitle().equals(marker2.getTitle())) {
                            markers.setVisible(mMap.getCameraPosition().zoom > 18);
                        }
                    } else {
                        markers.setVisible(mMap.getCameraPosition().zoom > 18);
                    }
                }
                for (Marker marker : MarkersList){
                    marker.setVisible(false);
                }
            }
        });
        //Slide up code setup
        slideupview = findViewById(R.id.slideup);
        slideupview.setVisibility(View.GONE);
        slideup = false;

        //Set Button for save spot
        Set = findViewById(R.id.OkMarkerTitle);
        Set.setOnClickListener(this);

        //Remove Spot button
        RemovePoint = findViewById(R.id.RemoveSpot);
        RemovePoint.setOnClickListener(this);

        //Navigation button Setup
        NavGo = findViewById(R.id.navgo);
        NavGo.setOnClickListener(this);

        //Button for when Done is pressed while in nav mode
        NavDone = findViewById(R.id.NavDone);
        NavDone.setOnClickListener(this);

        //zoom in
//        ZoomIn = findViewById(R.id.ZoomIn);
//        ZoomIn.setOnClickListener(this);

        //zoom out
//        ZoomOut = findViewById(R.id.ZoomOut);
//        ZoomOut.setOnClickListener(this);
        //SearchBar
        Search = findViewById(R.id.input_Search);

        //upFloor
        upFloor = findViewById(R.id.FloorUp);
        //downFloor
        downFloor = findViewById(R.id.FloorDown);

        //Nav Lock Button
        NacLock = findViewById(R.id.NavLock);
        NacLock.setOnClickListener(this);
        //Marker click function
        mMap.setOnMarkerClickListener(this);
        //Map click function
        mMap.setOnMapClickListener(this);
        //Map Long Click function
        mMap.setOnMapLongClickListener(this);
        //upFloor Click Listener
        upFloor.setOnClickListener(this);
        //downFloor Click Listener
        downFloor.setOnClickListener(this);
        if(floorPicked == 1)
        {
            downFloor.setVisibility(View.GONE);
        }
        else
        {
            upFloor.setVisibility(View.GONE);
            downFloor.setVisibility(View.VISIBLE);
        }
        //prepares searchbar
        SearchReady();
        //Getting Darkmode option from database
        DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference("/Users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/DarkMode/");
        mdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean DarkMode = (boolean) snapshot.getValue();
                    if (DarkMode) {
                        DarkorLight = true;
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(),R.raw.style_json));
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        for (PolylineOptions lines1: customPolyLines)
                        {
                            lines1.pattern(pattern);
                            lines1.width(15);
                            lines1.color(Color.BLUE);
                        }
                    } else {
                        DarkorLight = false;
                        mMap.setMapStyle(null);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
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

        if(favoritedMarkers == null||favoritedMarkers.isEmpty()){
            LoadFavoriteMarkers();
        }
        if(!favoritedMarkers.isEmpty())
        {
            for (int i = 0; i < favoritedMarkers.size(); i++) {
                if(favoritedMarkers.get(i).getTitle().equals(markerTitle2))
                {
                    onMarkerClick(favoritedMarkers.get(i));
                    break;
                }
            }
        }
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraLoad));
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("/Users/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/CustomMarkers/");
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){

                    String markerTitle = dataSnapshot.getKey().toString();
                    double latitude1 = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                    double longitude1 = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());
                    LatLng newLatLng = new LatLng(latitude1,longitude1);
                    MarkerOptions newMarkerOption = new MarkerOptions().position(newLatLng).title(markerTitle);
                    if(!wasRemoveHit) {
                        Marker newMarker = mMap.addMarker(newMarkerOption);
                        newMarker.showInfoWindow();
                        createdMarkers.add(newMarker);
                    }
                }
                doTheClick(createdMarkers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void doTheClick(ArrayList<Marker> ListToClick)
    {
        Marker marker = ListToClick.get(0);
        if(isNOTfUCKED){
            onMarkerClick(FindTheMarker(markerTitle2));
            btnFavoritesAdd.setVisibility(View.GONE);
            bntFavoritesRemove.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraLoad));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt("floorPicked", floorPicked);
    }
    @Override
    protected void onRestoreInstanceState(Bundle inState){
        super.onRestoreInstanceState(inState);
        floorPicked = inState.getInt("floorPicked");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraPosition mMyCam = mMap.getCameraPosition();
        double longitude = mMyCam.target.longitude;
        double latitude = mMyCam.target.latitude;

        SharedPreferences settings = getSharedPreferences("SOME_NAME", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat("longitude",(float) longitude);
        editor.putFloat("latitude",(float) latitude);
        editor.putInt("floorPicked",floorPicked);
        editor.commit();
    }

    public void navloc()
    {
        try {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            if (mLocationPermissionsGranted) {
                @SuppressLint("MissingPermission") Task location = fusedLocationClient.getLastLocation();
                location.addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {
                            getDeviceLocation();
                            if(FollowUser) {
                                moveCamera(new LatLng(Latitude,Longitued),19f);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                            }
                            if (location.getLatitude() != Latitude || location.getLongitude() != Longitued) {
                                if(wasMarkerClicked) {
                                    RemoveAllLines();
                                    getDirectionPoly(marker2);
                                }
                                if(FollowUser) {
                                    Latitude = location.getLatitude();
                                    Longitued = location.getLongitude();
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                                }
                            }
                        } else {
                        }
                    }
                });
            }
        }catch(SecurityException e){

    }
    }

    public boolean isItInMyFavorites(Marker marker)
    {
        for (int i = 0; i < favoritedMarkers.size(); i++) {
            if(favoritedMarkers.get(i).getTitle().equals(marker.getTitle()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        //Switch based on what button was clicked
        switch(view.getId()){
            case R.id.userMaps:
                Intent intent =  new Intent(this,Settings.class);
                startActivity(intent);
                break;
            case R.id.NavLock:
                if(!FollowUser)
                {
                    FollowUser = true;
                    Snackbar snack = Snackbar.make(findViewById(R.id.map), "NavLoc ON", Snackbar.LENGTH_SHORT);
                    snack.show();
                }
                else
                {
                    FollowUser = false;
                    Snackbar snack = Snackbar.make(findViewById(R.id.map), "NavLoc OFF", Snackbar.LENGTH_SHORT);
                    snack.show();
                }
                break;
            case R.id.navgo:
                getDirectionPoly(marker2);
                checkDistPoint = new LatLng(Latitude,Longitued);
                //Setting curlocation and final destination to text boxes
                AutoCompleteTextView curlocation = findViewById(R.id.From);
                AutoCompleteTextView finaldestination = findViewById(R.id.Destination);
                //create strings from textboxes
                String stringcurlocation = curlocation.getText().toString();
                String stringfinaldestination = finaldestination.getText().toString();
                //get the markers
                Marker finalDestinationMarker = FindTheMarker(stringfinaldestination);
                //Setup string for finding path
                String RooomtoRoom = "";
                if(!(CheckMarkerType(finalDestinationMarker)) && !stringcurlocation.isEmpty())
                {
                    //remove all lines
                    RemoveAllLines();
                    //Logic for deciding the order to place the strings in
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

                    //Set wasFound to false as standard, if polyline is found dont display error
                    Boolean wasFound = false;
                    for (int i = 0; i < LinesTitles.size(); i++) {
                        //Since the Linestitles and linesShowing are created together, the indexes are the same
                        if (RooomtoRoom.equals(LinesTitles.get(i))) {
                            linesShowing.add(mMap.addPolyline(customPolyLines.get(i)));
                            wasFound = true;
                            break;
                        }
                    }
                    if (!wasFound) {
                        snack = Snackbar.make(findViewById(R.id.map), "Invalid Route", Snackbar.LENGTH_SHORT);
                        snack.show();
                        break;
                    }
                }
                //"Select" markers to be used if needed
                //Removes slideup
                slideupview.setVisibility(View.GONE);
                slideup = false;
                //Allows NavDone button to appear
                NavDone.setVisibility(View.VISIBLE);
                //Brings back searchbar (may be depricated, will have to test)
                Search.setVisibility(View.VISIBLE);
                //Removes keyboard when Go is hit
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(slideupview.getWindowToken(), 0);
                break;

            case R.id.NavDone:
                wasMarkerClicked = false;
                //Remove all lines
                RemoveAllLines();
                checkIfMarkerNeedVisible();
                onMapClick(new LatLng(28.595085, -81.308305));
                markersClicked.remove(0);
                //Brings Searchbar back (again, may be depricated)
                Search.setVisibility(View.VISIBLE);
                //Removes navdone button
                NavDone.setVisibility(View.GONE);
                break;
            case R.id.btnRemoveFavorites:
                Favorites.removeFromFavorite(MapsActivity.this, marker2);
                favoritedMarkers.remove(marker2);
                snack = Snackbar.make(findViewById(R.id.map), "Removed From Favorites", Snackbar.LENGTH_SHORT);
                snack.show();
                bntFavoritesRemove.setVisibility(View.GONE);
                btnFavoritesAdd.setVisibility(View.VISIBLE);
                break;
            case R.id.RemoveSpot:
                wasRemoveHit = true;
                CustomMarker.removeFromCustomMarkers(MapsActivity.this, createdMarker);
                for (int i = 0; i <createdMarkers.size() ; i++)
                {
                    if(createdMarkers.get(i).getTitle().equals(createdMarker.getTitle()))
                    {
                        createdMarkers.get(i).remove();
                        createdMarkers.get(i).setVisible(false);
                        createdMarkers.remove(i);
                        createdMarker.remove();
                        createdMarker = null;
                    }
                }
                for (int i = 0; i < favoritedMarkers.size(); i++)
                {
                    if(favoritedMarkers.get(i).getTitle().equals(createdMarker.getTitle()))
                    {
                        Favorites.removeFromFavorite(MapsActivity.this,createdMarker);
                        favoritedMarkers.remove(i);
                    }
                }
                RemoveAllLines();
                RemovePoint.setVisibility(View.GONE);
                slideupview.setVisibility(View.GONE);
                break;
            case R.id.OkMarkerTitle:
                getDeviceLocation();

                boolean wasItCreatedAlready = false;
                boolean nameExistAlready = false;
                TextView markerName = findViewById(R.id.MarkerName);
                String name = markerName.getText().toString();
                markerName.setText(null);
                if(createdMarkers != null)
                {
                    for (Marker marker1: createdMarkers)
                    {
                        if(marker1.getTitle().equals(name))
                        {
                            wasItCreatedAlready = true;
                        }
                    }
                    for(Marker marker: MarkersList)
                    {
                        if(marker.getTitle().equals(name))
                        {
                            nameExistAlready = true;
                        }
                    }
                }
                Marker newMarker = null;
                if(wasItCreatedAlready || nameExistAlready)
                {
                    snack = Snackbar.make(findViewById(R.id.map), "Marker Already Exists", Snackbar.LENGTH_SHORT);
                    snack.show();
                    saveSpotLayout.setVisibility(View.GONE);
                }
                else
                {
                    if(!wasRemoveHit)
                    {
                        if(!name.isEmpty())
                        {
                            newMarker = mMap.addMarker(new MarkerOptions().position(LongClickPoint).title(name));
                            newMarker.showInfoWindow();
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(LongClickPoint));
                        }
                        else
                        {
                            newMarker = mMap.addMarker(new MarkerOptions().position(LongClickPoint).title(name));
                            newMarker.showInfoWindow();
                        }
                    }
                    CustomMarker.addToCustomMarkers(MapsActivity.this, newMarker);
                    saveSpotLayout.setVisibility(View.GONE);
                }
                InputMethodManager manager1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager1.hideSoftInputFromWindow(slideupview.getWindowToken(), 0);
                break;
            case R.id.btnAddFavorites:
                Favorites.addToFavorite(MapsActivity.this,marker2);
                snack = Snackbar.make(findViewById(R.id.map), "Added To Favorites", Snackbar.LENGTH_SHORT);
                snack.show();
                bntFavoritesRemove.setVisibility(View.VISIBLE);
                btnFavoritesAdd.setVisibility(View.GONE);
                break;
            case R.id.ZoomIn:
                mMap.moveCamera(CameraUpdateFactory.zoomIn());
                checkIfMarkerNeedVisible();
                break;
            case R.id.ZoomOut:
                mMap.moveCamera(CameraUpdateFactory.zoomOut());
                checkIfMarkerNeedVisible();
                break;
            case R.id.FloorUp:
                floorPicked = 2;
                showMapsFloor(floorPicked);
                upFloor.setVisibility(View.GONE);
                downFloor.setVisibility(View.VISIBLE);
                for (Marker markers : secondFloorMarkersList) {
                    if (marker2 != null) {
                        if (markers.getTitle().equals(marker2.getTitle())) {
                            markers.setVisible(mMap.getCameraPosition().zoom > 18);
                        }
                    } else {
                        markers.setVisible(mMap.getCameraPosition().zoom > 18);
                    }
                }
                for (Marker marker : MarkersList){
                    marker.setVisible(false);
                }
                break;
            case R.id.FloorDown:
                floorPicked = 1;
                showMapsFloor(floorPicked);
                downFloor.setVisibility(View.GONE);
                upFloor.setVisibility(View.VISIBLE);
                for (Marker markers : MarkersList) {
                    if (marker2 != null) {
                        if (markers.getTitle().equals(marker2.getTitle())) {
                            markers.setVisible(mMap.getCameraPosition().zoom > 18);
                        }
                    } else {
                        markers.setVisible(mMap.getCameraPosition().zoom > 18);
                    }
                }
                for(Marker marker: secondFloorMarkersList)
                {
                    marker.setVisible(false);
                }
                break;
        }
    }
    //Chooses which Maps needs to be shown
    protected void showMapsFloor(int Floor) {
        if(Floor == 2)
        {
            for (GroundOverlay Overlay : groundOverlaysf1)
            {
                Overlay.setVisible(false);
            }
            for(GroundOverlay Overlay : groundOverlaysf2)
            {
                if(mMap.getCameraPosition().zoom >18) {
                    Overlay.setVisible(true);
                }
            }
        }
        else
        {
            for (GroundOverlay Overlay : groundOverlaysf2)
            {
                Overlay.setVisible(false);
            }
            for(GroundOverlay Overlay : groundOverlaysf1)
            {
                if(mMap.getCameraPosition().zoom>18) {
                    Overlay.setVisible(true);
                }
            }
        }
    }

    //Getting permission from user for location
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
        }else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onMapLongClick(LatLng point) {
        wasRemoveHit = false;
        saveSpotLayout.setVisibility(View.VISIBLE);
        LongClickPoint = point;
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        //get location for drawing line between user and marker
        getDeviceLocation();

        if(!wasRemoveHit){
            wasRemoveHit = true;
        }
        else
        {
            marker.remove();
        }
        if(isItInMyFavorites(marker))
        {

            btnFavoritesAdd.setVisibility(View.GONE);
            bntFavoritesRemove.setVisibility(View.VISIBLE);
        }
        else
        {

            bntFavoritesRemove.setVisibility(View.GONE);
            btnFavoritesAdd.setVisibility(View.VISIBLE);
        }
        wasRemoveHit = false;
        wasMarkerClicked = true;
        NavDone.setVisibility(View.VISIBLE);
        //check for marker in original Marker list
        if(CheckMarkerType(marker))
        {
            RemovePoint.setVisibility(View.VISIBLE);
            createdMarker = marker;
        }
        else
        {
            RemovePoint.setVisibility(View.GONE);
        }
        //Change camera, zoom if needed
        if (mMap.getCameraPosition().zoom < 18) {
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
        if(markersClicked.size()!= 0) {
            if (!markersClicked.get(0).equals(marker)) {
                clickCount++;
                RemoveAllLines();
                markersClicked.add(marker);
            }
            //triggers if user clicks on same marker twice
            else {
                if (clickCount != 1) {
                    snack = Snackbar.make(findViewById(R.id.map), "Clicked On The Same Marker", Snackbar.LENGTH_SHORT);
                    snack.show();
                }
            }
        }
        else
        {
            RemoveAllLines();
            markersClicked.add(marker);
        }
        //Remove marker from markers clicked when more than 1 marker has been clicked
        if (markersClicked.size() > 1) {
            markersClicked.remove(0);
        }
        /*marker.showInfoWindow();*/

        //Slide up code
        TextView text = slideupview.findViewById(R.id.roomnumber);
        text.setText(marker.getTitle());
        if (!slideup){
            //Makes slideup visible
            slideupview.setVisibility(View.VISIBLE);
          //Animation (Does not function properly, cant remove slideup afterwards)
            /*  TranslateAnimation animate = new TranslateAnimation(0, 0, slideupview.getHeight(), 0);
            animate.setDuration(375);
            animate.setFillAfter(true);
            slideupview.startAnimation(animate);*/
            slideup = true;
        }

        //Creates list of all marker titles
        ArrayList<String> listfornav = new ArrayList<String>();
        for (Marker m : MarkersList){
            if (m.getTitle() != null){
                listfornav.add(m.getTitle());
            }
        }
        AutoCompleteTextView from = findViewById(R.id.From);
        AutoCompleteTextView To = findViewById(R.id.Destination);
        if(!CheckMarkerType(marker)) {
            from.setEnabled(true);
            from.setFocusableInTouchMode(true);
            from.setBackgroundColor(Color.GRAY);
            To.setBackgroundColor(Color.GRAY);
            To.setEnabled(true);
            To.setFocusableInTouchMode(true);
            //Creating Suggestions for text boxes in nav
            ArrayAdapter<String> adapterlist = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, listfornav);
            from = findViewById(R.id.From);
            from.setText("");//Blank "from"
            from.setAdapter(adapterlist);//set dropdown
            AutoCompleteTextView destination = findViewById(R.id.Destination);
            destination.setAdapter(adapterlist);//set dropdown
            //Autofill Destination
            destination.setText(markersClicked.get(0).getTitle());
        }
        else
        {
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
        for (Marker m : MarkersList)
        {
            if(!m.getTitle().equals(marker.getTitle())) {
                m.setVisible(false);
            }
        }
        marker2 = marker;
        //No clue why google decided marker click HAS to return a boolean, so here ya go google
        return false;
    }

    public void checkIfMarkerNeedVisible()
    {
        if(!wasMarkerClicked) {
            if (mMap.getCameraPosition().zoom > 18) {
                for (Marker m : MarkersList) {
                    m.setVisible(true);
                }
            } else {
                for (Marker m2 : MarkersList) {
                    m2.setVisible(false);
                }
            }
            for (Marker mC : createdMarkers)
            {
                mC.setVisible(true);
            }
        }
        else
        {
            for(Marker m3: MarkersList)
            {
                if(!m3.getTitle().equals(marker2.getTitle()))
                {
                    m3.setVisible(false);
                }
            }
            for(Marker m3C: createdMarkers)
            {
                if(!m3C.getTitle().equals(marker2.getTitle()))
                {
                    m3C.setVisible(false);
                }
            }
        }
    }

    //Handles what happens when user clicks on the map (not the same as drag)
    @Override
    public void onMapClick(LatLng point){
        //Hide Keyboard when map is clicked
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(slideupview.getWindowToken(), 0);
        //slide down slideup when map is clicked
        if (slideup){
            slideupview.setVisibility(View.GONE);
            //Animation stuff that bugs out
            /*TranslateAnimation animate = new TranslateAnimation(0, 0, 0, slideupview.getHeight());
            animate.setDuration(375);
            animate.setFillAfter(true);
            slideupview.startAnimation(animate);*/
            slideup = false;
        }
        if(RemovePoint.getVisibility() == View.VISIBLE)
        {
            RemovePoint.setVisibility(View.GONE);
        }
        if(saveSpotLayout.getVisibility() == View.VISIBLE)
        {
            saveSpotLayout.setVisibility(View.GONE);
        }
        //Make all markers visible
        checkIfMarkerNeedVisible();
    }

    public void RemoveAllLines()
    {
        while(linesShowing.size() > 0){
            linesShowing.get(0).remove();
            linesShowing.remove(0);
        }
    }

    public Marker FindTheMarker(String title)
    {
        Marker foundMarker = null;
        for (Marker m: createdMarkers)
        {
            if(m.getTitle().equals(title))
            {
                foundMarker = m;
            }
         }
        for(Marker m1: MarkersList)
        {
            if(m1.getTitle().equals(title))
            {
                foundMarker = m1;
            }
        }
        for(Marker m2: favoritedMarkers)
        {
            if(m2.getTitle().equals(title))
            {
                foundMarker = m2;
            }
        }
        for(Marker m3: secondFloorMarkersList)
        {
            if(m3.getTitle().equals(title))
            {
                foundMarker = m3;
            }
        }
        return foundMarker;
    }
    public boolean CheckMarkerType(Marker marker)
    {
        boolean isItCreatedMarker = false;
        for (Marker m: createdMarkers)
        {
            if(m.getTitle().equals(marker.getTitle()))
            {
                isItCreatedMarker =true;
            }
        }
        return isItCreatedMarker;
    }

    //Credit to Ruben for solving everything below here
    //get directions to marker
    public void getDirectionPoly(Marker marker)
    {
        getDeviceLocation();
        String url1 = "";
        if(!CheckMarkerType(marker))
        {
            url1 = getUrl(new LatLng(Latitude, Longitued), MarkersList.get(0).getPosition());
        }
        else
        {
            url1 = getUrl(new LatLng(Latitude, Longitued),marker.getPosition());
        }
        String url = url1;

        TaskRequestDirections taskRequestDirections = new TaskRequestDirections(marker);
        taskRequestDirections.execute(url);
    }
    //Get URL for Getting Direction
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
            if(polylineOptions!= null)
            {
                if(!CheckMarkerType(marker))
                {
                    List<LatLng> outToInPoly = customPolyLines.get(0).getPoints();
                    polylineOptions.add(outToInPoly.get(0));
                }
                String outToPolys = "outsideTo" + marker.getTitle();
                for (int i = 0; i < LinesTitles.size() ; i++)
                {
                    if(LinesTitles.get(i).equals(outToPolys))
                    {
                        linesShowing.add(mMap.addPolyline(customPolyLines.get(i)));
                    }
                }
                linesShowing.add(mMap.addPolyline(polylineOptions));
            }else
            {
                snack = Snackbar.make(findViewById(R.id.map), "Directions Not Found", Snackbar.LENGTH_SHORT);
                snack.show();
            }
        }
    }
}