package com.example.FSMap;
//Woohoo, imports

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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

import com.example.FSMap.databinding.ActivityMapsBinding;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
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
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {
    private static final long GPS_UPDATE_TIME = 500;
    //Global Variables used throughout the program,
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    protected ActivityMapsBinding binding;
    private CircleImageView userIconMaps;
    private ImageView mGps;
    private Button Set;
    protected LatLng dest;
    private Button RemovePoint;
    private ImageView ZoomIn;
    private ImageView ZoomOut;
    private Button NavGo;
    private Button NavDone;
    private Button NacLock;
    private AutoCompleteTextView Search;
    public Button Filter, CRFilter, OFFilter, BRFilter, WZFilter, ETCFilter;
    public boolean FilterShow, CRShow, OFShow, BRShow, WZShow, ETCShow, Filtering = false;
    private static final String FINE_lOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationPermissionsGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    ArrayList<GroundOverlayOptions> groundOverlaysf1 = new ArrayList<GroundOverlayOptions>();
    ArrayList<GroundOverlayOptions> groundOverlaysf2 = new ArrayList<GroundOverlayOptions>();
    GroundOverlay B3U2 = null;
    ArrayList<Marker> MarkersList = new ArrayList<Marker>();
    ArrayList<Polyline> linesShowing = new ArrayList<Polyline>();
    ArrayList<PolylineOptions> customPolyLines = new ArrayList<>();
    ArrayList<Marker> createdMarkers;
    ArrayList<Marker> favoritedMarkers;
    ArrayList<Marker> BathroomMarkers = new ArrayList<>();
    ArrayList<Marker> ClassRoomMarkers = new ArrayList<>();
    ArrayList<Marker> ETCRooms = new ArrayList<>();
    ArrayList<Marker> OFRooms = new ArrayList<>();
    ArrayList<Marker> secondFloorMarkersList = new ArrayList<Marker>();

    ArrayList<Marker> BuildingOne = new ArrayList<>();
    ArrayList<Marker> BuildingOneF2 = new ArrayList<>();
    ArrayList<Marker> BuildingTwo = new ArrayList<>();
    ArrayList<Marker> BuildingTwoF2 = new ArrayList<>();
    ArrayList<Marker> ThreeAMarkers = new ArrayList<>();
    ArrayList<Marker> ThreeAMarkersF2 = new ArrayList<>();
    ArrayList<Marker> ThreeBMarkers = new ArrayList<>();
    ArrayList<Marker> ThreeBCMarkers = new ArrayList<>();
    ArrayList<Marker> ThreeCMarkers = new ArrayList<>();
    ArrayList<Marker> ThreeDMarkers = new ArrayList<>();
    ArrayList<Marker> ThreeEMarkers = new ArrayList<>();
    ArrayList<Marker> ThreeFMarkers = new ArrayList<>();
    ArrayList<Marker> FourAMarkers = new ArrayList<>();
    ArrayList<Marker> FourBMarkers = new ArrayList<>();
    ArrayList<Marker> FourCMarkers = new ArrayList<>();
    ArrayList<Marker> FourDMarkers = new ArrayList<>();
    ArrayList<Marker> FourEMarkers = new ArrayList<>();
    ArrayList<Marker> FourFMarkers = new ArrayList<>();
    ArrayList<Marker> FourGMarkers = new ArrayList<>();
    ArrayList<Marker> AboveThreeBuildings = new ArrayList<>();
    ArrayList<Marker> WaterZones = new ArrayList<>();
    GroundOverlay ThreeA;
    GroundOverlay ThreeB;
    GroundOverlay ThreeBC;
    GroundOverlay ThreeC;
    GroundOverlay ThreeD;
    GroundOverlay ThreeE;
    GroundOverlay ThreeF;
    GroundOverlay FourA;
    GroundOverlay FourAWD2;
    GroundOverlay FourAFC;
    GroundOverlay FourB;
    GroundOverlay FourC;
    GroundOverlay FourD;
    GroundOverlay FourE;
    GroundOverlay FourF;
    ArrayList<GroundOverlay> B3U = new ArrayList<>();
    ArrayList<GroundOverlay> B3D = new ArrayList<>();
    ArrayList<GroundOverlay> B4U = new ArrayList<>();
    ArrayList<GroundOverlay> B4D = new ArrayList<>();
    ArrayList<GroundOverlay> B2 = new ArrayList<>();
    ArrayList<GroundOverlay> B1 = new ArrayList<>();
    ArrayList<GroundOverlay> DaBigBois = new ArrayList<>();
    ArrayList<GroundOverlay> AllFloorOneOverlays = new ArrayList<>();
    ArrayList<GroundOverlay> AllSecondFloorOverlays = new ArrayList<>();
    int clickCount = 0;
    ArrayList<String> LinesTitles = new ArrayList<String>();
    List<PatternItem> pattern = Arrays.asList(
            new Dash(30), new Gap(20), new Dot(), new Gap(20));

    double Latitude, Longitued;
    float zoom;
    LinearLayout slideupview;
    public boolean slidepup;
    BottomSheetBehavior bottomSheetBehavior;
    double mLastAltitude;
    ArrayList<String> nameslist = new ArrayList<String>() {
    };
    boolean DarkorLight;
    RelativeLayout saveSpotLayout;
    //Favorites
    ImageButton bntFavoritesRemove;
    ImageButton btnFavoritesAdd;

    Button upFloor;
    Button downFloor;

    boolean isInMyFavorites = false;
    private FirebaseAuth firebaseAuth;
    Marker createdMarker;
    boolean wasRemoveHit, CMReady, FMReady, RemoveMarkerTrue= false;
    private boolean FollowUser = false;
    boolean wasMarkerClicked = false;
    LatLng LongClickPoint;
    LatLng checkDistPoint;
    //Variables from profile Picture
    Snackbar snack;
    FirebaseAuth fAuth;
    StorageReference storageReference;
    LocationManager mLocationManager;
    String markerTitle2;
    boolean isNOTfUCKED, isRouting = false;
    int floorPicked = 1;
    CameraPosition cameraLoad;
    int altitudesCollectedNumber;
    List<LatLng> ltln = new ArrayList<>();
    private Double lat_decimal = 0.0, lng_decimal = 0.0;
    boolean isTraveling = false;
    Marker usermarker;
    String MarkerToRemoveTitle;
    Circle userLocationAccuracyCircle;
    Location currentLocation;
    String prevResult = "";
    String firstLoadResult = "";
    ArrayList<String> resultsList = new ArrayList<>();
    private MarkerFragment markerFragment;
    boolean isAndroidReady = false;
    boolean csvmarkerready, cmmarkerready = false;

    RelativeLayout loadingScreenMaps ;
    ArrayList<String> SearchList;
    boolean isReady;
    View importPanel;
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            setUserLocationMarker(location);
            ComparePoints(location);

        }
    };

    public String DoTheChecks() {
        double checklong = mMap.getCameraPosition().target.longitude;
        double checklat = mMap.getCameraPosition().target.latitude;
        //check's if maps current position is to the left of the marker
        if (checklong < -81.30322828888893) {
            if (checklat < 28.59314522571862)//they are below this marker show buildings 4
            {
                if (checklat > 28.591615290903963)//load tophalf of building4
                {
                    return "b4u";
                } else//load bottom half of building 4
                {
                    return "b4d";
                }
            } else//they are above this marker show buildings 3
            {
                if (checklat < 28.594668367488097)//load bottom half of building 3
                {
                    return "b3d";
                } else//load top half of building 3
                {
                    return "b3u";
                }
            }
        } else//we're on the right side of campus
        {
            //check if building one or two need showing
            //if camerposition is to the left show building two
            if (checklong < -81.3018935546279) {
                return "b2";
            } else//if to the right show building 1
            {
                return "b1";
            }
        }
    }

    protected String secondCheckForFinerArea(String result) {
        double checklong = mMap.getCameraPosition().target.longitude;
        double checklat = mMap.getCameraPosition().target.latitude;
        switch (result) {
            case "b3u"://check if result was building 3 top half.
            {
                if (checklat < 28.594907405128183) {
                    return "3BC";
                } else if (checklat < 28.59533160728655) {
                    return "3B";
                } else {
                    return "3A";
                }
            }
            case "b3d"://check if result was building 3 bottom half
            {
                if (checklong < -81.30451440811157) {
                    return "3F";
                } else {
                    if (checklat < 28.593998352683425) {
                        return "3E";
                    } else if (checklat < 28.594210014236552) {
                        return "3D";
                    } else {
                        return "3C";
                    }
                }
            }
            case "b4u": {
                if (checklat < 28.5920183103359) {
                    return "4B";
                } else if (checklat < 28.59227207342217) {
                    return "4AWD2";
                } else if (checklat < 28.59258824606235) {
                    return "4AFC";
                } else {
                    return "4A";
                }
            }
            case "b4d": {
                if (checklat < 28.590857233366894) {
                    return "4E";
                } else if (checklat < 28.591231699753845) {
                    return "4D";
                } else {
                    return "4C";
                }
            }
            case "b2":
                return "b2";
            case "b1":
                return "b1";
        }
        return "nun";
    }



    protected void HideAllOverlays() {
        if (B3U.size() > 0 ) {
            for (int i = 0; i < B3U.size(); i++) {
                B3U.get(i).setVisible(false);
            }
        }
        if (B3D.size() > 0 ) {
            for (int i = 0; i < B3D.size(); i++) {
                B3D.get(i).setVisible(false);
            }
        }
        if (B4U.size() > 0) {
            for (int i = 0; i < B4U.size(); i++) {
                B4U.get(i).setVisible(false);
            }
        }
        //Not Hiding B4D anymore to try and fix lag
        if (B4D.size() > 0) {
            for (int i = 0; i < B4D.size(); i++) {
                B4D.get(i).setVisible(false);
            }
        }
        if (B1.size() > 0) {
            for (int i = 0; i < B1.size(); i++) {
                B1.get(i).setVisible(false);
            }
        }
        if (B2.size() > 0) {
            for (int i = 0; i < B2.size(); i++) {
                B2.get(i).setVisible(false);
            }
        }
    }

    public void CheckResults(String result,String finerResults) {
        if (prevResult != result) {
            HideAllOverlays();
            prevResult = result;
        }
        switch (result) {
            case "b3u"://check if result was building 3 top half.
                if (finerResults.equals("3A")) {
                    if(ThreeA != null&& floorPicked == 1){
                        ThreeA.setVisible(true);
                    }else{
                        if(B3U2 != null) {
                            B3U2.setVisible(true);
                        }
                    }
                } else if (finerResults.equals("3B")) {
                    if(ThreeB != null&& floorPicked == 1){
                        ThreeB.setVisible(true);
                    }
                }
                else if (finerResults.equals("3BC")) {
                    if(ThreeBC != null&& floorPicked == 1){
                        ThreeBC.setVisible(true);
                    }
                }

                break;
            case "b3d"://check if result was building 3 bottom half
                if (finerResults.equals("3C")) {
                    if(ThreeC != null&& floorPicked == 1){
                        ThreeC.setVisible(true);
                    }
                }
                else if (finerResults.equals("3D")) {
                    if(ThreeD != null&& floorPicked == 1){
                        ThreeD.setVisible(true);
                    }
                }
                else if (finerResults.equals("3E")) {
                    if(ThreeE != null&& floorPicked == 1){
                        ThreeE.setVisible(true);
                    }
                } else if (finerResults.equals("3F")) {
                    if(ThreeF != null&& floorPicked == 1){
                        ThreeF.setVisible(true);
                    }
                }
                break;

            case "b4u":
                if (finerResults.equals("4A")) {
                    if(FourA != null&& floorPicked == 1){
                        FourA.setVisible(true);
                    }
                } else if (finerResults.equals("4AWD2")) {
                    if(FourAWD2 != null&& floorPicked == 1){
                        FourAWD2.setVisible(true);
                    }
                } if (finerResults.equals("4AFC")) {
                    if(FourAFC != null&& floorPicked == 1){
                        FourAFC.setVisible(true);
                    }
                } else if (finerResults.equals("4B")) {
                    if(FourB != null&& floorPicked == 1){
                        FourB.setVisible(true);
                    }
                }
                break;
            case "b4d":
                if (finerResults.equals("4C")) {
                    if(FourC != null && floorPicked == 1){
                        FourC.setVisible(true);
                    }
                }
                else if (finerResults.equals("4D")) {
                    if(FourD != null && floorPicked == 1){
                        FourD.setVisible(true);
                    }
                } else if (finerResults.equals("4E")) {
                    if(FourE != null && floorPicked == 1) {
                        FourE.setVisible(true);
                    }
                }
                break;
            case "b2":
                if (floorPicked == 1) {
                    B2.get(1).setVisible(true);
                } else {
                    B2.get(0).setVisible(true);
                }

                break;
            case "b1":
                if (floorPicked == 1) {
                    B1.get(1).setVisible(true);
                } else {
                    B1.get(0).setVisible(true);
                }
                break;


        }
    }

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
        double longitude = -81.30322828888893;
        double latitude = 28.594638340553995;
        float zoom = 18f;
        if (mMap != null) {
            CameraPosition mMyCam = mMap.getCameraPosition();
            longitude = mMyCam.target.longitude;
            latitude = mMyCam.target.latitude;
            zoom = mMyCam.zoom;
        }
        SharedPreferences settings = getSharedPreferences("SOME_NAME", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat("longitude", (float) longitude);
        editor.putFloat("latitude", (float) latitude);
        editor.putInt("floorPicked", floorPicked);
        editor.putFloat("zoom", zoom);
        editor.commit();


    }

    private void dismissSplashScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //change the boolean
                isAndroidReady = true;
            }
        }, 5000);
    }

    //onCreate gets rebuilt each time the map is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                //Extra bundle is null
                isNOTfUCKED = false;
            } else {
                if(extras.getString("marker_ToMap") != null) {
                    markerTitle2 = extras.getString("marker_ToMap");
                    isNOTfUCKED = true;
                }
                if (extras.getString("marker") != null){
                    markerTitle2 = extras.getString("marker");
                    isNOTfUCKED = true;
                }
                if (extras.getString("removeSpot") !=null){
                    MarkerToRemoveTitle = extras.getString("removeSpot");
                    RemoveMarkerTrue = true;
                }
            }
        }



        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        markerFragment = (MarkerFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        markerFragment.getMapAsync(this);

        userIconMaps = findViewById(R.id.userMaps);
        userIconMaps.setOnClickListener(this);

        mGps = (ImageView) findViewById(R.id.gps);

        getLocationPermission();
        //Slide up code
        //Slide up code
        slideupview = findViewById(R.id.design_bottom_sheet);
        slideupview.setVisibility(View.INVISIBLE);
        slidepup = false;

        //save spot code
        saveSpotLayout = findViewById(R.id.saveSpotLayout);
        //Favorites
        bntFavoritesRemove = (ImageButton) findViewById(R.id.btnRemoveFavorites);
        bntFavoritesRemove.setOnClickListener(this);

        btnFavoritesAdd = (ImageButton) findViewById(R.id.btnAddFavorites);
        btnFavoritesAdd.setOnClickListener(this);

        //Filters
        Filter = findViewById(R.id.FilterButton);
        CRFilter = findViewById(R.id.CRFilterButton);
        OFFilter = findViewById(R.id.OFFilterButton);
        BRFilter = findViewById(R.id.BRFilterButton);
        WZFilter = findViewById(R.id.WZFilterButton);
        ETCFilter = findViewById(R.id.ETCFilterButton);

        Filter.setOnClickListener(this);
        CRFilter.setOnClickListener(this);
        OFFilter.setOnClickListener(this);
        BRFilter.setOnClickListener(this);
        WZFilter.setOnClickListener(this);
        ETCFilter.setOnClickListener(this);


        Filter.setBackground(getDrawable(R.drawable.roundfilterbuttongray));
        CRFilter.setBackground(getDrawable(R.drawable.roundfilterbuttongray));
        OFFilter.setBackground(getDrawable(R.drawable.roundfilterbuttongray));
        BRFilter.setBackground(getDrawable(R.drawable.roundfilterbuttongray));
        WZFilter.setBackground(getDrawable(R.drawable.roundfilterbuttongray));
        ETCFilter.setBackground(getDrawable(R.drawable.roundfilterbuttongray));

        CRFilter.setVisibility(View.GONE);
        OFFilter.setVisibility(View.GONE);
        BRFilter.setVisibility(View.GONE);
        WZFilter.setVisibility(View.GONE);
        ETCFilter.setVisibility(View.GONE);



        loadingScreenMaps = findViewById(R.id.LoadingDesignMaps);
        //Loading markers from CSV

        //PROFILE PICTURE
        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("Users/" + fAuth.getCurrentUser().getUid() + "/ProfilePicture.jpg");
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

//        // FAVORITES
//        if (savedInstanceState == null) {
//            Bundle extras = getIntent().getExtras();
//
//            if (extras == null) {
//                //Extra bundle is null
//                isNOTfUCKED = false;
//            } else {
//                markerTitle2 = extras.getString("marker");
//                isNOTfUCKED = true;
//            }
//        }
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

    private void setUserLocationMarker(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (usermarker == null) {
            // Create a new marker
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.locationmarkersmol_orange));
            markerOptions.rotation(location.getBearing());
            markerOptions.anchor((float) 0.5, (float) 0.5);
            usermarker = mMap.addMarker(markerOptions);
            // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        } else {
            usermarker.setPosition(latLng);
            usermarker.setRotation(location.getBearing());
            //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        }

        if (userLocationAccuracyCircle == null) {
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(latLng);
            circleOptions.strokeWidth(4);
            circleOptions.strokeColor(Color.parseColor("#22ffb551"));
            circleOptions.fillColor(Color.parseColor("#22ffb551"));
            circleOptions.radius(location.getAccuracy());
            userLocationAccuracyCircle = mMap.addCircle(circleOptions);
        } else {
            userLocationAccuracyCircle.setCenter(latLng);
            userLocationAccuracyCircle.setRadius(location.getAccuracy());
        }
    }

    //Function to obtain device location and store in Latitude and Longitued
    private void getDeviceLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {
                @SuppressLint("MissingPermission") Task Location = fusedLocationClient.getLastLocation();
                Location.addOnCompleteListener(new OnCompleteListener() {
                    @Override

                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            currentLocation = (Location) task.getResult();
                            if (currentLocation != null) {
                                Latitude = currentLocation.getLatitude();
                                Longitued = currentLocation.getLongitude();
                                setUserLocationMarker(currentLocation);
                            }
                        } else {
                            Toast.makeText(MapsActivity.this, "Unable to get current Location", Toast.LENGTH_SHORT).show();

                        }

                    }
                });
            }
        } catch (SecurityException e) {

        }
    }

    private Location getMarkerProjectionOnSegment(LatLng carPos, List<LatLng> segment, Projection projection) {

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
        Location marker = new Location("source");
        marker.setLatitude(markerProjection.latitude);
        marker.setLongitude(markerProjection.longitude);
        return marker;
    }

    public void ComparePoints(Location location) {
//        int ixLastPoint = 0;
//        List<LatLng> points;
//        if (linesShowing.size() > 0) {
//
//            points = linesShowing.get(0).getPoints();
//            List<LatLng> pathPoints = points;
//            for (int i = 0; i < points.size(); i++) {
//                LatLng point1 = points.get(i);
//                LatLng point2 = points.get(i);
//                List<LatLng> currentSegment = new ArrayList<>();
//                currentSegment.add(point1);
//                currentSegment.add(point2);
//                if (PolyUtil.isLocationOnPath(new LatLng(location.getLatitude(), location.getLongitude()), currentSegment, true, 50)) {
//
//                    ixLastPoint = i;
//                    // LatLng snappedtOSegment = getMarkerProjectionOnSegment(new LatLng(location.getLatitude(), location.getLongitude()), currentSegment, mMap.getProjection());
//                    break;
//                }
//            }
//            pathPoints = points;
//            for (int i = 0; i < ixLastPoint; i++) {
//                pathPoints.remove(0);
//            }
//            linesShowing.get(0).setVisible(true);
//            linesShowing.get(0).setPoints(pathPoints);
//            for (int i = 0; i < linesShowing.size(); i++) {
//                linesShowing.get(0).setVisible(true);
//            }
//
//        }

        List<LatLng> sourcePoints = new ArrayList<>();
        PolylineOptions polyline;
        if (linesShowing.size() > 0) {
            sourcePoints = linesShowing.get(0).getPoints();
            LatLng carPos;

            carPos = new LatLng(location.getLatitude(), location.getLongitude());

            for (int i = 0; i < sourcePoints.size() - 1; i++) {
                LatLng segmentP1 = sourcePoints.get(i);
                LatLng segmentP2 = sourcePoints.get(i + 1);
                List<LatLng> segment = new ArrayList<>(2);
                segment.add(segmentP1);
                segment.add(segmentP2);

                if (PolyUtil.isLocationOnPath(carPos, segment, true, 30)) {
                    // ixLastPoint = i;
                    polyline = new PolylineOptions();
                    polyline.addAll(segment);
                    polyline.width(15);
                    polyline.color(Color.parseColor("#A9A9A9"));
                    mMap.addPolyline(polyline);
                    Location snappedToSegment = getMarkerProjectionOnSegment(carPos, segment, mMap.getProjection());
                    // setUserLocationMarker(snappedToSegment);
                    break;
                }
            }

        }

    }


    private void getDeviceLocationCameraMove() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {
                @SuppressLint("MissingPermission") Task Location = fusedLocationClient.getLastLocation();
                Location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                            Latitude = currentLocation.getLatitude();
                            Longitued = currentLocation.getLongitude();
                            //setUserLocationMarker(currentLocation);
                        } else {
                            Toast.makeText(MapsActivity.this, "Unable to get current Location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } catch (SecurityException e) {

        }

    }

    public void showMarkerInArea(String Area) {
        switch (Area) {
            case ("b1"):
                if (floorPicked == 1) {
                    for (int i = 0; i < BuildingOne.size(); i++) {
                        BuildingOne.get(i).setVisible(true);
                    }
                } else {
                    for (Marker m : BuildingOneF2) {
                        m.setVisible(true);
                    }
                }
                break;
            case ("b2"):
                if (floorPicked == 1) {
                    for (Marker m2 : BuildingTwoF2) {
                        m2.setVisible(false);
                    }
                    for (Marker m : BuildingTwo) {
                        m.setVisible(true);
                    }
                } else {
                    for (Marker m2 : BuildingTwo) {
                        m2.setVisible(false);
                    }
                    for (Marker m : BuildingTwoF2) {
                        m.setVisible(true);
                    }
                }
                break;
            case ("3A"):
                if (floorPicked == 1) {
                    for (Marker m : ThreeAMarkersF2) {
                        m.setVisible(false);
                    }
                    for (int i = 0; i < ThreeAMarkers.size(); i++) {
                        ThreeAMarkers.get(i).setVisible(true);
                    }
                } else {
                    for (Marker m : ThreeAMarkers) {
                        m.setVisible(false);
                    }
                    for (Marker m2 : ThreeAMarkersF2) {
                        m2.setVisible(true);
                    }
                }
                break;
            case ("3B"):
                if (floorPicked == 1) {
                    for (int i = 0; i < ThreeBMarkers.size(); i++) {
                        ThreeBMarkers.get(i).setVisible(true);
                    }
                }
                break;
            case ("3BC"):
                if (floorPicked == 1) {
                    for (int i = 0; i < ThreeBCMarkers.size(); i++) {
                        ThreeBCMarkers.get(i).setVisible(true);
                    }
                }
                break;
            case ("3C"):
                if (floorPicked == 1) {
                    for (int i = 0; i < ThreeCMarkers.size(); i++) {
                        ThreeCMarkers.get(i).setVisible(true);
                    }
                }
                break;
            case ("3D"):
                if (floorPicked == 1) {
                    for (int i = 0; i < ThreeDMarkers.size(); i++) {
                        ThreeDMarkers.get(i).setVisible(true);
                    }
                }
                break;
            case ("3E"):
                if (floorPicked == 1) {
                    for (int i = 0; i < ThreeEMarkers.size(); i++) {
                        ThreeEMarkers.get(i).setVisible(true);
                    }
                }
                break;
            case ("3F"):
                if (floorPicked == 1) {
                    for (int i = 0; i < ThreeFMarkers.size(); i++) {
                        ThreeFMarkers.get(i).setVisible(true);
                    }
                }
            case("4A"):
                if(floorPicked == 1){
                    for(int i = 0; i < FourAMarkers.size(); i++){
                        FourAMarkers.get(i).setVisible(true);
                    }
                }
                break;

        }
    }

    //Functions for moving the cammer, overload for zoom option
    private void moveCamera(LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    private void moveCamera(LatLng latlng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));
    }

    //Init runs on map start, used for currentlocation button
    private void Init() {
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocationCameraMove();
            }
        });
    }

    //Runs on map ready, used for search bar
    private void SearchReady() {
        SearchList = new ArrayList<String>();
        for (Marker m : MarkersList) {
            if (m.getTitle() != null) {
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
    private void Locate() {
        String searchstring = Search.getText().toString();
        Marker searched = null;
        for (Marker m : MarkersList) {
            if (searchstring.equals(m.getTitle())) {
                searched = m;
                break;
            }
        }
        if (null != searched) {
//            onMarkerClick(searched);
            markerFragment.MTouch.moveCamera(searched.getPosition());
            markerFragment.MTouch.ManualTouch(searched);
        } else {
            Toast.makeText(getApplicationContext(), "No Results Found", Toast.LENGTH_SHORT).show();
        }
    }

    private void LoadMarkers() {
        Activity act = this;
        Context con = this;
        createdMarkers = new ArrayList<>();
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("/Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/CustomMarkers/");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    String markerTitle = dataSnapshot.getKey().toString();
                    double latitude1 = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                    double longitude1 = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());
                    LatLng newLatLng = new LatLng(latitude1, longitude1);
                    MarkerOptions newMarkerOption = new MarkerOptions().position(newLatLng).title(markerTitle);
                    if (!wasRemoveHit) {
                        Marker newMarker = mMap.addMarker(newMarkerOption);
//                        newMarker.showInfoWindow();
                        if (FindTheMarker(markerTitle) == null) {
                            createdMarkers.add(newMarker);
                        }
                    }
                }
                markerFragment.MTouch.CustomMarkers(createdMarkers);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void LoadFavoriteMarkers() {
        favoritedMarkers = new ArrayList<>();
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("/Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/Favorites/");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Marker found = FindTheMarker(dataSnapshot.getKey());
                    if (found != null && createdMarkers != null) {
                        if (CheckMarkerType(found)) {
                            for (Marker m : createdMarkers) {
                                if (m.getTitle().equals(found.getTitle())) {
                                    favoritedMarkers.add(found);
                                }
                            }
                        } else {
                            for (Marker m : MarkersList) {
                                if (m.getTitle().equals(found.getTitle())) {
                                    favoritedMarkers.add(found);
                                }
                            }
                        }
                    }
                }
                markerFragment.MTouch.FavoriteMarkers(favoritedMarkers);
                FMReady = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        options.inScaled = true; // Enable scaling
        options.inPreferredConfig = Bitmap.Config.ARGB_8888; // High quality color
        options.inDither = true; // Optional, to improve color depth on some devices

        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // This while loop determines a scaled down size that maintains the aspect ratio
            // and ensures neither width nor height is smaller than the requested width and height.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
    private Bitmap decodeAndScaleBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // Decodifica el bitmap a un tamaño aproximado
        Bitmap sampledBitmap = decodeSampledBitmapFromResource(res, resId, reqWidth, reqHeight);

        // Escala el bitmap al tamaño exacto
        return Bitmap.createScaledBitmap(sampledBitmap, reqWidth, reqHeight, true);
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


        loadingScreenMaps.setVisibility(View.VISIBLE);
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                // region Bitmap Creations
                //BITMAP  CREATIONS

                Bitmap newBitmap1 = decodeSampledBitmapFromResource(getResources(),R.drawable.building_3a_blackmoore_1f,512,512);
                Bitmap newBitmap2 = decodeSampledBitmapFromResource(getResources(),R.drawable.buildinglibrary_rotated_1_left, 512,512);
                Bitmap newBitmap3 = decodeSampledBitmapFromResource(getResources(),R.drawable.building_3a_blackmoore_2f, 512,512);
                Bitmap newBitmap4 = decodeSampledBitmapFromResource(getResources(),R.drawable.building_3b_fishbowl,512,512);
                Bitmap newBitmap5 = decodeSampledBitmapFromResource(getResources(),R.drawable.building_3c_gd,512,512);
                Bitmap newBitmap6 = decodeSampledBitmapFromResource(getResources(),R.drawable.building_3c_mp,512,512);
                Bitmap newBitmap7 = decodeSampledBitmapFromResource(getResources(),R.drawable.building_3f_1f,512,512);
                Bitmap newBitmap8 = decodeSampledBitmapFromResource(getResources(),R.drawable.building_4c,512,512);
                Bitmap newBitmap9 = decodeSampledBitmapFromResource(getResources(),R.drawable.building_4b_1f,512,512);
                Bitmap newBitmap10 = decodeSampledBitmapFromResource(getResources(),R.drawable.building_4a_wd1,512,512);
                Bitmap newBitmap11 = decodeSampledBitmapFromResource(getResources(),R.drawable.building_4a_wd2,512,512);
                Bitmap newBitmap12 = decodeSampledBitmapFromResource(getResources(),R.drawable.building_4a_fc,512,512);
                Bitmap newBitmap13 = decodeSampledBitmapFromResource(getResources(),R.drawable.building_4d_1f,512,512);
                Bitmap newBitmap14 = decodeSampledBitmapFromResource(getResources(),R.drawable.building_4e_distrubution,512,512);
                Bitmap newBitmap15 = decodeSampledBitmapFromResource(getResources(),R.drawable.building_1_1f,512,512);
                Bitmap newBitmap16 = decodeSampledBitmapFromResource(getResources(),R.drawable.building_1_2f,512,512);
                Bitmap newBitmap17 = decodeSampledBitmapFromResource(getResources(),R.drawable.building_2_1f,512,512);
                Bitmap newBitmap18 = decodeSampledBitmapFromResource(getResources(),R.drawable.building_2_2f,512,512);
                Bitmap newBitmap19 = decodeSampledBitmapFromResource(getResources(),R.drawable.building_3b_gd,512,512); // THIS IS 3BGD


                BitmapDescriptor build3aF1BitMap = BitmapDescriptorFactory.fromBitmap(newBitmap1);
                BitmapDescriptor buildingLibrary = BitmapDescriptorFactory.fromBitmap(newBitmap2);
                BitmapDescriptor build3aF2Bitmap = BitmapDescriptorFactory.fromBitmap(newBitmap3);
                BitmapDescriptor build3bFishbowlBitmap = BitmapDescriptorFactory.fromBitmap(newBitmap4);
                BitmapDescriptor build3bGDBitmap = BitmapDescriptorFactory.fromBitmap(newBitmap19); // THIS IS 3BGD
                BitmapDescriptor build3cGDBitmap = BitmapDescriptorFactory.fromBitmap(newBitmap5);
                BitmapDescriptor build3cMPBitmap = BitmapDescriptorFactory.fromBitmap(newBitmap6);
                BitmapDescriptor build3fF1Bitmap = BitmapDescriptorFactory.fromBitmap(newBitmap7);
                BitmapDescriptor build4CBitmap = BitmapDescriptorFactory.fromBitmap(newBitmap8);
                BitmapDescriptor build4BF1Bitmap = BitmapDescriptorFactory.fromBitmap(newBitmap9);
                BitmapDescriptor build4aWD1Bitmap = BitmapDescriptorFactory.fromBitmap(newBitmap10);
                BitmapDescriptor build4aWD2Bitmap = BitmapDescriptorFactory.fromBitmap(newBitmap11);
                BitmapDescriptor build4aFCBitmap= BitmapDescriptorFactory.fromBitmap(newBitmap12);
                BitmapDescriptor build4dF1Bitmap = BitmapDescriptorFactory.fromBitmap(newBitmap13);
                BitmapDescriptor build4eDistrubutionBitmap = BitmapDescriptorFactory.fromBitmap(newBitmap14);
                BitmapDescriptor build1F1Bitmap = BitmapDescriptorFactory.fromBitmap(newBitmap15);
                BitmapDescriptor build1F2Bitmap = BitmapDescriptorFactory.fromBitmap(newBitmap16);
                BitmapDescriptor build2F1Bitmap = BitmapDescriptorFactory.fromBitmap(newBitmap17);
                BitmapDescriptor build2F2Bitmap = BitmapDescriptorFactory.fromBitmap(newBitmap18);
                // endregion

                // region  Boundaries

                 LatLngBounds buildLibrary = new LatLngBounds(
                 new LatLng(28.59379993356988, -81.30450729197996),
                 new LatLng(28.594005193975605, -81.30415971195876));

                LatLngBounds build3A = new LatLngBounds(
                        new LatLng(28.595392200538452, -81.30425629914613),
                        new LatLng(28.59565596435769, -81.30393979848783));
                LatLngBounds build3AF2 = new LatLngBounds(
                        new LatLng(28.595392200538452, -81.30425629914613),
                        new LatLng(28.59565596435769, -81.30393979848783));
                LatLngBounds build3B = new LatLngBounds(
                        new LatLng(28.59489939800887, -81.30421001414925),
                        new LatLng(28.595410442208898, -81.30359042388629));
                LatLngBounds build3BConnect = new LatLngBounds(
                        new LatLng(28.594658645277548, -81.30423153222328),
                        new LatLng(28.594876487499718, -81.30377019229515));
                LatLngBounds build3C = new LatLngBounds(
                        new LatLng(28.594253533934957, -81.3042093605151),
                        new LatLng(28.59463740843301, -81.30378020710396));
                LatLngBounds build3CMP = new LatLngBounds(
                        new LatLng(28.59401920494417, -81.30419863168258),
                        new LatLng(28.59422291811299, -81.30397734945726));
                LatLngBounds build3F = new LatLngBounds(
                        new LatLng(28.593322903500177, -81.30542386327811),
                        new LatLng(28.59398467985881, -81.30454409878536));
                LatLngBounds build4C = new LatLngBounds(
                        new LatLng(28.591369859267225, -81.3042459017761),
                        new LatLng(28.591409896421553, -81.30413526066346));
                LatLngBounds build4B = new LatLngBounds(
                        new LatLng(28.591588937000083, -81.30422856031794),
                        new LatLng(28.592005130777046, -81.30363824532108));
                LatLngBounds build4A = new LatLngBounds(
                        new LatLng(28.5926339005131, -81.30546789052039),
                        new LatLng(28.592869410133137, -81.30476246959658));
                LatLngBounds build4AWD2 = new LatLngBounds(
                        new LatLng(28.592030993472754, -81.30421731065695),
                        new LatLng(28.592066320152245, -81.30417908918166));
                LatLngBounds build4AFC = new LatLngBounds(
                        new LatLng(28.59226717692391, -81.30473598372107),
                        new LatLng(28.592838288837115, -81.30406274931435));
                LatLngBounds build4D = new LatLngBounds(
                        new LatLng(28.59059641684985, -81.30456270361756),
                        new LatLng(28.590932024410755, -81.30418853548505));
                LatLngBounds build4E = new LatLngBounds(
                        new LatLng(28.590101835337443, -81.30483226561324),
                        new LatLng(28.59086254784375, -81.30463378216082));
                LatLngBounds build1_1f = new LatLngBounds(
                        new LatLng(28.59600450841536, -81.3020220190869),
                        new LatLng(28.596635652774378, -81.30086330482766)
                );
                LatLngBounds build1_2f = new LatLngBounds(
                        new LatLng(28.59600450841536, -81.3020220190869),
                        new LatLng(28.596635652774378, -81.30086330482766)
                );
                LatLngBounds build2_1f = new LatLngBounds(
                        new LatLng(28.59575016558735, -81.30298761430296),
                        new LatLng(28.596852313396923, -81.30218295162292)
                );
                LatLngBounds build2_2f = new LatLngBounds(
                        new LatLng(28.59575016558735, -81.30298761430296),
                        new LatLng(28.596852313396923, -81.30218295162292)
                );

                // endregion
                GroundOverlayOptions buildLibraryOverlay = new GroundOverlayOptions()
                        .positionFromBounds(buildLibrary)
                        .image(buildingLibrary)
                        .anchor(0.43f, 0.45f);
                GroundOverlayOptions build3aOverlay = new GroundOverlayOptions()
                        .positionFromBounds(build3A)
                        .image(build3aF1BitMap)
                        .anchor(1.0f, -0.1f)
                        .bearing(-2);
                GroundOverlayOptions build3aF2Overlay = new GroundOverlayOptions()
                        .positionFromBounds(build3AF2)
                        .image(build3aF2Bitmap)
                        .anchor(1.0f, -0.1f)
                        .bearing(-2);
                GroundOverlayOptions building3BOverlay = new GroundOverlayOptions()
                        .positionFromBounds(build3B)
                        .image(build3bFishbowlBitmap)
                        .anchor(0.45f, 0.45f);
                GroundOverlayOptions build3BConnected = new GroundOverlayOptions()
                        .positionFromBounds(build3BConnect)
                        .image(build3bGDBitmap)
                        .anchor(0.08f, 0.77f);
                GroundOverlayOptions build3COverlay = new GroundOverlayOptions()
                        .positionFromBounds(build3C)
                        .image(build3cGDBitmap);
                GroundOverlayOptions build3DOverlay = new GroundOverlayOptions()
                        .positionFromBounds(build3CMP)
                        .image(build3cMPBitmap)
                        .anchor(0.7f, 0.7f);
                GroundOverlayOptions build3FOverlay = new GroundOverlayOptions()
                        .positionFromBounds(build3F)
                        .image(build3fF1Bitmap);
                GroundOverlayOptions build4COverlay = new GroundOverlayOptions()
                        .positionFromBounds(build4C)
                        .image(build4CBitmap)
                        .bearing(135)
                        .anchor(0.48f, 0.62f);
                GroundOverlayOptions build4BOverlay = new GroundOverlayOptions()
                        .positionFromBounds(build4B)
                        .image(build4BF1Bitmap);
                GroundOverlayOptions build4AOverlay = new GroundOverlayOptions()
                        .positionFromBounds(build4A)
                        .image(build4aWD1Bitmap);
                GroundOverlayOptions build4AWD2Overlay = new GroundOverlayOptions()
                        .positionFromBounds(build4AWD2)
                        .image(build4aWD2Bitmap)
                        .bearing(44)
                        .anchor(0.6f, 0.75f);
                GroundOverlayOptions build4AFCOverlay = new GroundOverlayOptions()
                        .positionFromBounds(build4AFC)
                        .image(build4aFCBitmap)
                        .bearing(-46);
                GroundOverlayOptions build4DOverlay = new GroundOverlayOptions()
                        .positionFromBounds(build4D)
                        .image(build4dF1Bitmap)
                        .bearing(45);
                GroundOverlayOptions build4EOverlay = new GroundOverlayOptions()
                        .positionFromBounds(build4E)
                        .image(build4eDistrubutionBitmap)
                        .bearing(45)
                        .anchor(0.5f, 0.5f);
                GroundOverlayOptions build1f1Overlay = new GroundOverlayOptions()
                        .positionFromBounds(build1_1f)
                        .image(build1F1Bitmap)
                        .bearing(180)
                        .anchor(0.56f, 0.5f);
                GroundOverlayOptions build1f2Overlay = new GroundOverlayOptions()
                        .positionFromBounds(build1_2f)
                        .image(build1F2Bitmap)
                        .bearing(180)
                        .anchor(0.558f, 0.485f);

                GroundOverlayOptions build2f1Overlay = new GroundOverlayOptions()
                        .positionFromBounds(build2_1f)
                        .image(build2F1Bitmap)
                        .bearing(-117.5f)
                        .anchor(0.605f, 0.387f);
                GroundOverlayOptions build2f2Overlay = new GroundOverlayOptions()
                        .positionFromBounds(build2_2f)
                        .image(build2F2Bitmap)
                        .bearing(-117.5f)
                        .anchor(0.605f, 0.387f);



                //add groundOverlay and create reference.
                groundOverlaysf1.add(build3aOverlay);//0
                groundOverlaysf2.add(build3aF2Overlay);//0f2
                groundOverlaysf1.add(building3BOverlay);//1
                groundOverlaysf1.add(build3BConnected);//2
                groundOverlaysf1.add(build3COverlay);//3
                groundOverlaysf1.add(build3DOverlay);//4
                groundOverlaysf1.add(buildLibraryOverlay);//5
                groundOverlaysf1.add(build3FOverlay);//6
                groundOverlaysf1.add(build4AOverlay);//7
                groundOverlaysf1.add(build4AWD2Overlay);//8
                groundOverlaysf1.add(build4AFCOverlay);//9
                groundOverlaysf1.add(build4BOverlay);//10
                groundOverlaysf1.add(build4COverlay);//11
                groundOverlaysf1.add(build4DOverlay);//12
                groundOverlaysf1.add(build4EOverlay);//13
                groundOverlaysf1.add(build1f1Overlay);//14
                groundOverlaysf1.add(build2f1Overlay);//15
                groundOverlaysf2.add(build1f2Overlay);//1f2
                groundOverlaysf2.add(build2f2Overlay);//2f2


                //B1
                B1.add(mMap.addGroundOverlay(groundOverlaysf1.get(groundOverlaysf1.size() - 2)));
                B1.get(B1.size() - 1).setDimensions(140, 90);
                AllFloorOneOverlays.add(B1.get(B1.size()-1));
                B1.add(mMap.addGroundOverlay(groundOverlaysf2.get(groundOverlaysf2.size() - 2)));
                B1.get(B1.size() - 1).setDimensions(136, 94);
                B1.get(B1.size()-1).setVisible(false);
                AllSecondFloorOverlays.add(B1.get(B1.size()-1));
                resultsList.add("B1");
                //B2
                B2.add(mMap.addGroundOverlay(groundOverlaysf1.get(groundOverlaysf1.size() - 1)));
                B2.get(B2.size() - 1).setDimensions(157, 95);
                AllFloorOneOverlays.add(B2.get(B2.size()-1));
                B2.add(mMap.addGroundOverlay(groundOverlaysf2.get(groundOverlaysf2.size() - 1)));
                B2.get(B2.size() - 1).setDimensions(157, 95);
                B2.get(B2.size()-1).setVisible(false);
                AllSecondFloorOverlays.add(B2.get(B2.size()-1));
                resultsList.add("B2");
                //4A
                B4U.add(mMap.addGroundOverlay(groundOverlaysf1.get(7)));
                B4U.get(B4U.size() - 1).setDimensions(90, 50);
                FourA = B4U.get(B4U.size()-1);
                AllFloorOneOverlays.add(B4U.get(B4U.size()-1));
                resultsList.add("4A");
                //4AWD2
                DaBigBois.add(mMap.addGroundOverlay(groundOverlaysf1.get(8)));
                DaBigBois.get(DaBigBois.size() - 1).setDimensions(56, 43);
                FourAWD2 = DaBigBois.get(DaBigBois.size()-1);
                AllFloorOneOverlays.add(DaBigBois.get(DaBigBois.size()-1));
                resultsList.add("4AWD2");
                //4AFC
                B4U.add(mMap.addGroundOverlay(groundOverlaysf1.get(9)));
                B4U.get(B4U.size() - 1).setDimensions(70, 50);
                FourAFC = B4U.get(B4U.size()-1);
                AllFloorOneOverlays.add(B4U.get(B4U.size()-1));
                resultsList.add("4AFC");
                //4c
                B4D.add(mMap.addGroundOverlay(groundOverlaysf1.get(11)));
                B4D.get(B4D.size() - 1).setDimensions(72, 54);
                FourC = B4D.get(B4D.size()-1);
                AllFloorOneOverlays.add(B4D.get(B4D.size()-1));
                resultsList.add("4C");
                //4e
                B4D.add(mMap.addGroundOverlay(groundOverlaysf1.get(12)));
                B4D.get(B4D.size() - 1).setDimensions(100, 60);
                FourE = B4D.get(B4D.size()-1);
                AllFloorOneOverlays.add(B4D.get(B4D.size()-1));
                resultsList.add("4E");
                //4f
                B4D.add(mMap.addGroundOverlay(groundOverlaysf1.get(13)));
                B4D.get(B4D.size() - 1).setDimensions(100, 60);
                FourF = B4D.get(B4D.size()-1);
                AllFloorOneOverlays.add(B4D.get(B4D.size()-1));
                resultsList.add("4F");
                //3A
                B3U.add(mMap.addGroundOverlay(groundOverlaysf1.get(0)));
                B3U2 = mMap.addGroundOverlay(groundOverlaysf2.get(0));
                B3U.get(B3U.size() - 1).setDimensions(34, 28);
                AllFloorOneOverlays.add(B3U.get(B3U.size() - 1));
                ThreeA = B3U.get(B3U.size()-1);
                B3U2.setDimensions(34, 28);
                AllSecondFloorOverlays.add(B3U2);
                resultsList.add("3A");
                if (floorPicked == 1) {
                    B3U2.setVisible(false);
                } else {
                    B3U.get(B3U.size() - 1).setVisible(false);
                }
                //3B
                B3U.add(mMap.addGroundOverlay(groundOverlaysf1.get(1)));
                B3U.get(B3U.size() - 1).setDimensions(84, 62);
                ThreeB = B3U.get(B3U.size()-1);
                AllFloorOneOverlays.add(B3U.get(B3U.size()-1));
                resultsList.add("FishBowl");
                //3BC
                B3U.add(mMap.addGroundOverlay(groundOverlaysf1.get(2)));
                B3U.get(B3U.size() - 1).setDimensions(64, 30);
                ThreeBC = B3U.get(B3U.size()-1);
                AllFloorOneOverlays.add(B3U.get(B3U.size()-1));
                resultsList.add("3BConnected");
                //3C
                B3D.add(mMap.addGroundOverlay(groundOverlaysf1.get(3)));
                B3D.get(B3D.size() - 1).setDimensions(40, 42);
                ThreeC = B3D.get(B3D.size()-1);
                AllFloorOneOverlays.add(B3D.get(B3D.size()-1));
                resultsList.add("3C");
                //3D
                B3D.add(mMap.addGroundOverlay(groundOverlaysf1.get(4)));
                B3D.get(B3D.size() - 1).setDimensions(40, 30);
                ThreeD = B3D.get(B3D.size()-1);
                AllFloorOneOverlays.add(B3D.get(B3D.size()-1));
                resultsList.add("3D");
                //3E
                DaBigBois.add(mMap.addGroundOverlay(groundOverlaysf1.get(5)));
                DaBigBois.get(DaBigBois.size() - 1).setDimensions(37, 28);
                ThreeE = DaBigBois.get(DaBigBois.size()-1);
                AllFloorOneOverlays.add(DaBigBois.get(DaBigBois.size()-1));
                resultsList.add("3E");
                //3F
                DaBigBois.add(mMap.addGroundOverlay(groundOverlaysf1.get(6)));
                DaBigBois.get(DaBigBois.size() - 1).setDimensions(100, 80);
                ThreeF = DaBigBois.get(DaBigBois.size()-1);
                AllFloorOneOverlays.add(DaBigBois.get(DaBigBois.size()-1));
                resultsList.add("3F");


                String result = DoTheChecks();
                String secondResult = secondCheckForFinerArea(result);
                CheckResults(result,secondResult);
                prevResult = result;


                loadingScreenMaps.setVisibility(View.GONE);
                new DoDahClick().execute();
            }
        });
        SharedPreferences settings = getSharedPreferences("SOME_NAME", 0);
        float longitude = settings.getFloat("longitude", (float) Longitued);
        float latitude = settings.getFloat("latitude", (float) Latitude);
        float zoomload = settings.getFloat("zoom", zoom);
        floorPicked = settings.getInt("floorPicked", floorPicked);

        if(zoomload <20){
            zoomload  = 20;
        }
        LatLng startPosition = new LatLng(latitude, longitude);
        cameraLoad = new CameraPosition.Builder()
                .target(startPosition)
                .zoom(zoomload)
                .build();
        registerLocationManager();

        if (mLocationPermissionsGranted) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    GPS_UPDATE_TIME, 1, locationListener);
        }
        //get latlong for corners for specified place
        LatLng one = new LatLng(28.5899089565466, -81.30689695755838);
        LatLng two = new LatLng(28.597315583066404, -81.29914504373565);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        //add them to builder
        builder.include(one);
        builder.include(two);

        LatLngBounds bounds = builder.build();

        //get width and height to current display screen
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        //20% Padding
        int padding = (int) (width * 0.18);

        //set latlong bounds
        mMap.setLatLngBoundsForCameraTarget(bounds);

        //move camera to fill the bound to screen
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));

        //set zoom
        mMap.setMinZoomPreference(mMap.getCameraPosition().zoom);
        moveCamera(mMap.getCameraPosition().target, 16f);
        //Location tracking
        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;

            }
//            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            Init();
        }

        //Marker stuffs
        //Markers for classrooms
        Bitmap bitmapdraw1 = decodeAndScaleBitmapFromResource(getResources(), R.drawable.pixilart_drawing, 100, 100);

        //Markers for Bathrooms
        Bitmap bitmapdraw2 = decodeAndScaleBitmapFromResource(getResources(), R.drawable.pixil_frame_0, 100, 100);
        //Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, 100, 100, false);

        //Markers for WaterZones
        Bitmap bitmapdraw3 = decodeAndScaleBitmapFromResource(getResources(),R.drawable.pixilart_drawing__1_, 100, 100);
        //Bitmap smallMarker3 = Bitmap.createScaledBitmap(b3, 100, 100, false);

        //Markers for ETCRooms
        Bitmap bitmapdraw4 = decodeAndScaleBitmapFromResource(getResources(),R.drawable.etc_marker,100,100);
       // Bitmap smallMarker4 = Bitmap.createScaledBitmap(b4, 100, 100, false);

        //Markers for OFRooms
        Bitmap bitmapdraw5 = decodeAndScaleBitmapFromResource(getResources(),R.drawable.office_marker,100,100);
        //Bitmap smallMarker5 = Bitmap.createScaledBitmap(b5, 100, 100, false);

        //On Marker Click Override
        markerFragment.MTouch.setGoogleMap(mMap, linesShowing, this.getApplicationContext(), this);

        // disable marker click processing
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                return true;
            }
        });

        CSVReader creader = new CSVReader();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference sr = storage.getReference();
        StorageReference csvref = sr.child("Classrooms/rooms.csv");
        Context con = this;
        Activity act = this;
        csvref.getBytes(2048 * 2048).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                creader.CreateRoomList(bytes);
                ArrayList<String[]> roomlist = creader.GetRoomList();

                for (String[] room : roomlist) {
                    MarkerOptions roommarker = new MarkerOptions().position(new LatLng(Double.parseDouble(room[1]), Double.parseDouble(room[2]))).title(room[0]).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    roommarker.anchor(0.5f, 0.5f);
                    Marker mark = mMap.addMarker(roommarker);
                    MarkersList.add(mark);
                    switch (room[3]) {
                        case ("CR"):
                            ClassRoomMarkers.add(mark);
                            break;
                        case ("BR"):
                            BathroomMarkers.add(mark);
                            break;
                        case ("WZ"):
                            WaterZones.add(mark);
                            break;
                        case ("ETC"):
                            ETCRooms.add(mark);
                            break;
                        case ("OF"):
                            OFRooms.add(mark);

                    }
                    int floorgrabbed = 0;
                    switch (room[4]) {
                        case "1":
                            floorgrabbed = 1;
                            break;
                        case "2":
                            floorgrabbed = 2;
                            break;
                    }
                    switch (room[5]) {
                        case ("1"):
                            if (floorgrabbed == 1) {
                                BuildingOne.add(mark);
                            } else if (floorgrabbed == 2) {
                                BuildingOneF2.add(mark);
                            }
                            break;
                        case ("2"):
                            if (floorgrabbed == 1) {
                                BuildingTwo.add(mark);
                            } else if (floorgrabbed == 2) {
                                BuildingTwoF2.add(mark);
                            }
                            break;
                        case ("3A"):
                            if (floorgrabbed == 1) {
                                ThreeAMarkers.add(mark);
                            } else if (floorgrabbed == 2) {
                                ThreeAMarkersF2.add(mark);
                            }
                            break;
                        case ("3B"):
                            ThreeBMarkers.add(mark);
                            break;
                        case("3BC"):
                            ThreeBCMarkers.add(mark);
                            break;
                        case ("3C"):
                            ThreeCMarkers.add(mark);
                            break;
                        case ("3D"):
                            ThreeDMarkers.add(mark);
                            break;
                        case ("3E"):
                            ThreeEMarkers.add(mark);
                            break;
                        case ("3F"):
                            ThreeFMarkers.add(mark);
                            break;
                    }


                }

                //Set Markers image for classrooms
                for (Marker ClassRoom : ClassRoomMarkers) {
                    ClassRoom.setIcon(BitmapDescriptorFactory.fromBitmap(bitmapdraw1));
                }

                //Set Markers image for bathrooms
                for (Marker Bathrooms : BathroomMarkers) {
                    Bathrooms.setIcon(BitmapDescriptorFactory.fromBitmap(bitmapdraw2));
                }

                //Set Markers image for Waterzones
                for (Marker WaterStation : WaterZones) {
                    WaterStation.setIcon(BitmapDescriptorFactory.fromBitmap(bitmapdraw3));
                }
                //Set Markers image for ETC rooms
                for (Marker etc : ETCRooms) {
                    etc.setIcon(BitmapDescriptorFactory.fromBitmap((bitmapdraw4)));
                }
                //Set Marker image for OF rooms
                for (Marker etc : OFRooms) {
                    etc.setIcon(BitmapDescriptorFactory.fromBitmap((bitmapdraw5)));
                }
                markerFragment.MTouch.CSVMarkers(MarkersList);

                SearchReady();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MapsActivity.this, "CSV Failure", Toast.LENGTH_LONG);
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                Toast.makeText(MapsActivity.this, "CSV Cancelled", Toast.LENGTH_LONG);

            }
        });


        //set overlays and markers visibile after certain zoom level is reached.
        //For camera moving
        mMap.setOnCameraMoveListener(() ->
        {
            if(mMap.getCameraPosition().zoom < 18){
                HideAllOverlays();
            }
//            if (groundOverlaysf1.size() > 0) {
//                String result = DoTheChecks();
//                String FinerResult = secondCheckForFinerArea(result);
//                if (FinerResult == "3BConnected" || FinerResult == "FishBowl") {
//                    FinerResult = "3B";
//                }
//                if (prevResult != FinerResult) {
//                    prevResult = FinerResult;
//                    if (!Filtering) {
//                        HideAllOtherMarkers(FinerResult);
//                        showMarkerInArea(FinerResult);
//                    } else {
//                        ShowTheseMarkers();
//                        HideAllOtherMarkers(FinerResult);
//                    }
//                }
//                if (CheckResultLoadType(result)) {
//                    CheckResults(result);
//                } else {
//                    String secondResult = secondCheckForFinerArea(result);
//                    if (!CheckResultLoadType(secondResult)) {
//                        FirstLoad(result, secondResult);
//                    }
//                }
//            }
//            if (wasRemoveHit) {
//                for (int i = 0; i < createdMarkers.size(); i++) {
//                    if (createdMarkers.get(i).getTitle().equals(markerFragment.MTouch.createdMarker.getTitle())) {
//                        createdMarkers.get(i).remove();
//                        createdMarkers.remove(i);
//                    }
//                }
//            }

        });

        //when camera is still (used for searchbar since it doesn't count as camera moving)
        mMap.setOnCameraIdleListener(() -> {

            if (groundOverlaysf1.size() > 0 && !markerFragment.MTouch.wasMarkerClicked) {
                String result = DoTheChecks();
                String FinerResult = secondCheckForFinerArea(result);
                if (prevResult != FinerResult) {

                    prevResult = FinerResult;
                    if (!Filtering) {
                        HideAllOtherMarkers(FinerResult);
                        showMarkerInArea(FinerResult);
                    } else {
                        HideAllOtherMarkers(FinerResult);
                        ShowTheseMarkers();
                    }
                    if(mMap.getCameraPosition().zoom >17) {
                            CheckResults(result, FinerResult);
                    }
                }
            }
            if (FollowUser || wasMarkerClicked) {
                navloc();
            }

        });
        //Slide up code setup
        slideupview = findViewById(R.id.design_bottom_sheet);
        slideupview.setVisibility(View.GONE);
        slidepup = false;

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

        if (floorPicked == 1) {
            downFloor.setVisibility(View.GONE);
        } else {
            upFloor.setVisibility(View.GONE);
            downFloor.setVisibility(View.VISIBLE);
        }

        //prepares searchbar


        //Getting Darkmode option from database
        DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference("/Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/DarkMode/");
        mdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean DarkMode = (boolean) snapshot.getValue();
                    if (DarkMode) {
                        DarkorLight = true;
                        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.style_json));
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        for (PolylineOptions lines1 : customPolyLines) {
                            lines1.pattern(pattern);
                            lines1.width(15);
                            lines1.color(Color.BLUE);
                        }
                    } else {
                        DarkorLight = false;
                        mMap.setMapStyle(null);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        for (PolylineOptions lines1 : customPolyLines) {
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
        if (DarkorLight) {
            for (PolylineOptions lines1 : customPolyLines) {
                lines1.pattern(pattern);
                lines1.width(15);
                lines1.color(Color.BLUE);
            }
        } else {
            for (PolylineOptions lines1 : customPolyLines) {
                lines1.pattern(pattern);
                lines1.width(15);
                lines1.color(Color.parseColor("#FFA500"));
            }
        }
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraLoad));
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("/Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/CustomMarkers/");
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int floor = 1;
                createdMarkers = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Marker newMarker = null;

                    String markerTitle;
                    if (dataSnapshot.child("CustomName").getValue() != null) {
                        markerTitle = dataSnapshot.child("CustomName").getValue().toString();
                    } else {
                        markerTitle = dataSnapshot.getKey().toString();
                    }
                    double latitude1 = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                    double longitude1 = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());
                    if (dataSnapshot.child("Floor").getValue() != null) {
                        floor = Integer.parseInt(dataSnapshot.child("Floor").getValue().toString());
                    }
                    LatLng newLatLng = new LatLng(latitude1, longitude1);
                    MarkerOptions newMarkerOption = new MarkerOptions().position(newLatLng).title(markerTitle);
                    if (!wasRemoveHit) {
                        newMarker = mMap.addMarker(newMarkerOption);
//                        newMarker.showInfoWindow();
                        createdMarkers.add(newMarker);
                    }
                    if (floor == 1 && newMarker != null) {
                        MarkersList.add(newMarker);
                    } else if (newMarker != null) {
                        secondFloorMarkersList.add(newMarker);
                    }
                }
                markerFragment.MTouch.CustomMarkers(createdMarkers);
                markerFragment.MTouch.ThreeA = ThreeAMarkers;
                CMReady =true;
               if(RemoveMarkerTrue){
                    new DoDahRemove().execute();
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        if (favoritedMarkers == null || favoritedMarkers.isEmpty()) {
            LoadFavoriteMarkers();
        }
        if (!favoritedMarkers.isEmpty()) {
            for (int i = 0; i < favoritedMarkers.size(); i++) {
                if (favoritedMarkers.get(i).getTitle().equals(markerTitle2)) {
                    onMarkerClick(favoritedMarkers.get(i));
                    break;
                }
            }
        }
    }

    //Building 1
    LatLng Build1Mid = new LatLng(28.596453482744995, -81.30128234624863);
    ArrayList<LatLng> Build1F1Q1 = new ArrayList<>(Arrays.asList(
            new LatLng(28.596675442516183, -81.30125887691975),
            new LatLng(28.596650126173408, -81.301256865263),
            new LatLng(28.596617156043415, -81.30125652998686),
            new LatLng(28.596585657784573, -81.3012283667922),
            new LatLng(28.596549155117632, -81.30122903734446),
            new LatLng(28.596585069031967, -81.30112811923027),
            new LatLng(28.59658536340828, -81.30105100572109)
    ));

    ArrayList<LatLng> Build1F1Q2 = new ArrayList<>(Arrays.asList(
            new LatLng(28.596690161317344, -81.30131889134645),
            new LatLng(28.596691044445322, -81.3013507425785),
            new LatLng(28.596689572565325, -81.30138259381056),
            new LatLng(28.596670732499394, -81.30138326436281),
            new LatLng(28.596693399453294, -81.30143824964763),
            new LatLng(28.59663952863274, -81.30138225853443),
            new LatLng(28.59664247239413, -81.30135577172041),
            new LatLng(28.596615978538562, -81.30132358521223),
            new LatLng(28.596593311567943, -81.30128636956215),
            new LatLng(28.59655504264543, -81.30129039287567),
            new LatLng(28.596533847543864, -81.3012870401144),
            new LatLng(28.596416096901862, -81.30130916833878),
            new LatLng(28.59665571931942, -81.30159985274076),
            new LatLng(28.59662127731022, -81.30162063986063),
            new LatLng(28.596618039172032, -81.30165617913008),
            new LatLng(28.596554453892672, -81.30162667483091),
            new LatLng(28.596482626030802, -81.30166321992874),
            new LatLng(28.59660125972704, -81.30145568400621),
            new LatLng(28.596603614737027, -81.30157236009836),
            new LatLng(28.596584185903072, -81.30157303065062),
            new LatLng(28.596584774655675, -81.30147311836481),
            new LatLng(28.59652766563845, -81.30142852663994)

    ));

    ArrayList<LatLng> Build1F1Q3 = new ArrayList<>(Arrays.asList(
            new LatLng(28.59644582895142, -81.30168601870537),
            new LatLng(28.59641491939477, -81.30170613527298),
            new LatLng(28.596396668033247, -81.30172155797482),
            new LatLng(28.596429343855675, -81.30178961902857),
            new LatLng(28.596363992200672, -81.30171954631805),
            new LatLng(28.596370468492623, -81.30173832178116),
            new LatLng(28.59635251150032, -81.30174100399017),
            new LatLng(28.596326017571677, -81.30176346749067),
            new LatLng(28.59630599993224, -81.30176916718483),
            new LatLng(28.596274795957324, -81.30178593099117),
            new LatLng(28.596247418877343, -81.3018137589097),
            new LatLng(28.59623181688235, -81.30182381719351),
            new LatLng(28.59627038029975, -81.30190059542655),
            new LatLng(28.596214448621023, -81.30183655768633),
            new LatLng(28.596186777147935, -81.3018586859107),
            new LatLng(28.596169408879177, -81.30186539143324),
            new LatLng(28.59619060405423, -81.3018959015608),
            new LatLng(28.59620414541382, -81.30193412303925),
            new LatLng(28.596157339402584, -81.30184426903725),
            new LatLng(28.596245358236626, -81.3017550855875),
            new LatLng(28.596126724138692, -81.30177788436413),
            new LatLng(28.596099641397807, -81.30172725766897)

    ));

    ArrayList<LatLng> Build1F1Q4 = new ArrayList<>(Arrays.asList(
            new LatLng(28.596034583915635, -81.30067281424999),
            new LatLng(28.596079918092983, -81.30072109401225),
            new LatLng(28.59611229963626, -81.30068957805634),
            new LatLng(28.59618412775118, -81.30074255168437),
            new LatLng(28.596389602989223, -81.30087967962027),
            new LatLng(28.5963887198587, -81.30095444619656),
            new LatLng(28.596023986312908, -81.30071807652712),
            new LatLng(28.5961343779555, -81.30079250782728),
            new LatLng(28.596204734168538, -81.30083408206701),
            new LatLng(28.596230933750483, -81.30077876150608),
            new LatLng(28.59630128989889, -81.30081798881292),
            new LatLng(28.596272440939984, -81.30088437348604),
            new LatLng(28.596269202791028, -81.30090717226267),
            new LatLng(28.596327195079738, -81.30092795938253)
    ));


    public ArrayList<LatLng> ChoosePointsToGrabOutsideToInsideBuilding1(String QuadrantDestination, LatLng Destination) {
        ArrayList<LatLng> points = new ArrayList();
        if (QuadrantDestination == "Q2") {
            if (Destination.longitude < -81.301382258534431) {
                //Left side of Q2
                //Grab first three points
                points.add(Build1F1Q2.get(0));
                points.add(Build1F1Q2.get(1));
//             points.add(Build1F1Q2.get(2));
                //Check for br or dubb
                if (Destination.latitude >= 28.59654533) {
                    //Bathroom
                    points.add(Build1F1Q2.get(6));
                    points.add(Build1F1Q2.get(5));

                    for (LatLng p : Build1F1Q2) {
                        if (p.latitude > 28.59659419469676 && p.latitude < 28.59662127731022 && p.longitude > -81.30162063986063 && p.longitude < -81.30138225853443) {
                            points.add(p);
                        }
                    }
                } else if (Destination.latitude <= 28.59648263) {
                    points.add(Build1F1Q2.get(5));
                    for (LatLng p : Build1F1Q2) {
                        //IDubbz
                        if (p.latitude < 28.59662127731022 && p.longitude > -81.30159985274076 && p.latitude > 28.59648263 && p.longitude < -81.30138225853443) {
                            points.add(p);
                        }
                    }
                }
            } else {
                //Right side of Q2
                for (LatLng p : Build1F1Q2) {
                    if (p.longitude > -81.301382258534431 && p.latitude > Destination.latitude) {
                        points.add(p);
                    }
                }
            }
        } else if (QuadrantDestination == "Q3") {
            points.add(Build1F1Q3.get(3));
            points.add(Build1F1Q3.get(2));

            //112 Sound Studio
            if (Destination.latitude >= 28.59627244 && Destination.latitude <= 28.59640108
                    && Destination.longitude >= -81.30167931 && Destination.longitude <= -81.30155459) {
                points.add(Build1F1Q3.get(4));
            } else if (Destination.latitude > Build1F1Q3.get(2).latitude) {
                points.add(Build1F1Q3.get(1));
                if (Destination.latitude > Build1F1Q3.get(1).latitude) {
                    points.add(Build1F1Q3.get(0));
                }
            } else if (Destination.latitude < Build1F1Q3.get(2).latitude
                    && Destination.longitude < Build1F1Q3.get(2).longitude) {
                points.add(Build1F1Q3.get(5));
                for (int i = 6; i < Build1F1Q3.size(); i++) {
                    if (Destination.latitude < Build1F1Q3.get(i).latitude)
                        if (Build1F1Q3.get(i).latitude < Build1F1Q3.get(i - 1).latitude
                                && Build1F1Q3.get(i).longitude < Build1F1Q3.get(i - 1).longitude) {
                            points.add(Build1F1Q3.get(i));
                        }
                }
                //Bathrooms for q3
                if (Destination.longitude >= -81.30175877 && Destination.longitude <= -81.30172055
                && Destination.latitude <= 28.59627627 && Destination.latitude >= 28.59625802) {
                    points.add(Build1F1Q3.get(10));
                    points.add(Build1F1Q3.get(21));
                } else if (Destination.longitude > -81.30186539143324
                        && Destination.latitude < 28.59617206) {
                    points.add(Build1F1Q3.get(15));
                    points.add(Build1F1Q3.get(18));
                    for (int i = 21; i < Build1F1Q3.size(); i++){
                        if (Destination.longitude > Build1F1Q3.get(i).longitude){
                            points.add(Build1F1Q3.get(i));
                        }
                    }
                    
                }
            }
        }
        return points;
    }
    //Building4A
    public ArrayList<LatLng> ChoosePointsToGrabOutsideToInsideBuilding4A(String QuadrantDestination, LatLng Destination){
        ArrayList<LatLng> points = new ArrayList<>();

        if(Latitude > 28.592680683693704){

        }else{

        }




        return points;
    }
    // VICTORIA BUILDING 2 UP

    ArrayList<LatLng> Q1Building2UP = new ArrayList<>(Arrays.asList(
            new LatLng(28.596650714925627,-81.30265496671198),
            new LatLng(28.59664129488958,-81.30264893174171),
            new LatLng(28.596631580476533,-81.30264390259981),
            new LatLng(28.596613034776436,-81.30263451486826),
            new LatLng(28.596618039172032,-81.30261238664389),
            new LatLng(28.596630402971886,-81.30259227007627),
            new LatLng(28.596647771164466,-81.30256108939648),
            new LatLng(28.59667720877244,-81.3025775179267),
            new LatLng(28.59668339066905,-81.30260065197945),
            new LatLng(28.596714300146743,-81.3026200979948),
            new LatLng(28.596745798367042,-81.30263887345791),
            new LatLng(28.596766404674277,-81.3026687130332),
            new LatLng(28.596763460916343,-81.3026925176382),
            new LatLng(28.596763166540562,-81.3027273863554),
            new LatLng(28.596761105909945,-81.30276393145323),
            new LatLng(28.596762872164756,-81.30281187593937),
            new LatLng(28.59675610152117,-81.3028474152088),
            new LatLng(28.596760222782528,-81.30288496613503),
            new LatLng(28.59675639589697,-81.30291245877743),
            new LatLng(28.596758162151886,-81.3029295578599)));

    ArrayList<LatLng> Q2Building2UP = new ArrayList<>(Arrays.asList(
            new LatLng(28.596599199093273,-81.30256712436676),
            new LatLng(28.596570644592436,-81.30254734307528),
            new LatLng(28.596541795707445,-81.30253460258245),
            new LatLng(28.596521189356164,-81.30251582711935),
            new LatLng(28.596503232389637,-81.30250610411167),
            new LatLng(28.596484686666862,-81.30249939858913),
            new LatLng(28.59646231404457,-81.30248833447695),
            new LatLng(28.5964493614716,-81.30247961729765),
            new LatLng(28.596429343855675,-81.30246553570032),
            new LatLng(28.596422867567355,-81.30246050655842),
            new LatLng(28.596414625017996,-81.30248263478279),
            new LatLng(28.59640903185913,-81.30249738693237),
            new LatLng(28.596397551163722,-81.3025077804923),
            new LatLng(28.59638283232159,-81.30249738693237),
            new LatLng(28.596366935969797,-81.30249101668596),
            new LatLng(28.596310709965394,-81.30245883017778),
            new LatLng(28.596257133325885,-81.30242463201284)));

    ArrayList<LatLng> Q3Building2UP = new ArrayList<>(Arrays.asList(
            new LatLng(28.59622269118608,-81.30240317434072),
            new LatLng(28.596226812468355,-81.30230661481619),
            new LatLng(28.59621121047031,-81.30234517157078),
            new LatLng(28.596157045025098,-81.30236964672804),
            new LatLng(28.596207677942115,-81.30239747464657),
            new LatLng(28.59617912333492,-81.30241893231869),
            new LatLng(28.59616293257483,-81.30244977772236),
            new LatLng(28.596149979964967,-81.30248330533504),
            new LatLng(28.59618059522209,-81.3025004044175),
            new LatLng(28.596147036189763,-81.30244106054306),
            new LatLng(28.596069614872572,-81.30239378660917),
            new LatLng(28.596038410827475,-81.30245681852102),
            new LatLng(28.595985717183076,-81.30234517157078),
            new LatLng(28.59595156933239,-81.30232639610767),
            new LatLng(28.595911239528938,-81.30230024456978),
            new LatLng(28.595822926036657,-81.30231231451035),
            new LatLng(28.595822631658233,-81.30233678966759),
            new LatLng(28.595958045649763,-81.30218289792538)));

    ArrayList<LatLng> Q4Building2UP = new ArrayList<>(Arrays.asList(
            new LatLng(28.595958928783926,-81.30211886018515),
            new LatLng(28.595977768977463,-81.30211785435677),
            new LatLng(28.595965405100813,-81.30195960402489),
            new LatLng(28.595983950915212,-81.30194384604692)));

    public String FindQuadrantForAreaBuilding2UP(LatLng position) {
        double _longitude = position.longitude;
        double _latitdue = position.latitude;


        double latToCheckQ1UP = 28.596560930172874; // LAT CHECKPOINTQ1
        double latToCheckQ2UP =28.596238587559704;
        double latToCheckQ3UP= 28.596238587559704;
        double longToCheckQ4Up = -81.30215540528297;

        if (_latitdue > latToCheckQ1UP) {
            return "Q1";
        }else if(_latitdue > latToCheckQ2UP){

            return "Q2";
        }else if(_latitdue < latToCheckQ3UP && _longitude < longToCheckQ4Up){

            return "Q3";

        }else if(_longitude > longToCheckQ4Up){

            return "Q4";
        }else{
            return "Not found";
        }
    }

    @SuppressLint("SuspiciousIndentation")
    public ArrayList<LatLng> ChoosePointsToGrabOutsideToInsideBuilding2UP(String QuadrantDestination, LatLng Destination) {
        ArrayList<LatLng> points = new ArrayList<>();
        //all of these paths only consider travel from outside to inside. Not travel withing the building.
        //Within building travel will require different logic because there may be shortcuts to be taken within the building.
        boolean rightside = false;
        if (Longitued > -81.30276795476675) {
            rightside = true;
        }
        if (QuadrantDestination.equals("Q1")) {


                if(Destination.latitude > 28.596745798367042 && Destination.longitude < -81.30263887345791){

                    for (LatLng point : Q1Building2UP){

                        if(point.latitude <=28.596745798367042){
                            points.add(point);
                        }
                    }

                    for (LatLng point :Q1Building2UP){

                        if(point.longitude >= Destination.longitude && point.latitude > 28.596745798367042 ){
                            points.add(point);

                        }
                    }

                }else{
                    //Left
                    if(Destination.longitude <-81.3026200979948 && Destination.latitude < 28.596714300146743 ){

                            points.add(Q1Building2UP.get(0));
                            points.add(Q1Building2UP.get(1));


                    }else{
                        //Right

                        for (int i = 0; i < 6 ; i++){
                            points.add(Q1Building2UP.get(i));

                        }

                        for (int i = 6; i < 12; i++){

                            LatLng point = Q1Building2UP.get(i);
                            if(point.latitude <= Destination.latitude){

                                points.add(point);
                            }
                        }

                    }

                }


        } else if (QuadrantDestination.equals("Q2")) {

            if (rightside) {

                if(Latitude > 28.59658124214005){

                    for (int i =1; i < 6; i++){

                        points.add(Q1Building2UP.get(i));

                    }
                    for (LatLng point: Q2Building2UP){
                        if(Destination.longitude <-81.30245883017778){

                            for (LatLng point1: Q2Building2UP){
                                if(point1.latitude <= Destination.latitude){
                                    points.add(point1);
                                }

                            }
                        }else{
                            for (LatLng point2: Q2Building2UP){
                                if(point2.latitude >=28.596422867567355){

                                    points.add(point2);
                                }
                            }
                        }
                    }

                }else{

                    points.add(Q3Building2UP.get(1));
                    points.add(Q3Building2UP.get(2));
                    points.add(Q3Building2UP.get(4));
                    points.add(Q3Building2UP.get(0));

                    if(Destination.longitude <-81.30245883017778){

                        for (int i = Q2Building2UP.size()-1; i >= 0; i--){
                            LatLng point1 = Q2Building2UP.get(i);
                            if(point1.latitude <= Destination.latitude){
                                points.add(point1);
                            }

                        }
                    }else{
                        for (int i = Q2Building2UP.size()-1; i >= 0; i--){
                            LatLng point2 = Q2Building2UP.get(i);
                            if(point2.latitude <=28.596422867567355){

                                points.add(point2);
                            }
                        }
                    }
                }
            } else {

                if(Latitude > 28.59658124214005){
                    for (int i =1; i < 6; i++){

                        points.add(Q1Building2UP.get(i));

                    }
                    if(Destination.longitude <-81.30245883017778){
                        for (LatLng point1: Q2Building2UP){
                            if(point1.latitude >= Destination.latitude){
                                    points.add(point1);
                            }
                        }
                    }else{
                        for (LatLng point2: Q2Building2UP){
                            if(point2.latitude >=28.596422867567355){
                                points.add(point2);
                            }
                        }
                    }
                }else{
                    points.add(Q3Building2UP.get(0));
                    if(Destination.longitude <-81.30245883017778){
                            for (int i = Q2Building2UP.size()-1; i>=0;i--){
                                LatLng point1 = Q2Building2UP.get(i);
                                if(point1.latitude <= Destination.latitude){
                                    points.add(point1);
                                }
                            }
                    }else{
                        for (int i = Q2Building2UP.size()-1; i >= 0; i--){
                            LatLng point2 = Q2Building2UP.get(i);
                            if(point2.latitude <=28.596422867567355){
                                    points.add(point2);
                            }
                        }
                    }
                }
            }
        } else if (QuadrantDestination.equals("Q3")) {
            if (rightside){
                if(Destination.latitude > 28.59596569947886 && Destination.longitude > -81.30241088569164 && Destination.longitude < -81.30215641111135){
                    for (LatLng point :Q3Building2UP){
                        if(point.latitude >= 28.596157045025098 && point.longitude >= -81.30236964672804)
                        {
                            points.add(point);
                        }
                    }
                }else {
                   if(Destination.latitude > 28.59613908799633 && Destination.longitude < -81.30245614796877){
                       points.add(Q3Building2UP.get(1));
                       points.add(Q3Building2UP.get(2));
                       points.add(Q3Building2UP.get(4));
                       points.add(Q3Building2UP.get(5));
                       points.add(Q3Building2UP.get(6));
                       points.add(Q3Building2UP.get(7));
                       points.add(Q3Building2UP.get(8));
                   }
                   else if(Destination.longitude < -81.3025004044175)
                   {
                       points.add(Q3Building2UP.get(1));
                       points.add(Q3Building2UP.get(2));
                       points.add(Q3Building2UP.get(4));
                       points.add(Q3Building2UP.get(5));
                       points.add(Q3Building2UP.get(6));
                       for(LatLng point : Q3Building2UP){
                           if( point.longitude < -81.30237434 &&  point.latitude < Q3Building2UP.get(7).latitude){
                               points.add(point);
                           }
                       }
                   } else if (Destination.longitude  > -81.3025004044175 && Destination.latitude > 28.595958634405854) {
                       points.add(Q3Building2UP.get(1));
                       points.add(Q3Building2UP.get(2));
                       points.add(Q3Building2UP.get(4));
                       points.add(Q3Building2UP.get(5));
                       points.add(Q3Building2UP.get(6));
                       for(LatLng point : Q3Building2UP){
                           if(point.latitude < Q3Building2UP.get(7).latitude && point.longitude> Q3Building2UP.get(11).longitude  && point.latitude >= Destination.latitude){
                               points.add(point);
                           }
                       }
                   } else if (Destination.longitude <-81.30231902003288){
                       points.add(Q3Building2UP.get(1));
                       points.add(Q3Building2UP.get(2));
                       points.add(Q3Building2UP.get(4));
                       points.add(Q3Building2UP.get(5));
                       points.add(Q3Building2UP.get(6));
                       for(LatLng point : Q3Building2UP){
                           if(point.latitude < Q3Building2UP.get(7).latitude && point.longitude> Q3Building2UP.get(11).longitude && point.latitude > 28.595958634405854){
                               points.add(point);
                           }
                       }
                       for(int i = 13; i < Q3Building2UP.size() -1; i ++){
                           LatLng point = Q3Building2UP.get(i);
                           if(point.longitude < -81.30228951573372){
                               points.add(point);
                           }
                       }
                   }
                   else {
                       if(Destination.latitude > 28.595902408183058) {
                           points.add(Q3Building2UP.get(1));
                           points.add(Q3Building2UP.get(2));
                           points.add(Q3Building2UP.get(4));
                           points.add(Q3Building2UP.get(5));
                           points.add(Q3Building2UP.get(6));
                           for(LatLng point : Q3Building2UP){
                               if(point.latitude < Q3Building2UP.get(7).latitude && point.longitude> Q3Building2UP.get(11).longitude && point.latitude > 28.595958634405854){
                                   points.add(point);
                               }
                           }
                            points.add(Q3Building2UP.get(13));
                       }else{
                           points.add(Q3Building2UP.get(1));
                           points.add(Q3Building2UP.get(2));
                           points.add(Q3Building2UP.get(4));
                           points.add(Q3Building2UP.get(5));
                           points.add(Q3Building2UP.get(6));
                           for(LatLng point : Q3Building2UP){
                               if(point.latitude < Q3Building2UP.get(7).latitude && point.longitude> Q3Building2UP.get(11).longitude && point.latitude > 28.595958634405854){
                                   points.add(point);
                               }
                           }
                           points.add(Q3Building2UP.get(13));
                           for (LatLng point: Q3Building2UP){
                               if(point.longitude >-81.30231902003288 &&  point.longitude < -81.30228951573372 && point.latitude < 28.595958634405854){
                                   points.add(point);
                               }
                           }
                       }
                   }
                }
            }else{
                if(Destination.latitude > 28.59596569947886 && Destination.longitude > -81.30241088569164 && Destination.longitude < -81.30215641111135){
                    points.add(Q3Building2UP.get(0));
                    points.add(Q3Building2UP.get(4));
                    points.add(Q3Building2UP.get(2));
                    points.add(Q3Building2UP.get(3));
                }else {
                    if (Destination.latitude > 28.59613908799633 && Destination.longitude < -81.30245614796877) {
                        points.add(Q3Building2UP.get(0));
                        points.add(Q3Building2UP.get(4));
                        points.add(Q3Building2UP.get(5));
                        points.add(Q3Building2UP.get(6));
                        points.add(Q3Building2UP.get(7));
                        points.add(Q3Building2UP.get(8));
                    } else if (Destination.longitude < -81.3025004044175) {
                        points.add(Q3Building2UP.get(0));
                        points.add(Q3Building2UP.get(4));
                        points.add(Q3Building2UP.get(5));
                        points.add(Q3Building2UP.get(6));
                        for (LatLng point : Q3Building2UP) {
                            if (point.longitude < -81.30237434 && point.latitude < Q3Building2UP.get(7).latitude) {
                                points.add(point);
                            }
                        }
                    } else if (Destination.longitude > -81.3025004044175 && Destination.latitude > 28.595958634405854) {
                        points.add(Q3Building2UP.get(0));
                        points.add(Q3Building2UP.get(4));
                        points.add(Q3Building2UP.get(5));
                        points.add(Q3Building2UP.get(6));
                        for (LatLng point : Q3Building2UP) {
                            if (point.latitude < Q3Building2UP.get(7).latitude && point.longitude > Q3Building2UP.get(11).longitude && point.latitude >= Destination.latitude) {
                                points.add(point);
                            }
                        }
                    } else if (Destination.longitude < -81.30231902003288) {
                        points.add(Q3Building2UP.get(0));
                        points.add(Q3Building2UP.get(4));
                        points.add(Q3Building2UP.get(5));
                        points.add(Q3Building2UP.get(6));
                        for (LatLng point : Q3Building2UP) {
                            if (point.latitude < Q3Building2UP.get(7).latitude && point.longitude > Q3Building2UP.get(11).longitude && point.latitude > 28.595958634405854) {
                                points.add(point);
                            }
                        }
                        for (int i = 13; i < Q3Building2UP.size() - 1; i++) {
                            LatLng point = Q3Building2UP.get(i);
                            if (point.longitude < -81.30228951573372) {
                                points.add(point);
                            }
                        }
                    } else {
                        if (Destination.latitude > 28.595902408183058) {
                            points.add(Q3Building2UP.get(0));
                            points.add(Q3Building2UP.get(4));
                            points.add(Q3Building2UP.get(5));
                            points.add(Q3Building2UP.get(6));
                            for (LatLng point : Q3Building2UP) {
                                if (point.latitude < Q3Building2UP.get(7).latitude && point.longitude > Q3Building2UP.get(11).longitude && point.latitude > 28.595958634405854) {
                                    points.add(point);
                                }
                            }
                            points.add(Q3Building2UP.get(13));
                        } else {
                            points.add(Q3Building2UP.get(0));
                            points.add(Q3Building2UP.get(4));
                            points.add(Q3Building2UP.get(5));
                            points.add(Q3Building2UP.get(6));
                            for (LatLng point : Q3Building2UP) {
                                if (point.latitude < Q3Building2UP.get(7).latitude && point.longitude > Q3Building2UP.get(11).longitude && point.latitude > 28.595958634405854) {
                                    points.add(point);
                                }
                            }
                            points.add(Q3Building2UP.get(13));
                            for (LatLng point : Q3Building2UP) {
                                if (point.longitude > -81.30231902003288 && point.longitude < -81.30228951573372 && point.latitude < 28.595958634405854) {
                                    points.add(point);
                                }
                            }
                        }
                    }
                }
            }
        } else if (QuadrantDestination.equals("Q4")) {
            if (rightside) {
                points.add(Q3Building2UP.get(1));
                points.add(Q3Building2UP.get(2));
                points.add(Q3Building2UP.get(4));
                points.add(Q3Building2UP.get(5));
                points.add(Q3Building2UP.get(6));
                for (LatLng point : Q3Building2UP) {
                    if (point.latitude < Q3Building2UP.get(7).latitude && point.longitude > Q3Building2UP.get(11).longitude && point.latitude > 28.595958634405854) {
                        points.add(point);
                    }
                }
                for (LatLng point : Q3Building2UP) {
                    if (point.latitude < 28.59596569947886 && point.latitude > 28.595902408183058) {
                        points.add(point);
                    }
                }
                if (Destination.longitude < -81.30211886018515) {
                    points.add(Q4Building2UP.get(0));
                    points.add(Q4Building2UP.get(1));
                } else {
                    points.add(Q4Building2UP.get(0));
                    points.add(Q4Building2UP.get(2));
                    points.add(Q4Building2UP.get(3));
                }
            } else {
                points.add(Q3Building2UP.get(0));
                points.add(Q3Building2UP.get(4));
                points.add(Q3Building2UP.get(5));
                points.add(Q3Building2UP.get(6));
                for (LatLng point : Q3Building2UP) {
                    if (point.latitude < Q3Building2UP.get(7).latitude && point.longitude > Q3Building2UP.get(11).longitude && point.latitude > 28.595958634405854) {
                        points.add(point);
                    }
                }
                for (LatLng point : Q3Building2UP) {
                    if (point.latitude < 28.59596569947886 && point.latitude >28.595902408183058) {
                        points.add(point);
                    }
                }
                if (Destination.longitude < -81.30211886018515) {
                    points.add(Q4Building2UP.get(0));
                    points.add(Q4Building2UP.get(1));
                } else {
                    points.add(Q4Building2UP.get(0));
                    points.add(Q4Building2UP.get(2));
                    points.add(Q4Building2UP.get(3));
                }
            }
        }
        return points;
    }


    //Victoria Building 2Down

    ArrayList<LatLng> Q1Building2Down = new ArrayList<>(Arrays.asList(new LatLng(28.596566523323645, -81.30287457257509),
            new LatLng(28.596641294889583, -81.302948333323),
            new LatLng(28.596690455693327, -81.30294196307659),
            new LatLng(28.596731962701362, -81.30293928086758),
            new LatLng(28.596850007365035, -81.30269721150398),
            new LatLng(28.596860899259983, -81.30281589925289),
            new LatLng(28.596862371137615, -81.30278337746859),
            new LatLng(28.59685824988025, -81.30275152623653),
            new LatLng(28.59684500298048, -81.30288798362017),
            new LatLng(28.596862665513115, -81.30268346518278),
            new LatLng(28.596855011749334, -81.30266603082418),
            new LatLng(28.596810561033315, -81.30272973328829),
            new LatLng(28.596806439773914, -81.30269721150398),
            new LatLng(28.59682380793741, -81.30266066640615),
            new LatLng(28.59681880355161, -81.30263820290565),
            new LatLng(28.596170292011557, -81.30186472088099),
            new LatLng(28.596819686678543, -81.302610039711),
            new LatLng(28.596834111083876, -81.30261104553938),
            new LatLng(28.596813504789925, -81.30257483571768),
            new LatLng(28.596774058444492, -81.30256108939648),
            new LatLng(28.596735495211924, -81.3025550544262),
            new LatLng(28.59672372017625, -81.30253560841084),
            new LatLng(28.596716066402344, -81.30258791148663),
            new LatLng(28.596731668325454, -81.30263585597277),
            new LatLng(28.596673970635955, -81.30259864032269),
            new LatLng(28.59669251632532, -81.30264054983854),
            new LatLng(28.596639823008875, -81.30274683237076),
            new LatLng(28.596676031268277, -81.30251381546259),
            new LatLng(28.596661901217324, -81.3025014102459),
            new LatLng(28.59663393548586, -81.30247827619314),
            new LatLng(28.59663393548586, -81.30247827619314),
            new LatLng(28.59659125093402, -81.30244340747595),
            new LatLng(28.596559458291043, -81.30243267863989))
    );
    ArrayList<LatLng> Q2Building2Down = new ArrayList<>(Arrays.asList(
            new LatLng(28.596272440939984, -81.3023491948843),
            new LatLng(28.59630158427599, -81.30236461758612),
            new LatLng(28.596327195079738, -81.30237635225058),
            new LatLng(28.596366347215973, -81.30240384489299),
            new LatLng(28.596389308612373, -81.3024115562439),
            new LatLng(28.596425811334832, -81.30244139581919),
            new LatLng(28.596462019667946, -81.30246117711067),
            new LatLng(28.596453482744995, -81.30250476300716),
            new LatLng(28.59652472187383, -81.30249671638013)));

    ArrayList<LatLng> Q3Building3Down = new ArrayList<>(
            Arrays.asList(new LatLng(28.5962750903345, -81.3022680580616),
                    new LatLng(28.596259193966375, -81.30230627954006),
                    new LatLng(28.59623623254153, -81.30229353904724),
                    new LatLng(28.596222396808777, -81.30233108997345),
                    new LatLng(28.59623505503245, -81.30236964672804),
                    new LatLng(28.59613732173104, -81.30257818847895),
                    new LatLng(28.596130845424735, -81.30256544798613),
                    new LatLng(28.596136732975943, -81.30253728479147),
                    new LatLng(28.596144681169545, -81.30247827619314),
                    new LatLng(28.596162638197345, -81.30246218293905),
                    new LatLng(28.59616823136931, -81.30243670195341),
                    new LatLng(28.5961888377898, -81.30239512771368),
                    new LatLng(28.596174707673327, -81.30253527313471),
                    new LatLng(28.596184422128612, -81.30247358232737),
                    new LatLng(28.596126724138692, -81.30246687680483),
                    new LatLng(28.59606814298387, -81.30243133753538),
                    new LatLng(28.596181183976935, -81.30236327648163),
                    new LatLng(28.59614997996497, -81.3023491948843),
                    new LatLng(28.596130256669586, -81.3023317605257),
                    new LatLng(28.59608668878013, -81.302295550704),
                    new LatLng(28.59601721562174, -81.30226403474809)
            ));

    ArrayList<LatLng> Q4Building4Down = new ArrayList<>(Arrays.asList(
            new LatLng(28.59597247017336, -81.30237467586994),
            new LatLng(28.595901819426636, -81.30233108997345),
            new LatLng(28.595861783982404, -81.30230896174908),
            new LatLng(28.595829991118773, -81.30234617739916),
            new LatLng(28.59580938462796, -81.30238708108664),
            new LatLng(28.595848536957064, -81.30228985100985),
            new LatLng(28.59585177511897, -81.30226135253905),
            new LatLng(28.59585236387567, -81.30220972001553),
            new LatLng(28.595854718902423, -81.30219262093307),
            new LatLng(28.595951274954345, -81.30222044885159),
            new LatLng(28.59593478978102, -81.3021731749177),
            new LatLng(28.595894754349356, -81.30219664424658),
            new LatLng(28.59585206949732, -81.30216177552938),
            new LatLng(28.595936556049697, -81.30215071141718)
    ));


    ArrayList<LatLng> Q5Building5Down = new ArrayList<>(Arrays.asList(
            new LatLng(28.59593272913417, -81.30210544914007),
            new LatLng(28.595902408183058, -81.30203671753407),
            new LatLng(28.595919776495936, -81.3020658865571),
            new LatLng(28.59594891992973, -81.30205381661654),
            new LatLng(28.595985717183076, -81.30202163010836),
            new LatLng(28.59602604695795, -81.30199447274208),
            new LatLng(28.596056367873416, -81.30197033286095),
            new LatLng(28.59605842851785, -81.3019485399127),
            new LatLng(28.596037527693976, -81.30190093070269),
            new LatLng(28.596016332488063, -81.3018573448062),
            new LatLng(28.595993959766073, -81.30182281136513),
            new LatLng(28.59595922316197, -81.30184695124626),
            new LatLng(28.595950391820107, -81.30186740309),
            new LatLng(28.595930668487295, -81.3018412515521),
            new LatLng(28.595916832714288, -81.30185667425395),
            //desconocidos pero a la chingada
            new LatLng(28.595993959766073, -81.30182281136513),
            new LatLng(28.595950391820107, -81.30186740309)));


    //latitude for CenterQuadrant1: 28.59656328518375
    //longitude for CenterQUadrant1: -81.30264725536108

    //Latitude for Center Quadrant2:28.59627038029975
    //Lonngitude For CenterQUadrant2:-81.30249939858913

    //Latitude for Center Quadrant3:28.596130256669586
    //Lonngitude For CenterQUadrant3:-81.3023317605257

    //Latitude for Center Quadrant4:28.595877974788902
    //Lonngitude For CenterQUadrant4:-81.30213160067797
    public String FindQuadrantForAreaBuilding2(LatLng position) {
        double _longitude = position.longitude;
        double _latitdue = position.latitude;

        double latToCheckQ1 = 28.59656328518375;
        double longToCheckQ1 = -81.30264725536108;

        double latToCheckQ2 = 28.59627038029975;
        double longToCheckQ2 = -81.30249939858913;

        double latToCheckQ3 = 28.59600808990682;
        double longToCheckQ3 = -81.30207393318415;

        double latToCheckQ4 = 28.59577906364125;
        double longToCheckQ4 = -81.3020645454526;

        double latToCheck5 = 28.595936556049697;
        double longToCheckQ5 = -81.30215071141718;

        if (_latitdue > latToCheckQ1) {
            return "Q1";
        } else if (_latitdue > latToCheckQ2) {
            return "Q2";
        } else if (_latitdue > latToCheckQ3 && _longitude < longToCheckQ5) {
            return "Q3";

        } else if (_latitdue > latToCheckQ4 && _longitude < longToCheckQ5) {
            return "Q4";
        } else if (_longitude > longToCheckQ5) {
            return "Q5";
        } else {
            return "Not found";
        }
    }

    public ArrayList<LatLng> ChoosePointsToGrabOutsideToInsideBuilding2(String QuadrantDestination, LatLng Destination) {
        ArrayList<LatLng> points = new ArrayList<>();
        //all of these paths only consider travel from outside to inside. Not travel withing the building.
        //Within building travel will require different logic because there may be shortcuts to be taken within the building.
        boolean rightside = false;
        if (Longitued > -81.30249939858913) {
            rightside = true;
        }
        if (QuadrantDestination.equals("Q1")) {

        } else if (QuadrantDestination.equals("Q2")) {

            if (rightside) {

                points.add(Q3Building3Down.get(0));
                points.add(Q3Building3Down.get(1));

                if (Destination.longitude > -81.30256745964289) {

                    for (LatLng point : Q2Building2Down) {

                        if (point.longitude > -81.30250476300716) {
                            if (point.latitude >= Destination.latitude) {
                                points.add(point);
                            }
                        }
                    }

                } else {
                    for (LatLng point : Q2Building2Down) {
                        if (point.latitude < 28.59652472187383) {
                            points.add(point);
                        }
                    }
                }


            } else {
                for (LatLng point : Q3Building3Down) {
                    if (point.latitude < 28.596174707673327 && point.latitude > 28.596126724138692 && point.longitude < -81.30236964672804) {
                        points.add(point);
                    }
                }
                for (int i = Q3Building3Down.size() - 1; i >= 0; i--) {

                    if (Q3Building3Down.get(i).latitude >= 28.5961888377898 && Q3Building3Down.get(i).longitude >= -81.30239512771368 && Q3Building3Down.get(i).longitude < -81.3022680580616) {
                        points.add(Q3Building3Down.get(i));
                    }
                }
                if (Destination.longitude > -81.30256745964289) {
                    for (LatLng point : Q2Building2Down) {
                        if (point.latitude <= Destination.latitude) {
                            points.add(point);
                        }
                    }
                } else {
                    for (LatLng point : Q2Building2Down) {
                        if (point.latitude < 28.59652472187383) {
                            points.add(point);
                        }
                    }
                }
            }
        } else if (QuadrantDestination.equals("Q3")) {
            if (Destination.longitude < -81.30236964672804) {
                if (rightside) {
                    for (LatLng point : Q3Building3Down) {
                        if (point.latitude > 28.596215626130352) {
                            points.add(point);
                        }
                        if (point.longitude < -81.30236964672804 && point.longitude > -81.30247358232737 && point.latitude > 28.596144681169545) {
                            points.add(point);
                        }
                    }

                    for (LatLng point : Q3Building3Down) {
                        if (point.longitude > -81.30253527313471 && point.latitude < 28.596162638197345 && point.longitude < -81.30239512771368) {
                            if (point.latitude >= Destination.latitude) {
                                points.add(point);
                            }
                        }
                    }
                } else {

                    for (LatLng point : Q3Building3Down) {
                        if (point.longitude < -81.30239512771368 && point.latitude < 28.596162638197345) {
                            if (point.latitude >= Destination.latitude) {
                                points.add(point);
                            }
                        }
                    }
                }
            } else if (Destination.longitude > -81.30236964672804) {
                if (rightside) {
                    for (LatLng point : Q3Building3Down) {
                        if (point.longitude > -81.30242429673672 && point.latitude >= Destination.latitude) {
                            points.add(point);
                        }
                    }
                } else {
                    for (LatLng point : Q3Building3Down) {

                        if (point.latitude < 28.596174707673327 && point.latitude > 28.596126724138692 && point.longitude < -81.30236964672804) {
                            points.add(point);
                        }
                    }
                    for (LatLng point : Q3Building3Down) {
                        if (point.latitude <= 28.5961888377898 && point.longitude >= -81.30239512771368) {
                            if (point.latitude >= Destination.latitude) {
                                points.add(point);
                            }

                        }
                    }
                }

            }

        } else if (QuadrantDestination.equals("Q4")) {
            if (rightside) {
                //top hallway
                if (Destination.latitude > 28.595894754349356 && Destination.longitude > -81.30226403474809) {
                    for (LatLng point : Q3Building3Down) {
                        if (point.longitude >= -81.30242429673672) {
                            points.add(point);
                        }
                    }

                    for (LatLng point : Q4Building4Down) {

                        if (point.latitude > 28.595894754349356 && point.longitude > -81.30226403474809) {
                            if (point.longitude < Destination.longitude) {
                                points.add(point);
                            }


                        }
                    }
                }
                //bot hallway
                else {
                    //add start point of hallways for right side hallway
                    for (LatLng point : Q3Building3Down) {
                        if (point.latitude >= 28.5961888377898 && point.longitude >= -81.30239512771368) {
                            points.add(point);
                        }
                    }
                    for (int i = Q3Building3Down.size() - 1; i >= 0; i--) {

                        LatLng point = Q3Building3Down.get(i);
                        if (point.latitude < 28.596184422128612 && point.longitude > -81.30253527313471 && point.longitude < -81.30239512771368
                                && point.latitude > 28.596126724138692) {
                            points.add(point);
                        }
                    }
                    for (LatLng point : Q3Building3Down) {
                        if (point.latitude <= 28.596126724138692 && point.longitude < -81.30239512771368) {
                            points.add(point);
                        }
                    }
                    if (Destination.latitude > 28.595861783982404 && Destination.longitude < -81.30230896174908) {

                        for (LatLng point : Q4Building4Down) {
                            if (Destination.latitude <= point.latitude) {
                                points.add(point);
                            }
                        }
                    } else if (Destination.latitude < 28.595861783982404 && Destination.longitude < -81.30230896174908) {

                        if (Destination.latitude > 28.595829991118773) {

                            for (LatLng point : Q4Building4Down) {

                                if (point.longitude <= -81.30230896174908 && point.latitude >= 28.595829991118773) {
                                    points.add(point);
                                } else {
                                    break;
                                }
                            }

                        } else {
                            for (LatLng point : Q4Building4Down) {

                                if (point.longitude <= -81.30230896174908) {
                                    points.add(point);
                                }
                            }

                        }

                    } else {


                        for (LatLng point : Q4Building4Down) {
                            if (point.latitude > 28.595861783982404 && point.longitude <= -81.30230896174908) {
                                points.add(point);
                            }
                        }
                        for (LatLng point : Q4Building4Down) {
                            if (point.longitude >= -81.30230896174908 && point.latitude < 28.595894754349356) {
                                if (point.longitude <= Destination.longitude) {
                                    points.add(point);
                                }

                            }
                        }
                    }

                }
            } else {
                //top hallway right side
                if (Destination.latitude > 28.595894754349356 && Destination.longitude > -81.30226403474809) {
                    for (LatLng point : Q3Building3Down) {
                        if (point.latitude < 28.596174707673327 && point.latitude > 28.596126724138692 && point.longitude < -81.30239512771368) {
                            points.add(point);
                        }
                    }
                    for (LatLng point2 : Q3Building3Down) {
                        if (point2.latitude <= 28.5961888377898 && point2.longitude >= -81.30239512771368) {
                            points.add(point2);
                        }
                    }
                    for (LatLng point : Q4Building4Down) {

                        if (point.latitude > 28.595894754349356 && point.longitude > -81.30226403474809) {
                            if (point.longitude < Destination.longitude) {
                                points.add(point);
                            }


                        }
                    }
                }
                //bot hallway left side
                else {
                    for (LatLng point : Q3Building3Down) {
                        if (point.latitude < 28.596162638197345 && point.longitude < -81.30239512771368) {
                            points.add(point);
                        }
                    }
                    if (Destination.latitude > 28.595861783982404 && Destination.longitude < -81.30230896174908) {
                        for (LatLng point : Q4Building4Down) {
                            if (Destination.latitude <= point.latitude) {
                                points.add(point);
                            }
                        }
                    } else if (Destination.latitude < 28.595861783982404 && Destination.longitude < -81.30230896174908) {
                        if (Destination.latitude > 28.595829991118773) {
                            for (LatLng point : Q4Building4Down) {
                                if (point.longitude <= -81.30230896174908 && point.latitude >= 28.595829991118773) {
                                    points.add(point);
                                } else {
                                    break;
                                }
                            }
                        } else {
                            for (LatLng point : Q4Building4Down) {
                                if (point.longitude <= -81.30230896174908) {
                                    points.add(point);
                                }
                            }
                        }
                    } else {
                        for (LatLng point : Q4Building4Down) {
                            if (point.latitude > 28.595861783982404 && point.longitude <= -81.30230896174908) {
                                points.add(point);
                            }
                        }
                        for (LatLng point : Q4Building4Down) {
                            if (point.longitude >= -81.30230896174908 && point.latitude < 28.595894754349356) {
                                if (point.longitude <= Destination.longitude) {
                                    points.add(point);
                                }
                            }
                        }
                    }
                }
            }

        } else if (QuadrantDestination.equals("Q5")) {
            if (rightside) {
                for (LatLng point : Q3Building3Down) {
                    if (point.longitude >= -81.30242429673672) {
                        points.add(point);
                    }
                }
                for (LatLng point : Q4Building4Down) {
                    if (point.longitude < Destination.longitude) {
                        points.add(point);
                    }
                }
                if (Destination.latitude < 28.595902408183058 && Destination.longitude < -81.30203671753407) {
                    points.add(Q5Building5Down.get(0));
                    points.add(Q5Building5Down.get(1));
                } else if (Destination.latitude > 28.59594891992973 && Destination.longitude < -81.30205381661654) {
                    points.add(Q5Building5Down.get(0));
                    points.add(Q5Building5Down.get(1));
                    points.add(Q5Building5Down.get(2));
                } else if (Destination.latitude > 28.596037527693976 && Destination.longitude > -81.30190093070269) {
                    for (LatLng point : Q5Building5Down) {
                        if (point.latitude > 28.595902408183058) {
                            if (point.longitude < Destination.longitude) {
                                points.add(point);
                            }
                        }
                    }
                } else if (Destination.longitude < -81.30190093070269 && Destination.latitude > 28.595950391820107) {
                    for (LatLng point : Q5Building5Down) {
                        if (point.latitude < 28.596056367873416 && point.longitude < -81.30197033286095 && point.latitude > 28.595902408183058) {
                            points.add(point);
                        }
                    }
                } else if (Destination.longitude > -81.30190093070269 && Destination.latitude > 28.596016332488063) {
                    for (LatLng point : Q5Building5Down) {
                        if (point.latitude > 28.596056367873416) {
                            if (point.longitude < Destination.longitude) {
                                points.add(point);
                            }
                        }
                    }
                } else if (Destination.latitude > 28.595930668487295 && Destination.longitude > -81.3018412515521) {
                    for (LatLng point : Q5Building5Down) {
                        if (point.latitude > 28.596056367873416) {
                            if (point.longitude <= -81.30190093070269) {
                                points.add(point);
                            }
                        }
                    }
                    for (LatLng point : Q5Building5Down) {
                        if (point.longitude > -81.30190093070269 && point.latitude > 28.595930668487295) {
                            points.add(point);
                        }
                    }
                } else if (Destination.latitude < 28.595930668487295 && Destination.longitude > -81.3018412515521) {
                    for (LatLng point : Q5Building5Down) {
                        if (point.latitude > 28.596056367873416) {
                            if (point.longitude <= -81.30190093070269) {
                                points.add(point);
                            }
                        }
                    }
                    for (LatLng point : Q5Building5Down) {
                        if (point.longitude > -81.30190093070269 && point.latitude > 28.595930668487295) {
                            points.add(point);
                        }
                    }
                } else {
                    for (LatLng point : Q5Building5Down) {
                        if (point.latitude > 28.596056367873416) {
                            if (point.longitude <= -81.30190093070269) {
                                points.add(point);
                            }
                        }
                    }
                    for (LatLng point : Q5Building5Down) {
                        if (point.longitude > -81.30190093070269) {
                            points.add(point);
                        }
                    }
                }

            } else {
                for (LatLng point : Q3Building3Down) {
                    if (point.latitude < 28.596174707673327 && point.latitude > 28.596126724138692 && point.longitude < -81.30239512771368) {
                        points.add(point);
                    }
                }
                for (LatLng point2 : Q3Building3Down) {
                    if (point2.latitude <= 28.5961888377898 && point2.longitude >= -81.30239512771368) {
                        points.add(point2);
                    }
                }
                for (LatLng point : Q4Building4Down) {

                    if (point.latitude > 28.595894754349356 && point.longitude > -81.30226403474809) {
                        points.add(point);
                    }
                }
                if (Destination.latitude < 28.595902408183058 && Destination.longitude < -81.30203671753407) {

                    points.add(Q5Building5Down.get(0));
                    points.add(Q5Building5Down.get(1));
                } else if (Destination.latitude > 28.59594891992973 && Destination.longitude < -81.30205381661654) {
                    points.add(Q5Building5Down.get(0));
                    points.add(Q5Building5Down.get(1));
                    points.add(Q5Building5Down.get(2));
                } else if (Destination.latitude > 28.596037527693976 && Destination.longitude > -81.30190093070269) {
                    for (LatLng point : Q5Building5Down) {
                        if (point.latitude > 28.595902408183058) {
                            if (point.longitude < Destination.longitude)
                                points.add(point);
                        }
                    }
                } else if (Destination.longitude < -81.30190093070269 && Destination.latitude > 28.595950391820107) {
                    for (LatLng point : Q5Building5Down) {
                        if (point.latitude < 28.596056367873416 && point.longitude < -81.30197033286095 && point.latitude > 28.595902408183058) {
                            points.add(point);
                        }
                    }
                } else if (Destination.longitude > -81.30190093070269 && Destination.latitude > 28.596016332488063) {
                    for (LatLng point : Q5Building5Down) {
                        if (point.latitude > 28.596056367873416) {
                            if (point.longitude < Destination.longitude) {
                                points.add(point);
                            }
                        }
                    }
                } else if (Destination.latitude > 28.595930668487295 && Destination.longitude > -81.3018412515521) {
                    for (LatLng point : Q5Building5Down) {
                        if (point.latitude > 28.596056367873416) {
                            if (point.longitude <= -81.30190093070269) {
                                points.add(point);
                            }
                        }
                    }
                    for (LatLng point : Q5Building5Down) {
                        if (point.longitude > -81.30190093070269 && point.latitude > 28.595930668487295) {
                            points.add(point);
                        }
                    }
                } else if (Destination.latitude < 28.595930668487295 && Destination.longitude > -81.3018412515521) {
                    for (LatLng point : Q5Building5Down) {
                        if (point.latitude > 28.596056367873416) {
                            if (point.longitude <= -81.30190093070269) {
                                points.add(point);
                            }
                        }
                    }
                    for (LatLng point : Q5Building5Down) {
                        if (point.longitude > -81.30190093070269 && point.latitude > 28.595930668487295) {
                            points.add(point);
                        }
                    }
                } else {
                    for (LatLng point : Q5Building5Down) {
                        if (point.latitude > 28.596056367873416) {
                            if (point.longitude <= -81.30190093070269) {
                                points.add(point);
                            }
                        }
                    }
                    for (LatLng point : Q5Building5Down) {
                        if (point.longitude > -81.30190093070269) {
                            points.add(point);
                        }
                    }
                }
            }

        }
        return points;
    }

    // Victoria Briceno Q3AF2

    ArrayList<LatLng> Q13AF2 = new ArrayList<>(Arrays.asList(
            new LatLng(28.5956351124297,-81.30407653748989),
            new LatLng(28.5956215709968,-81.30407452583312),
            new LatLng(28.595623042891766,-81.30403161048888),
            new LatLng(28.595597137537357,-81.3040554150939),
            new LatLng(28.595572115313963,-81.30408760160208)
    ));

    ArrayList<LatLng> Q23AF2 = new ArrayList<>(Arrays.asList(
            new LatLng(28.595547093084633,-81.30412917584181),
            new LatLng(28.595536495432746,-81.30415163934231),
            new LatLng(28.595527369676127,-81.30418483167887)));

    ArrayList<LatLng> Q33AF2 = new ArrayList<>(Arrays.asList(
            new LatLng(28.595474970154942,-81.30418147891758),
            new LatLng(28.595470260084344,-81.3041278347373)));

    ArrayList<LatLng> Q43AF2 = new ArrayList<>(Arrays.asList(
            new LatLng(28.5954684938078,-81.30407754331827),
            new LatLng(28.595474675775527,-81.3040456920862),
            new LatLng(28.59547614767257,-81.30402188748121)
    ));

    public ArrayList<LatLng> ChoosePointsToGrabOutsideToInside3AF2(String QuadrantDestination, LatLng Destination) {
        ArrayList<LatLng> points = new ArrayList<>();
        //all of these paths only consider travel from outside to inside. Not travel withing the building.
        //Within building travel will require different logic because there may be shortcuts to be taken within the building.

        if (QuadrantDestination.equals("Q1")) {

            points.add(Q13AF2.get(0));
            points.add(Q13AF2.get(1));
            points.add(Q13AF2.get(2));
            points.add(Q13AF2.get(3));

        } else if (QuadrantDestination.equals("Q2")) {

            for (LatLng point : Q13AF2){
                points.add(point);
            }

            if(Destination.longitude <-81.30418483167887){
                for(LatLng point: Q23AF2){
                 points.add(point);
                }
            }else{
                Q23AF2.get(0);
                Q23AF2.get(1);
            }

        } else if (QuadrantDestination.equals("Q3")) {
            for(LatLng point: Q13AF2){
                points.add(point);
            }
            for(LatLng point: Q23AF2){
                points.add(point);
            }
            points.add(Q33AF2.get(0));
        } else if (QuadrantDestination.equals("Q4")) {
            for(LatLng point: Q13AF2){
                points.add(point);
            }
            for(LatLng point: Q23AF2){
                points.add(point);
            }
            for(LatLng point: Q33AF2){
                points.add(point);
            }
            if(Destination.longitude < -81.3040456920862){
                for(LatLng point: Q43AF2){
                    if(point.longitude < -81.3040456920862){
                        points.add(point);
                    }
                }
            }else{
                for(LatLng point: Q43AF2){
                    points.add(point);
                }
            }
        }
        return points;
    }



    ArrayList<LatLng> Q1 = new ArrayList<>(Arrays.asList(new LatLng(28.595217682239355, -81.30385525524616), new LatLng(28.595218270999617, -81.30383245646954), new LatLng(28.595219742900227, -81.30377478897572), new LatLng(28.595214738438067, -81.30369365215302), new LatLng(28.595217387859222, -81.30366180092096), new LatLng(28.595217387859222, -81.30359776318073), new LatLng(28.59519059926425, -81.30358066409826), new LatLng(28.595216504718856, -81.30352735519409)));
    ArrayList<LatLng> Q2 = new ArrayList<>(Arrays.asList(new LatLng(28.595212677777095, -81.30417109), new LatLng(28.59521562157846, -81.30409933626652), new LatLng(28.595226219262685, -81.30408190190792), new LatLng(28.595226513642796, -81.30406312644482), new LatLng(28.595225041742264, -81.30404904484749), new LatLng(28.595218270999617, -81.30403999239206), new LatLng(28.595211500256514, -81.30402792245148), new LatLng(28.595218270999617, -81.3039967417717), new LatLng(28.595219742900227, -81.30395483225584), new LatLng(28.595217976619487, -81.3039256632328)));
    ArrayList<LatLng> Q3 = new ArrayList<>(Arrays.asList(new LatLng(28.595091687469754, -81.30420729517937), new LatLng(28.595101402025108, -81.30417343229055), new LatLng(28.59507461340057, -81.30415465682745), new LatLng(28.59507284711739, -81.30410704761744), new LatLng(28.595052829239282, -81.30406580865383), new LatLng(28.595054889903384, -81.30396254360676), new LatLng(28.59499984072009, -81.30391594022512), new LatLng(28.59495273979169, -81.303915604949), new LatLng(28.594935960080836, -81.30391493439674)));
    ArrayList<LatLng> Q4 = new ArrayList<>(Arrays.asList(new LatLng(28.595049591052774, -81.30383715033533), new LatLng(28.595052240478108, -81.30379255861044), new LatLng(28.595052829239282, -81.30375735461712), new LatLng(28.595048119149773, -81.30369331687689), new LatLng(28.595049591052774, -81.30366079509258), new LatLng(28.595050179813978, -81.3035749644041), new LatLng(28.595105228970915, -81.30357999354601), new LatLng(28.595050179813978, -81.3035749644041)));

    //latitude for fish bowl: 28.595124658078248
    //longitude for fish bowl: -81.30386296659708
    public String FindQuadrantForArea(LatLng position, LatLng AreaToCheck) {
        double _longitude = position.longitude;
        double _latitdue = position.latitude;
        double longToCheck = AreaToCheck.longitude;
        double latToCheck = AreaToCheck.latitude;
        if (_longitude < longToCheck && _latitdue < latToCheck) {
            return "Q3";
        } else if (_longitude < longToCheck && _latitdue > latToCheck) {
            return "Q2";
        } else if (_longitude > longToCheck && _latitdue > latToCheck) {
            return "Q1";
        } else if (_longitude > longToCheck && _latitdue < latToCheck) {
            return "Q4";
        } else {
            return "NotFound";
        }
    }
    public ArrayList<LatLng> ChoosePointsToGrabOutsideToInsideFishBowl(String QuadrantDestination, LatLng Destination) {
        ArrayList<LatLng> points = new ArrayList<>();
        //all of these paths only consider travel from outside to inside. Not travel withing the building.
        //Within building travel will require different logic because there may be shortcuts to be taken within the building.

        if (QuadrantDestination.equals("Q1")) {
            //grab first two points of quadrant3
            points.add(Q3.get(0));
            points.add(Q3.get(1));
            //use all points from quadrant2
            for (LatLng point : Q2) {
                points.add(point);
            }
            // grab all points from quadrant1 that has a long less than the destiantion point whichever that may be.
            for (LatLng point : Q1) {
                if (point.longitude < Destination.longitude) {
                    points.add(point);
                } else {
                    break;
                }
            }
        } else if (QuadrantDestination.equals("Q2")) {
            //-81.30417109
            //grab first two points of quadrant3
            points.add(Q3.get(0));
            points.add(Q3.get(1));
            //then grab all points from quadrant2 till you get to destination(lat is less than destination)
            for (LatLng point : Q2) {
                if (point.longitude <= Destination.longitude) {
                    points.add(point);
                } else {
                    break;
                }
            }
        } else if (QuadrantDestination.equals("Q3")) {
            //grab all points from qudrant 3 until you get to destination. quadrant 3 is by the entrance of the building.
            for (LatLng point : Q3) {
                if (point.longitude <= Destination.longitude) {
                    points.add(point);
                } else {
                    break;
                }
            }
        } else if (QuadrantDestination.equals("Q4")) {
            //grab first part of quadrant 3's points.(quadrant three will have points that lead down into the next building)
            for (LatLng point : Q3) {
                if (point.latitude > 28.59499984072009) {
                    points.add(point);
                } else {
                    break;
                }
            }
            //then grab all quadrant 4 points until you reach the destination.
            for (LatLng point : Q4) {
                if (Destination.latitude > 28.595050179813978) {
                    if (point.longitude < -81.3035411015153) {
                        points.add(point);
                    }
                    if (point.latitude > 28.595050179813978) {
                        points.add(point);
                    }
                } else {
                    if (point.longitude <= Destination.longitude) {
                        points.add(point);
                    } else {
                        break;
                    }
                }
            }
        }
        return points;
    }
    ArrayList<LatLng> Q13BC = new ArrayList<>(Arrays.asList(new LatLng(28.594776699884957,-81.30389012396336),new LatLng(28.594774639215387,-81.30391828715801), new LatLng(28.594882382740593,-81.30391459912062)));
    ArrayList<LatLng> Q23BC = new ArrayList<>(Arrays.asList(new LatLng(28.594776699884957,-81.30419488996267),new LatLng(28.594879144548813,-81.30419589579105)));
    ArrayList<LatLng> Q33BC = new ArrayList<>(Arrays.asList(new LatLng(28.594752560610537,-81.30419522523879),new LatLng(28.594753149373393,-81.30411174148321),new LatLng(28.594750499940506,-81.30407955497503),new LatLng(28.594753149373393,-81.30397494882345),new LatLng(28.594753149373393,-81.30393370985985)));
    ArrayList<LatLng> Q43BC = new ArrayList<>(Arrays.asList(new LatLng(28.594749911177647,-81.3039068877697),new LatLng(28.59475167746625,-81.30386833101511), new LatLng(28.594752560610537,-81.30376003682613),new LatLng(28.594752266229108,-81.30373019725086)));
    public ArrayList<LatLng> ChoosePointsToGrabOutsideToInside3BC(String QuadrantDestination, LatLng Destination){
        ArrayList<LatLng> points = new ArrayList<>();
        if(QuadrantDestination.equals("Q1")){
            for(LatLng point: Q33BC)
            {
                points.add(point);
            }
            points.add(Q43BC.get(0));
            points.add(Q13BC.get(0));
        } else if (QuadrantDestination.equals("Q2")) {
            if(Destination.longitude < -81.30404770374298)
            {
                points.add(Q33BC.get(0));
                points.add(Q23BC.get(0));
            }
            else{
                for(LatLng point: Q33BC)
                {
                    points.add(point);
                }
                points.add(Q43BC.get(0));
                points.add(Q13BC.get(1));
            }
        } else if (QuadrantDestination.equals("Q3")) {
            for(LatLng point: Q33BC)
            {
                if(point.longitude<Destination.longitude)
                {
                    points.add(point);
                }
            }
        } else if (QuadrantDestination.equals("Q4")) {
            for(LatLng point:Q33BC)
            {
                points.add(point);
            }
            for(LatLng point:Q43BC){
                if(point.longitude < Destination.longitude){
                    points.add(point);
                }
            }
        }
        return points;
    }
    ArrayList<LatLng> Q13C = new ArrayList<>(Arrays.asList(new LatLng(28.594494976544492,-81.30402322858573),new LatLng(28.594497920365935,-81.30400780588388),new LatLng(28.59445759000472,-81.30394041538239),new LatLng(28.594462594502932,-81.30387872457504), new LatLng(28.594596243955476,-81.30387905985117)));
    ArrayList<LatLng> Q23C = new ArrayList<>(Arrays.asList(new LatLng(28.594499686658786,-81.30418047308923),new LatLng(28.5944929158694,-81.3040966540575)));
    ArrayList<LatLng> Q33C = new ArrayList<>(Arrays.asList(new LatLng(28.594446992243014,-81.30417779088022),new LatLng(28.594397241625096,-81.30418147891758),new LatLng(28.594398419154782,-81.3041090592742),new LatLng(28.594400185449288,-81.304095312953)));
    ArrayList<LatLng> Q43C = new ArrayList<>(Arrays.asList(new LatLng(28.594398419154782,-81.30402356386185),new LatLng(28.594401362978946,-81.30394209176302),new LatLng(28.594372807881026,-81.30394008010626), new LatLng(28.594349257278985,-81.303939409554),new LatLng(28.594318347105794,-81.30394041538239), new LatLng(28.594430212451424,-81.30394142121078), new LatLng(28.59442932930444,-81.30390387028456)));
    public ArrayList<LatLng> ChoosePointsToGrabOutsideToInside3C(String QuadrantDestination, LatLng Destination) {
        ArrayList<LatLng> points = new ArrayList<>();
        //all of these paths only consider travel from outside to inside. Not travel withing the building.
        //Within building travel will require different logic because there may be shortcuts to be taken within the building.

        if (QuadrantDestination.equals("Q1")) {
            points.add(Q33C.get(0));
            for(LatLng point: Q23C){
                points.add(point);
            }
            for(LatLng point: Q13C){
                if(point.longitude < Destination .longitude){
                    points.add(point);
                }
            }
        } else if (QuadrantDestination.equals("Q2")) {
            points.add(Q33C.get(0));
            for(LatLng point: Q23C){
                if(point.longitude< Destination.longitude){
                    points.add(point);
                }
            }
        } else if (QuadrantDestination.equals("Q3")) {
            for(LatLng point: Q33C){
                if(point.longitude < Destination.longitude){
                    points.add(point);
                }
            }
        } else if (QuadrantDestination.equals("Q4")) {
            for(LatLng point:Q33C){
                points.add(point);
            }
            if(Destination.longitude<-81.30394209176302){
                for(LatLng point: Q43C){
                    if(point.longitude < Destination.longitude)
                    {
                        points.add(point);
                    }
                }
            }else{
                if(Destination.latitude > 28.594401362978946){
                    for(LatLng point: Q43C){
                        if(point.latitude >= 28.594401362978946){
                            points.add(point);
                        }
                    }
                }else{
                    for(LatLng point: Q43C){
                        if(point.latitude <= 28.594401362978946 && point.latitude >= Destination.latitude){
                            points.add(point);
                        }
                    }
                }
            }
        }
        return points;
    }

    ArrayList<LatLng> Q13D = new ArrayList<>(Arrays.asList(new LatLng(28.594210897385395,-81.30406580865383), new LatLng(28.594210014236552,-81.30402524024248)));
    ArrayList<LatLng> Q23D = new ArrayList<>(Arrays.asList(new LatLng(28.594175277042876,-81.30418583750725),new LatLng(28.594208542321798,-81.30418147891758),new LatLng(28.59421030861951,-81.30414325743914),new LatLng(28.594210897385395,-81.30410436540842)));
    ArrayList<LatLng> Q33D = new ArrayList<>(Arrays.asList(new LatLng(28.594117577950065,-81.30418483167887),new LatLng(28.59411698918364,-81.30414593964815),new LatLng(28.594118166716456,-81.30412112921476),new LatLng(28.59411698918364,-81.3041016831994),new LatLng(28.594045748422907,-81.3041865080595),new LatLng(28.59404663157312,-81.30416471511126), new LatLng(28.59404663157312,-81.3041466102004),new LatLng(28.59404722033995,-81.30411811172962), new LatLng(28.59404780910675,-81.30410101264715)));
    ArrayList<LatLng> Q43D = new ArrayList<>(Arrays.asList(new LatLng(28.594119638632485,-81.30408190190792),new LatLng(28.594119049866087,-81.30405575037001),new LatLng(28.594119049866087,-81.30404267460108),new LatLng(28.594104330704887,-81.304029263556),new LatLng(28.59404722033995,-81.30407318472862),new LatLng(28.59404722033995,-81.3040567561984),new LatLng(28.59404780910675,-81.30402758717537),new LatLng(28.59406517772608,-81.30402825772762)));
    public ArrayList<LatLng> ChoosePointToGrabOutsideToInside3D(String QuadrantDestination, LatLng Destination){
        ArrayList<LatLng> points = new ArrayList<>();
        if(QuadrantDestination.equals("Q1")){
            if(Destination.latitude > 28.59416350172039){
                for(LatLng point: Q23D){
                    points.add(point);
                }
                for(LatLng point: Q13D) {
                    if (point.longitude < Destination.longitude)
                    {
                        points.add(point);
                    }
                }
            }else{
                points.add(Q23D.get(0));
                for(LatLng point: Q33D){
                    if(point.latitude > 28.594082546342538)
                    {
                        points.add(point);
                    }
                }
                for(LatLng point: Q43D){
                    if(point.latitude >28.594104330704887 && point.longitude< Destination.longitude){
                        points.add(point);
                    }
                }
            }
        } else if (QuadrantDestination.equals("Q2")) {
            if(Destination.latitude >28.59416350172039)
            {
                for(LatLng point: Q23D){
                    if(point.longitude<Destination.longitude){
                        points.add(point);
                    }
                }
            }else{
                points.add(Q23D.get(0));
                for(LatLng point:Q33D){
                    if(point.latitude>28.594082546342538 && point.longitude < Destination.longitude){
                        points.add(point);
                    }
                }
            }
        } else if (QuadrantDestination.equals("Q3")) {
            if(Destination.latitude>28.594082546342538){
                points.add(Q23D.get(0));
                for(LatLng point:Q33D){
                    if(point.latitude>28.594082546342538 && point.longitude < Destination.longitude){
                        points.add(point);
                    }
                }
            }else{
                points.add(Q23D.get(0));
                points.add(Q33D.get(0));
                for(LatLng point:Q33D){
                    if(point.latitude<28.594082546342538 && point.longitude < Destination.longitude){
                        points.add(point);
                    }
                }
            }
        } else if (QuadrantDestination.equals("Q4")) {
            if(Destination.latitude >28.594082546342538){
                points.add(Q23D.get(0));
                for(LatLng point:Q33D){
                    if(point.latitude>28.594082546342538){
                        points.add(point);
                    }
                }
                for(LatLng point:Q43D){
                    if(point.latitude> 28.594082546342538 && point.longitude < Destination.longitude){
                        points.add(point);
                    }
                }
            }else{
                if(Destination.latitude > 28.594054285541397 && Destination.longitude >-81.30402222275734){
                    points.add(Q23D.get(0));
                    for(LatLng point:Q33D){
                        if(point.latitude>28.594082546342538){
                            points.add(point);
                        }
                    }
                    for(LatLng point: Q43D){
                        if(point.latitude > 28.594054285541397){
                            points.add(point);
                        }
                    }
                }
                else{
                    points.add(Q23D.get(0));
                    points.add(Q33D.get(0));
                    for(LatLng point:Q33D){
                        if(point.latitude<28.594082546342538){
                            points.add(point);
                        }
                    }
                    for(LatLng point:Q43D){
                        if(point.latitude < 28.594054285541397 && point.longitude < Destination.longitude){
                            points.add(point);
                        }
                    }
                }
            }
        }
        return points;
    }


    ArrayList<LatLng> Q13E = new ArrayList<>(Arrays.asList(new LatLng(28.59399982460113,-81.30436655133964),new LatLng(28.593970975010514,-81.30431659519672)));
    ArrayList<LatLng> Q23E = new ArrayList<>(Arrays.asList(new LatLng(28.593997469532802,-81.30447551608086),new LatLng(28.593970975010514,-81.30447551608086),new LatLng(28.59394359733048,-81.30447585135698),new LatLng(28.593928289377235,-81.30446009337902)));
    ArrayList<LatLng> Q33E = new ArrayList<>(Arrays.asList(new LatLng(28.593911215119075,-81.30446009337902),new LatLng(28.593864702470704,-81.30446009337902),new LatLng(28.593858520408276,-81.30438331514597)));
    ArrayList<LatLng> Q43E = new ArrayList<>(Arrays.asList(new LatLng(28.593884131807343,-81.30432933568954),new LatLng(28.59386411370286,-81.30432933568954),new LatLng(28.593847333818246,-81.30431391298771),new LatLng(28.593835852843018,-81.30432967096567),new LatLng(28.593812596504723,-81.30433201789856),new LatLng(28.593852338345513,-81.30421433597803),new LatLng(28.593814951577173,-81.30421500653028)));
    public ArrayList<LatLng> ChoosePointToGrabOutsideToInside3E(String QuadrantDestination,LatLng Destination){
        ArrayList<LatLng> points = new ArrayList<>();
        if(QuadrantDestination.equals("Q1"))
        {

        } else if (QuadrantDestination.equals("Q2")) {
            points.add(Q13E.get(0));
            for(LatLng point: Q23E){
                if(point.latitude > Destination.latitude){
                    points.add(point);
                }
            }
        } else if (QuadrantDestination.equals("Q3")) {
            points.add(Q13E.get(0));
            for(LatLng point:Q23E){
                points.add(point);
            }

            if(Destination.latitude < 28.593847922586168){
                for(LatLng point:Q33E){
                    points.add(point);
                }
            }else{
                for(LatLng point:Q33E){
                    if(point.latitude > Destination.latitude && point.latitude >28.593858520408276){
                        points.add(point);
                    }
                }
            }
        } else if (QuadrantDestination.equals("Q4")) {
            if(Destination.longitude < -81.30431391298771)
            {
                points.add(Q13E.get(0));
                points.add(Q13E.get(1));
                for(LatLng point: Q43E){
                    if(point.longitude < -81.30431391298771 && point.latitude > Destination.latitude){
                        points.add(point);
                    }
                }
            }else{
                points.add(Q13E.get(0));
                points.add(Q13E.get(1));
                for(LatLng point: Q43E){
                    if(point.longitude >=-81.30431391298771){
                        points.add(point);
                    }
                }
            }
        }

        return points;
    }
    ArrayList<LatLng> Q13A = new ArrayList<>(Arrays.asList(new LatLng(28.595617744069802,-81.30403161048888),new LatLng(28.595614505900667,-81.30407586693764),new LatLng(28.595628341713468,-81.30408894270658),new LatLng(28.595562400802088,-81.30402825772762)));
    ArrayList<LatLng> Q23A = new ArrayList<>(Arrays.asList(new LatLng(28.59561126773146,-81.30416605621576),new LatLng(28.595624220407736,-81.30416605621576)));
    ArrayList<LatLng> Q33A = new ArrayList<>(Arrays.asList(new LatLng(28.595419332432524,-81.3042401522398)));
    ArrayList<LatLng> Q43A = new ArrayList<>(Arrays.asList(new LatLng(28.595422276228085,-81.30402356386185),new LatLng(28.595457013009668,28.595457013009668)));
    public ArrayList<LatLng> ChoosePointsToGrabOutsideToInside3A(String QuadrantDestination, LatLng Destination) {
        ArrayList<LatLng> points = new ArrayList<>();
        //all of these paths only consider travel from outside to inside. Not travel withing the building.
        //Within building travel will require different logic because there may be shortcuts to be taken within the building.
        boolean topEntrance = false;
        if(Latitude > 28.59563746746134 || Longitued > -81.30403395742178)
        {
            topEntrance = true;
        }
        if (QuadrantDestination.equals("Q1")) {
                if(topEntrance) {
                    if (Destination.latitude < 28.595605674529853 && Destination.longitude < -81.30404636263847) {
                        for(LatLng point: Q13A){
                            if(point.longitude > -81.30404636263847){
                                points.add(point);
                            }
                        }
                    }
                }else{
                    if (Destination.latitude < 28.595605674529853 && Destination.longitude < -81.30404636263847) {
                        points.add(Q33A.get(0));
                        points.add(Q43A.get(0));
                        points.add(Q43A.get(1));
                        for(LatLng point: Q13A){
                            if(point.latitude < 28.595605674529853){
                                points.add(point);
                            }
                        }
                    }
                }
        } else if (QuadrantDestination.equals("Q2")) {
            if(topEntrance)
            {
                if (Destination.latitude < 28.595605674529853 && Destination.longitude < -81.30404636263847) {
                    for(LatLng point: Q13A){
                        if(point.longitude > -81.30404636263847){
                            points.add(point);
                        }
                    }
                }
            }else{
                if (Destination.latitude < 28.595605674529853 && Destination.longitude < -81.30404636263847) {
                    points.add(Q33A.get(0));
                    points.add(Q43A.get(0));
                    points.add(Q43A.get(1));
                    for(LatLng point: Q13A){
                        if(point.latitude < 28.595605674529853){
                            points.add(point);
                        }
                    }
                }
            }
        } else if (QuadrantDestination.equals("Q3")) {
            if(topEntrance){
                if (Destination.latitude < 28.595605674529853 && Destination.longitude < -81.30404636263847) {
                    for(LatLng point: Q13A){
                        if(point.longitude > -81.30404636263847){
                            points.add(point);
                        }
                    }
                }
            }else{
                if (Destination.latitude < 28.595605674529853 && Destination.longitude < -81.30404636263847) {
                    points.add(Q33A.get(0));
                }
            }
        } else if (QuadrantDestination.equals("Q4")) {
            if(topEntrance){
                if (Destination.latitude < 28.595605674529853 && Destination.longitude < -81.30404636263847) {
                    for(LatLng point: Q13A){
                        if(point.longitude > -81.30404636263847){
                            points.add(point);
                        }
                    }
                }else{
                    for(LatLng point: Q13A){
                        if(point.longitude > -81.30404636263847){
                            points.add(point);
                        }
                    }
                    for(LatLng point: Q43A){
                        if(point.latitude>= Destination.latitude){
                            points.add(point);
                        }
                    }
                }
            }else{
                if (Destination.latitude < 28.595605674529853 && Destination.longitude < -81.30404636263847) {
                    points.add(Q33A.get(0));
                    points.add(Q43A.get(0));
                    points.add(Q43A.get(1));
                    for(LatLng point: Q13A){
                        if(point.latitude < 28.595605674529853){
                            points.add(point);
                        }
                    }
                }
                else{
                    points.add(Q33A.get(0));
                    points.add(Q43A.get(0));
                    points.add(Q43A.get(1));
                }
            }
        }
        return points;
    }
    //SearchType is whether youre looking for the area youre traveling to or if you want the spot by the front door. 1 is front door 2 is just the area return.
    public LatLng FindMarkerAreaForTravel(Marker marker, int SearchType) {
        LatLng returnArea = null;
        if (ThreeAMarkers.contains(marker)) {
            if(SearchType == 1)
            {
                if(Latitude > 28.59563746746134 || Longitued > -81.30403395742178)
                {
                    return new LatLng(28.59563746746134,-81.30403395742178);
                }
                else {
                    return new LatLng(28.595412856081968,-81.30432799458504);
                }
            }else {
                return new LatLng(28.595520598952945,-81.30410872399807);
            }
        } else if (ThreeAMarkersF2.contains(marker)) {
            if(SearchType == 1 ){
                if(Latitude < 28.595632463019054){
                    return new LatLng(28.595416977396,-81.30433067679405);
                }else{
                    return new LatLng(28.59563364053492,-81.30403395742178);
                }
            }else{
                return new LatLng(28.5955282528139,-81.30411978811026);
            }
        } else if (ThreeBMarkers.contains(marker)) {
            if (SearchType == 1) {
                returnArea = new LatLng(28.59504105401512, -81.30434174090624);
            } else {
                returnArea = new LatLng(-81.30386296659708, 28.595124658078248);
            }
        } else if (ThreeBCMarkers.contains(marker)) {
            if(SearchType == 1){
                return new LatLng(28.59475167746625,-81.30431525409222);
            }else{
                return new LatLng(28.59476433586704,-81.3039256632328);
            }
        }
        if (ThreeCMarkers.contains(marker)) {
            if(SearchType == 1)
            {
                returnArea = new LatLng(28.594441398979445,-81.30426932126284);
            }else{
                returnArea = new LatLng(28.594457001240205,-81.30404971539974);
            }
        } else if (ThreeDMarkers.contains(marker)) {
            if(SearchType == 1){
                return new LatLng(28.59417262759543,-81.30432464182375);
            } else{
                return new LatLng(28.59412434876362,-81.3040879368782);
            }
        } else if (ThreeEMarkers.contains(marker)) {
            if(SearchType == 1){
                return new LatLng(28.594129353277687,-81.3043739274144);
            }else{
                return new LatLng(28.59392387362109,-81.30436789244413);
            }
        } else if (ThreeFMarkers.contains(marker)) {
//            return "3F";
        } else if (FourAMarkers.contains(marker)) {
//            return "4A";
        } else if (BuildingOne.contains(marker)) {
            if (SearchType == 1) {
                String area = FindQuadrantForArea(marker.getPosition(), new LatLng(28.596453482744995, -81.30128234624863));
                if (area == "Q1" || area == "Q2") {
                    return new LatLng(28.596690161317344, -81.30131889134645);
                } else if (area == "Q3") {
                    return new LatLng(28.596429343855675, -81.30178961902857);
                } else if (area == "Q4") {
                    return new LatLng(28.596022808801415, -81.30066644400358);
                }
            } else {
                return new LatLng(28.596453482744995, -81.3012823462486);
            }
        } else if (BuildingTwo.contains(marker)) {
            if (marker.getPosition().latitude > 28.59656328518375) {
                if (28.596764638419543 < marker.getPosition().latitude) {
                    return new LatLng(28.596977766278073, -81.30269687622787);
                } else if (-81.30258791148663 > marker.getPosition().longitude) {

                    return new LatLng(28.59678877723184, -81.30247157067062);

                } else {
                    return marker.getPosition();
                }

            } else {
                if (Longitued < -81.30249939858913) {
                    return new LatLng(28.596084922513946, -81.30266435444355);
                } else{
                    return new LatLng(28.596334260127957, -81.302160769701);
                }
            }
        } else if (BuildingTwoF2.contains(marker)) {
            boolean rightside = false;
            if (Longitued > -81.30276795476675) {
                rightside = true;
            }
            if(marker.getPosition().latitude > 28.59658124214005){
                return new LatLng(28.5967084126279,-81.30242194980383);
            }
            else {
                if(rightside){
                    return new LatLng(28.596311298719524,-81.30214400589466);
                }else{
                    return new LatLng(28.596102290796672,-81.3026137277484);
                }
            }
        }
        return returnArea;
    }

    public void HideAllOtherMarkers(String typeNotToHide) {
        if (!typeNotToHide.equals("1")) {
            for (int i = 0; i < BuildingOne.size(); i++) {
                BuildingOne.get(i).setVisible(false);
            }
        }
        if (!typeNotToHide.equals("2")) {
            for (int i = 0; i < BuildingTwo.size(); i++) {
                BuildingTwo.get(i).setVisible(false);
            }
        }
        if (!typeNotToHide.equals("3A")) {
            for (int i = 0; i < ThreeAMarkers.size(); i++) {
                ThreeAMarkers.get(i).setVisible(false);
            }
        }
        if (!typeNotToHide.equals("3B")) {
            for (int i = 0; i < ThreeBMarkers.size(); i++) {
                ThreeBMarkers.get(i).setVisible(false);
            }
        }
        if(!typeNotToHide.equals("3BC")){
            for(int i = 0; i < ThreeBCMarkers.size(); i++){
                ThreeBCMarkers.get(i).setVisible(false);
            }
        }
        if (!typeNotToHide.equals("3C")) {
            for (int i = 0; i < ThreeCMarkers.size(); i++) {
                ThreeCMarkers.get(i).setVisible(false);
            }
        }
        if (!typeNotToHide.equals("3D")) {
            for (int i = 0; i < ThreeDMarkers.size(); i++) {
                ThreeDMarkers.get(i).setVisible(false);
            }
        }
        if (!typeNotToHide.equals("3E")) {
            for (int i = 0; i < ThreeEMarkers.size(); i++) {
                ThreeEMarkers.get(i).setVisible(false);
            }
        }
        if (!typeNotToHide.equals("3F")) {
            for (int i = 0; i < ThreeFMarkers.size(); i++) {
                ThreeFMarkers.get(i).setVisible(false);
            }
        }
    }

//    public boolean CheckResultLoadType(String Result) {
//        boolean wasFound = false;
//        if (!Result.equals("b3u") && !Result.equals("b3d") && !Result.equals("b4u") && !Result.equals("b4d") && !Result.equals("b1") && !Result.equals("b2")) {
//            for (int i = 0; i < resultsList.size(); i++) {
//                if (resultsList.get(i).equals(Result)) {
//                    wasFound = true;
//                    break;
//                }
//            }
//            if (!wasFound) {
//                resultsList.add(Result);
//            }
//        } else {
//            switch (Result) {
//                case "b3u":
//
//                    if (B3U.size() >= 3 && resultsList.contains("3A") && resultsList.contains("FishBowl") && resultsList.contains("3BConnected")) {
//                        if (!resultsList.contains("b3u")) {
//
//                            resultsList.add("b3u");
//                        }
//                        wasFound = true;
//                    }
//                    break;
//                case "b3d":
//
//                    if (B3D.size() >= 4 && resultsList.contains("3C") && resultsList.contains("3D") && resultsList.contains("3E") && resultsList.contains("3F")) {
//                        if (!resultsList.contains("b3d")) {
//                            resultsList.add("b3d");
//                        }
//                        wasFound = true;
//                    }
//                    break;
//                case "b4u":
//                    if (B4U.size() == 4 && resultsList.contains("4B")&& resultsList.contains("4AWD2")&& resultsList.contains("4AFC")&& resultsList.contains("4A")) {
//                        if (!resultsList.contains("b4u")) {
//                            resultsList.add("b4u");
//                        }
//                        wasFound = true;
//                    }
//                    break;
//                case "b4d":
//                    if (B4D.size() >= 3 && resultsList.contains("4D") && resultsList.contains("4C") && resultsList.contains("4E")) {
//                        if (!resultsList.contains("b4d")) {
//                            resultsList.add("b4d");
//                        }
//                        wasFound = true;
//                    }
//                    break;
//                case "b1":
//                    if (B1.size() == 2) {
//                        if (!resultsList.contains("b1")) {
//                            resultsList.add("b1");
//                        }
//                        wasFound = true;
//                    }
//                    break;
//                case "b2":
//                    if (B2.size() == 2) {
//                        if (!resultsList.contains("b2")) {
//                            resultsList.add("b2");
//                        }
//                        wasFound = true;
//                    }
//            }
//        }
//        return wasFound;
//    }

    public void doTheClick(ArrayList<Marker> ListToClick) {
        if (isNOTfUCKED) {
            Marker m = FindTheMarker(markerTitle2);
            if(m != null) {
                markerFragment.MTouch.ManualTouch(m);
                btnFavoritesAdd.setVisibility(View.GONE);
                bntFavoritesRemove.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraLoad));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("floorPicked", floorPicked);
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState) {
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
        editor.putFloat("longitude", (float) longitude);
        editor.putFloat("latitude", (float) latitude);
        editor.putInt("floorPicked", floorPicked);
        editor.commit();
    }

    public void navloc() {
        try {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            if (mLocationPermissionsGranted) {
                @SuppressLint("MissingPermission") Task location = fusedLocationClient.getLastLocation();
                location.addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {
                            getDeviceLocation();
                            if (FollowUser) {
                                //moveCamera(new LatLng(Latitude, Longitued), 19f);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                            }
                            if (location.getLatitude() != Latitude || location.getLongitude() != Longitued) {
                                if (wasMarkerClicked) {
                                    RemoveAllLines();
                                    getDirectionPoly(markerFragment.MTouch.marker2);
                                }
                                // if (FollowUser) {
                                Latitude = location.getLatitude();
                                Longitued = location.getLongitude();
                                //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                                // }
                            }
                        } else {
                        }
                    }
                });
            }
        } catch (SecurityException e) {

        }
    }

    @Override
    public void onClick(View view) {
        //Switch based on what button was clicked
        switch (view.getId()) {
            case R.id.userMaps:
                Intent intent = new Intent(this, Settings.class);
                startActivity(intent);
                break;
            case R.id.NavLock:
                if (!FollowUser) {
                    FollowUser = true;
                    Snackbar snack = Snackbar.make(findViewById(R.id.map), "NavLoc ON", Snackbar.LENGTH_SHORT);
                    snack.show();
                } else {
                    FollowUser = false;
                    Snackbar snack = Snackbar.make(findViewById(R.id.map), "NavLoc OFF", Snackbar.LENGTH_SHORT);
                    snack.show();
                }
                break;
            case R.id.navgo:

                if(!isTraveling) {
                    getDirectionPoly(markerFragment.MTouch.marker2);
                    markerFragment.MTouch.marker2.setVisible(true);
                    isTraveling = true;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Latitude, Longitued), 20f));
                    checkDistPoint = new LatLng(Latitude, Longitued);
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
                    if (!(CheckMarkerType(finalDestinationMarker)) && !stringcurlocation.isEmpty()) {
                        //remove all lines
                        RemoveAllLines();
                        //Logic for deciding the order to place the strings in
                        int start = Integer.parseInt(stringcurlocation.replaceAll("[^0-9]", ""));
                        int end = Integer.parseInt(stringfinaldestination.replaceAll("[^0-9]", ""));
                        if (start > end) {
                            RooomtoRoom += curlocation.getText().toString();
                            RooomtoRoom += finaldestination.getText().toString();
                        } else {
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
                    FollowUser = true;
                    //"Select" markers to be used if needed
                    //Removes slideup
                    slideupview.setVisibility(View.GONE);
                    slidepup = false;
                    //Allows NavDone button to appear
                    NavDone.setVisibility(View.VISIBLE);
                    //Brings back searchbar (may be depricated, will have to test)
                    Search.setVisibility(View.VISIBLE);
                    //Removes keyboard when Go is hit
                    InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(slideupview.getWindowToken(), 0);
                } else {
                    snack = Snackbar.make(findViewById(R.id.map), "Already routing, please cancel previous route", Snackbar.LENGTH_SHORT);
                    snack.show();
//                    Toast toast1 = Toast.makeText(this,"Already routing, please cancel previous route", Toast.LENGTH_LONG);
////                    toast1.getView().setBackgroundResource(R.drawable.round_linearlayout);
//                    toast1.show();



                }
                break;

            case R.id.NavDone:
                slideupview.setVisibility(View.GONE);
                markerFragment.MTouch.slideup = false;
                if (markerFragment.MTouch.FilterMarker != null) {
                    markerFragment.MTouch.FilterMarker.setEnabled(true);
                }
                Filter.setEnabled(true);
                markerFragment.MTouch.wasMarkerClicked = false;
                isTraveling = false;
                FollowUser = false;
                //Remove all lines
                RemoveAllLines();
                if(createdMarkers != null) {
                    for (Marker m : createdMarkers) {
                        m.setVisible(true);
                    }
                }
//                checkIfMarkerNeedVisible();
                onMapClick(new LatLng(28.595085, -81.308305));

                showMarkerInArea(prevResult);
                //Brings Searchbar back
                Search.setVisibility(View.VISIBLE);
                //Removes navdone button
                NavDone.setVisibility(View.GONE);
                break;
            case R.id.btnRemoveFavorites:
                Favorites.removeFromFavorite(MapsActivity.this, markerFragment.MTouch.marker2);
                favoritedMarkers.remove(markerFragment.MTouch.marker2);
                snack = Snackbar.make(findViewById(R.id.map), "Removed From Favorites", Snackbar.LENGTH_SHORT);
                snack.show();
                bntFavoritesRemove.setVisibility(View.GONE);
                btnFavoritesAdd.setVisibility(View.VISIBLE);
                break;
            case R.id.RemoveSpot:
                wasRemoveHit = true;


                for (int i = 0; i < favoritedMarkers.size()-1; i++) {
                    if (favoritedMarkers.get(i).getTitle().equals(markerFragment.MTouch.createdMarker.getTitle())) {
                        Favorites.removeFromFavorite(MapsActivity.this, markerFragment.MTouch.createdMarker);
                        favoritedMarkers.remove(i);
                    }
                }
                markerFragment.MTouch.mMarkers.remove(markerFragment.MTouch.marker2);
                markerFragment.MTouch.AM.remove(markerFragment.MTouch.marker2);
                for (int i = 0; i < createdMarkers.size(); i++) {
                    if (createdMarkers.get(i).getTitle().equals(markerFragment.MTouch.createdMarker.getTitle())) {
                        createdMarkers.get(i).remove();
                        CustomMarker.removeFromCustomMarkers(MapsActivity.this, markerFragment.MTouch.createdMarker);
                        createdMarkers.get(i).setVisible(false);
                        CustomMarker.removeFromCustomMarkers(MapsActivity.this, markerFragment.MTouch.createdMarker);
                        createdMarkers.remove(i);
                        if(i != markerFragment.MTouch.CM.size()) {
                            if (markerFragment.MTouch.CM.get(i).getTitle().equals(markerFragment.MTouch.createdMarker.getTitle())) {
                                markerFragment.MTouch.CM.get(i).remove();
                                markerFragment.MTouch.CM.remove(i);
                            }
                        }
                        markerFragment.MTouch.createdMarker.remove();
                        markerFragment.MTouch.createdMarker = null;
                        markerFragment.MTouch.marker2.remove();
                        markerFragment.MTouch.marker2 = null;
                        break;
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
                if (createdMarkers != null) {
                    for (Marker marker1 : createdMarkers) {
                        if (marker1.getTitle().equals(name)) {
                            wasItCreatedAlready = true;
                        }
                    }
                    for (Marker marker : MarkersList) {
                        if (marker.getTitle().equals(name)) {
                            nameExistAlready = true;
                        }
                    }
                }
                Marker newMarker = null;
                if (wasItCreatedAlready || nameExistAlready) {
                    snack = Snackbar.make(findViewById(R.id.map), "Marker Already Exists", Snackbar.LENGTH_SHORT);
                    snack.show();
                    saveSpotLayout.setVisibility(View.GONE);
                } else {
                    if (!wasRemoveHit) {
                        if (!name.isEmpty()) {
                            newMarker = mMap.addMarker(new MarkerOptions().position(LongClickPoint).title(name));
//                            newMarker.showInfoWindow();
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(LongClickPoint));
                            createdMarkers.add(newMarker);
                            markerFragment.MTouch.mMarkers.add(newMarker);
                            markerFragment.MTouch.CM.add(newMarker);
                        } else {
                            name = "CustomMarkerNumber0" + createdMarkers.size();
                            newMarker = mMap.addMarker(new MarkerOptions().position(LongClickPoint).title(name));
//                            newMarker.showInfoWindow();
                            createdMarkers.add(newMarker);
                            markerFragment.MTouch.mMarkers.add(newMarker);
                            markerFragment.MTouch.CM.add(newMarker);
                        }
                        if (floorPicked == 1) {
                            MarkersList.add(newMarker);
                        } else {
                            secondFloorMarkersList.add(newMarker);
                        }
                    }
                    CustomMarker.addToCustomMarkers(MapsActivity.this, newMarker, floorPicked);
                    saveSpotLayout.setVisibility(View.GONE);
                }
                InputMethodManager manager1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager1.hideSoftInputFromWindow(slideupview.getWindowToken(), 0);
                break;
            case R.id.btnAddFavorites:
                Favorites.addToFavorite(MapsActivity.this, markerFragment.MTouch.marker2);

                snack = Snackbar.make(findViewById(R.id.map), "Toa To Favorites", Snackbar.LENGTH_SHORT);
                snack.show();
                bntFavoritesRemove.setVisibility(View.VISIBLE);
                btnFavoritesAdd.setVisibility(View.GONE);
                break;
            case R.id.FloorUp:
                floorPicked = 2;
                upFloor.setVisibility(View.GONE);
                downFloor.setVisibility(View.VISIBLE);
                for (Marker markers : secondFloorMarkersList) {
                    if (markerFragment.MTouch.marker2 != null) {
                        if (markers.getTitle().equals(markerFragment.MTouch.marker2.getTitle())) {
                            markers.setVisible(mMap.getCameraPosition().zoom > 18);
                        }
                    } else {
                        markers.setVisible(mMap.getCameraPosition().zoom > 18);
                    }
                }
                for (Marker marker : MarkersList) {
                    marker.setVisible(false);
                }
                if(isTraveling){
                    linesShowing.get(0).setVisible(false);
                    if(linesShowing.size()>1) {
                        linesShowing.get(1).setVisible(true);
                    }
                }
                removeAllOverlays();
                String result = DoTheChecks();
//                CheckResults(result);
                break;
            case R.id.FloorDown:
                floorPicked = 1;

                downFloor.setVisibility(View.GONE);
                upFloor.setVisibility(View.VISIBLE);
                for (Marker markers : MarkersList) {
                    if (markerFragment.MTouch.marker2 != null) {
                        if (markers.getTitle().equals(markerFragment.MTouch.marker2.getTitle())) {
                            markers.setVisible(mMap.getCameraPosition().zoom > 18);
                        }
                    } else {
                        markers.setVisible(mMap.getCameraPosition().zoom > 18);
                    }
                }
                if (B1 != null && B1.size() > 0) {
                    B1.get(1).setVisible(false);
                }
                if (B2 != null && B2.size() > 0) {
                    B2.get(1).setVisible(false);
                }
                for (Marker marker : secondFloorMarkersList) {
                    marker.setVisible(false);
                }
                for (Marker m : ThreeAMarkersF2) {
                    m.setVisible(false);
                }
                for (Marker m2 : BuildingOneF2) {
                    m2.setVisible(false);
                }
                for (Marker m3 : BuildingTwoF2) {
                    m3.setVisible(false);
                }
                if (B3U2 != null) {
                    B3U2.setVisible(false);
                }
                if(isTraveling){
                    linesShowing.get(1).setVisible(false);
                    linesShowing.get(0).setVisible(true);
                }
                removeAllOverlays();
                String results = DoTheChecks();
                String FinerResults = secondCheckForFinerArea(results);
                CheckResults(results,FinerResults);
                break;
            case R.id.FilterButton:
                FilterShow = !FilterShow;
                if (FilterShow) {
                    Filter.setBackground(getDrawable(R.drawable.roundforthefilters));
                    CRFilter.setVisibility(View.VISIBLE);
                    OFFilter.setVisibility(View.VISIBLE);
                    BRFilter.setVisibility(View.VISIBLE);
                    WZFilter.setVisibility(View.VISIBLE);
                    ETCFilter.setVisibility(View.VISIBLE);
                } else {
                    Filter.setBackground(getDrawable(R.drawable.roundfilterbuttongray));
                    CRFilter.setVisibility(View.GONE);
                    OFFilter.setVisibility(View.GONE);
                    BRFilter.setVisibility(View.GONE);
                    WZFilter.setVisibility(View.GONE);
                    ETCFilter.setVisibility(View.GONE);
                }
                break;
            case R.id.CRFilterButton:
                FilteredMarkers("CR");
                Filtering = true;
                break;

            case R.id.OFFilterButton:
                FilteredMarkers("OF");
                Filtering = true;
                break;

            case R.id.BRFilterButton:
                FilteredMarkers("BR");
                Filtering = true;
                break;

            case R.id.WZFilterButton:
                FilteredMarkers("WZ");
                Filtering = true;
                break;

            case R.id.ETCFilterButton:
                FilteredMarkers("ETC");
                Filtering = true;
                break;
        }
    }

    public void removeSpot(String title){
        wasRemoveHit = true;
        Marker m = FindTheMarker(title);

        for (int i = 0; i < favoritedMarkers.size()-1; i++) {
            if (favoritedMarkers.get(i).getTitle().equals(title)) {
                Favorites.removeFromFavorite(MapsActivity.this, m);
                favoritedMarkers.remove(i);
            }
        }
        for (int i = 0; i < createdMarkers.size(); i++) {
            if (createdMarkers.get(i).getTitle().equals(title)) {
                createdMarkers.get(i).remove();
                CustomMarker.removeFromCustomMarkers(MapsActivity.this,m);
                createdMarkers.get(i).setVisible(false);
                CustomMarker.removeFromCustomMarkers(MapsActivity.this, m);
                createdMarkers.remove(i);
                if(i != markerFragment.MTouch.CM.size()) {
                    if (markerFragment.MTouch.CM.get(i).getTitle().equals(title)) {
                        markerFragment.MTouch.CM.get(i).remove();
                        markerFragment.MTouch.CM.remove(i);
                    }
                }

                break;
            }
        }
        RemoveAllLines();
        RemovePoint.setVisibility(View.GONE);
        slideupview.setVisibility(View.GONE);
    }
    private void FilteredMarkers(String type) {
        switch (type) {
            case "CR":
                CRShow = !CRShow;
                break;
            case "OF":
                OFShow = !OFShow;
                break;
            case "BR":
                BRShow = !BRShow;
                break;
            case "WZ":
                WZShow = !WZShow;
                break;
            case "ETC":
                ETCShow = !ETCShow;
                break;
        }

        if (CRShow) {
            for (Marker m : ClassRoomMarkers) {
                m.setVisible(true);
            }
            CRFilter.setBackground(getDrawable(R.drawable.roundforthefilters));
        } else {
            for (Marker m : ClassRoomMarkers) {
                m.setVisible(false);
            }
            CRFilter.setBackground(getDrawable(R.drawable.roundfilterbuttongray));
        }
        if (OFShow) {
            for (Marker m : OFRooms) {
                m.setVisible(true);
            }
            OFFilter.setBackground(getDrawable(R.drawable.roundforthefilters));

        } else {
            for (Marker m : OFRooms) {
                m.setVisible(false);
            }
            OFFilter.setBackground(getDrawable(R.drawable.roundfilterbuttongray));

        }
        if (BRShow) {
            for (Marker m : BathroomMarkers) {
                m.setVisible(true);
            }
            BRFilter.setBackground(getDrawable(R.drawable.roundforthefilters));

        } else {
            for (Marker m : BathroomMarkers) {
                m.setVisible(false);
            }
            BRFilter.setBackground(getDrawable(R.drawable.roundfilterbuttongray));

        }
        if (WZShow) {
            for (Marker m : WaterZones) {
                m.setVisible(true);
            }
            WZFilter.setBackground(getDrawable(R.drawable.roundforthefilters));

        } else {
            for (Marker m : WaterZones) {
                m.setVisible(false);
            }
            WZFilter.setBackground(getDrawable(R.drawable.roundfilterbuttongray));

        }

        if (ETCShow) {
            for (Marker m : ETCRooms) {
                m.setVisible(true);
            }
            ETCFilter.setBackground(getDrawable(R.drawable.roundforthefilters));

        } else {
            for (Marker m : ETCRooms) {
                m.setVisible(false);
            }
            ETCFilter.setBackground(getDrawable(R.drawable.roundfilterbuttongray));

        }
        if (!CRShow && !OFShow && !BRShow && !WZShow && !ETCShow) {
            Filtering = false;
            for (Marker m : MarkersList) {
                m.setVisible(true);
            }

        }
    }
    protected String FindDestinationArea(Marker m){
        if(BuildingOne.contains(m)) {
            return "B1";
        } else if (BuildingOneF2.contains(m)) {
            return "B1F2";
        } else if (BuildingTwo.contains(m)) {
            return "B2";
        }else if(BuildingTwoF2.contains(m)){
            return "B2F2";
        } else if (ThreeAMarkers.contains(m)) {
            return "3A";
        } else if (ThreeAMarkersF2.contains(m)) {
            return "3AF2";
        } else if (ThreeBMarkers.contains(m)) {
            return "3B";
        }else if(ThreeBCMarkers.contains(m)){
            return "3BC";
        } else if (ThreeCMarkers.contains(m)) {
            return "3C";
        } else if (ThreeDMarkers.contains(m)) {
            return "3D";
        }else if(ThreeEMarkers.contains(m)){
            return "3E";
        }else if(FourAMarkers.contains(m)){
            return "4A";
        }
        return "NotFound";
    }
    
    protected void ShowTheseMarkers() {
        if (CRShow) {
            for (Marker m : ClassRoomMarkers) {
                m.setVisible(true);
            }
            CRFilter.setBackground(getDrawable(R.drawable.roundforthefilters));
        } else {
            for (Marker m : ClassRoomMarkers) {
                m.setVisible(false);
            }
            CRFilter.setBackground(getDrawable(R.drawable.roundfilterbuttongray));
        }
        if (OFShow) {
            for (Marker m : OFRooms) {
                m.setVisible(true);
            }
            OFFilter.setBackground(getDrawable(R.drawable.roundforthefilters));

        } else {
            for (Marker m : OFRooms) {
                m.setVisible(false);
            }
            OFFilter.setBackground(getDrawable(R.drawable.roundfilterbuttongray));

        }
        if (BRShow) {
            for (Marker m : BathroomMarkers) {
                m.setVisible(true);
            }
            BRFilter.setBackground(getDrawable(R.drawable.roundforthefilters));

        } else {
            for (Marker m : BathroomMarkers) {
                m.setVisible(false);
            }
            BRFilter.setBackground(getDrawable(R.drawable.roundfilterbuttongray));

        }
        if (WZShow) {
            for (Marker m : WaterZones) {
                m.setVisible(true);
            }
            WZFilter.setBackground(getDrawable(R.drawable.roundforthefilters));

        } else {
            for (Marker m : WaterZones) {
                m.setVisible(false);
            }
            WZFilter.setBackground(getDrawable(R.drawable.roundfilterbuttongray));

        }

        if (ETCShow) {
            for (Marker m : ETCRooms) {
                m.setVisible(true);
            }
            ETCFilter.setBackground(getDrawable(R.drawable.roundforthefilters));

        } else {
            for (Marker m : ETCRooms) {
                m.setVisible(false);
            }
            ETCFilter.setBackground(getDrawable(R.drawable.roundfilterbuttongray));

        }
        if (!CRShow && !OFShow && !BRShow && !WZShow && !ETCShow) {
            Filtering = false;
            for (Marker m : MarkersList) {
                m.setVisible(true);
            }

        }
    }

    protected void removeAllOverlays() {
        if(floorPicked == 1){
            for(GroundOverlay overlay: AllSecondFloorOverlays){
                overlay.setVisible(false);
            }
            for(GroundOverlay overlay: AllFloorOneOverlays){
                overlay.setVisible(true);
            }
        }else{
            for(GroundOverlay overlay: AllFloorOneOverlays){
                overlay.setVisible(false);
            }
            for(GroundOverlay overlay: AllSecondFloorOverlays){
                overlay.setVisible(true);
            }
        }
    }


    protected void showMapsFloor(int floorPicked) {

    }

    //Getting permission from user for location
    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_lOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //SET A BOOLEAN
                mLocationPermissionsGranted = true;

            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onMapLongClick(LatLng point) {
        wasRemoveHit = false;
        saveSpotLayout.setVisibility(View.VISIBLE);
        LongClickPoint = point;
    }



    String photoCode;
    @Override
    public boolean onMarkerClick(Marker marker) {


        dest = FindMarkerAreaForTravel(markerFragment.MTouch.marker2, 1);
        photoCode = FindDestinationArea(marker);
        wasMarkerClicked = true;
        new MarkerShowInfo().execute();
        return true;
    }

    public void checkIfMarkerNeedVisible() {
        if (!wasMarkerClicked) {
            if (mMap.getCameraPosition().zoom > 18) {
                for (Marker m : MarkersList) {
                    if (!Filtering) {
                        m.setVisible(true);
                    }
                }
            } else {
                for (Marker m2 : MarkersList) {
                    m2.setVisible(false);
                }
            }
            for (Marker mC : createdMarkers) {
                mC.setVisible(true);
            }
        } else {
            for (Marker m3 : MarkersList) {
                if (!m3.getTitle().equals(markerFragment.MTouch.marker2.getTitle())) {
                    m3.setVisible(false);
                }
            }
            for (Marker m3C : createdMarkers) {
                if (!m3C.getTitle().equals(markerFragment.MTouch.marker2.getTitle())) {
                    m3C.setVisible(false);
                }
            }
        }
    }

    //Handles what happens when user clicks on the map (not the same as drag)
    @Override
    public void onMapClick(LatLng point) {
        //Hide Keyboard when map is clicked
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(slideupview.getWindowToken(), 0);
        if (slidepup) {
            slideupview.setVisibility(View.GONE);
            slidepup = false;
        }
        if (RemovePoint.getVisibility() == View.VISIBLE) {
            RemovePoint.setVisibility(View.GONE);
        }
        if (saveSpotLayout.getVisibility() == View.VISIBLE) {
            saveSpotLayout.setVisibility(View.GONE);
        }
        //Make all markers visible
//        checkIfMarkerNeedVisible();
    }

    public void RemoveAllLines() {
        while (linesShowing.size() > 0) {
            linesShowing.get(0).remove();
            linesShowing.remove(0);
        }
    }

    public Marker FindTheMarker(String title) {
        Marker foundMarker = null;
        if (createdMarkers != null) {
            for (Marker m : createdMarkers) {
                if (m.getTitle().equals(title)) {
                    foundMarker = m;
                }
            }
        }
        if (MarkersList != null) {
            for (Marker m1 : MarkersList) {
                if (m1.getTitle().equals(title)) {
                    foundMarker = m1;
                }
            }
        }
        if (favoritedMarkers != null) {
            for (Marker m2 : favoritedMarkers) {
                if (m2.getTitle().equals(title)) {
                    foundMarker = m2;
                }
            }
        }
        if (secondFloorMarkersList != null) {
            for (Marker m3 : secondFloorMarkersList) {
                if (m3.getTitle().equals(title)) {
                    foundMarker = m3;
                }
            }
        }
        return foundMarker;
    }

    ArrayList<LatLng> Pathfloor3A = new ArrayList<>(Arrays.asList(
            new LatLng(28.595419921191642,-81.30425088107586),
            new LatLng(28.595421393089424,-81.30402222275734),
            new LatLng(28.595618332827797,-81.30403496325016),
            new LatLng(28.595613917142643,-81.30408123135567),
            new LatLng(28.59561156211047,-81.30416739732026),
            new LatLng(28.59562510354469,-81.30416505038738),
            new LatLng(28.595627752955522,-81.30407486110926)));
    ArrayList<LatLng> BuildingStairEntryPointsTop = new ArrayList<>(Arrays.asList(
            new LatLng(28.596682801917005,-81.30251448601486),
            new LatLng(28.59665984058472,-81.30256544798613)
    ));
    ArrayList<LatLng> BuildingStairEntryPointsRight = new ArrayList<>(Arrays.asList(
            new LatLng(28.59627832848325,-81.3022429123521),
            new LatLng(28.59624947951801,-81.30222883075477),
            new LatLng(28.59627273531714,-81.30218122154474),
            new LatLng(28.59628421602619,-81.30218490958214),
            new LatLng(28.59627450158014,-81.30220770835876)
    ));
    ArrayList<LatLng> BuildingStairEntryPointsLeft = new ArrayList<>(Arrays.asList(
            new LatLng(28.596132906067687,-81.30256243050098),
            new LatLng(28.596138793618795,-81.30250677466393),
            new LatLng(28.596191192809023,-81.302385404706),
            new LatLng(28.596227401222972,-81.30236864089966),
            new LatLng(28.59624918514078,-81.3023766875267),
            new LatLng(28.59622534058185,-81.302430331707),
            new LatLng(28.59621621488502,-81.30242496728897),
            new LatLng(28.596224457449928,-81.30240518599749)
    ));
    public ArrayList<LatLng> GetPathToSecondFloor(String Destination,LatLng DestinationPoint){
        ArrayList<LatLng> points = new ArrayList<>();
        switch(Destination){
            case "3AF2":
                if(Latitude < 28.595632463019054){


                    for (LatLng point : Pathfloor3A){
                        points.add(point);
                    }


                } else {

                    for (LatLng point: Pathfloor3A){
                        if(point.latitude > 28.595421393089424){

                            points.add(point);

                        }
                    }
                }
                break;
            case "B2F2":
                boolean rightside = false;
                if (Longitued > -81.30276795476675) {
                    rightside = true;
                }
                if(DestinationPoint.latitude > 28.596601259727045){
                    for(LatLng point: BuildingStairEntryPointsTop){
                        points.add(point);
                    }
                }
                else {
                    if(rightside){
                        for(LatLng point: BuildingStairEntryPointsRight){
                            points.add(point);
                        }
                    }else{
                        for(LatLng point: BuildingStairEntryPointsLeft){
                            points.add(point);
                        }
                    }
                }
                break;
            case "B1F2":
                break;
        }
        return points;
    }

    public boolean CheckMarkerType(Marker marker) {
        boolean isItCreatedMarker = false;
        for (Marker m : createdMarkers) {
            if (m.getTitle().equals(marker.getTitle())) {
                isItCreatedMarker = true;
            }
        }
        return isItCreatedMarker;
    }

    //Credit to Ruben for solving everything below here
    //get directions to marker
    public void getDirectionPoly(Marker marker) {
        getDeviceLocation();
        if (dest == null) {
            dest = FindMarkerAreaForTravel(marker, 1);
        }
        //LatLng dest = marker.getPosition();
        String url1 = "";
//            if (!CheckMarkerType(marker)) {
//                url1 = getUrl(new LatLng(Latitude, Longitued), MarkersList.get(0).getPosition());
//            } else {
        url1 = getUrl(new LatLng(Latitude, Longitued), dest);
//            }
        String url = url1;

        TaskRequestDirections taskRequestDirections = new TaskRequestDirections(marker);
        taskRequestDirections.execute(url);
    }

    //Get URL for Getting Direction
    private String getUrl(LatLng origin, LatLng dest) {
        String str_org = "origin=" + origin.latitude + "," + origin.longitude;

        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        String sensor = "sensor=false";

        String mode = "mode=walking";

        String param = str_dest + "&" + mode + "&" + str_org + "&" + sensor;

        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&key=AIzaSyBXJ-PMOg2kDp8jXih-3ME_52znZc6A2ds ";

        return url;
    }

    private String getDirectionsUrl(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    private class MarkerShowInfo extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            while(!markerFragment.MTouch.markerready){

            }
            return "succ" ;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("succ")){
                FindTheMarker(markerFragment.MTouch.marker2.getTitle()).showInfoWindow();
                markerFragment.MTouch.markerready = false;
            }
        }
    }

    public class TaskRequestDirections extends AsyncTask<String, Void, String> {
        Marker marker;

        public TaskRequestDirections() {
        }

        public TaskRequestDirections(Marker _marker) {
            marker = _marker;
        }

        @Override
        protected String doInBackground(String... strings) {
            String responseString = "";
            try {
                responseString = getDirectionsUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //parse json here
            TaskParser taskParser = new TaskParser(marker);
            taskParser.execute(s);
        }

        public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {
            Marker marker;

            public TaskParser() {
            }

            public TaskParser(Marker _marker) {
                marker = _marker;
            }

            @Override
            protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
                JSONObject jsonObject = null;
                List<List<HashMap<String, String>>> routes = null;
                try {
                    jsonObject = new JSONObject(strings[0]);
                    DataParser dataParser = new DataParser();
                    routes = dataParser.parse(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return routes;
            }

            @Override
            protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
                ArrayList points = null;
                ArrayList SecondFloorPoints = null;
                PolylineOptions polylineOptions = null;

                for (List<HashMap<String, String>> path : lists) {
                    points = new ArrayList();
                    polylineOptions = new PolylineOptions();

                    for (HashMap<String, String> point : path) {
                        double lat = Double.parseDouble(point.get("lat"));
                        double lon = Double.parseDouble(point.get("lon"));

                        points.add(new LatLng(lat, lon));
                    }

                    String TravelingTo = FindDestinationArea(marker);
                    LatLng PosToCheck = FindMarkerAreaForTravel(marker,2);
                    LatLng PosOfMarker = marker.getPosition();
                    String area = FindQuadrantForArea(marker.getPosition(),PosToCheck);
                    switch (TravelingTo){
                        case "B1":
                            break;
                        case"B1F2":
                            break;
                        case"B2":
                             area = FindQuadrantForAreaBuilding2(marker.getPosition());
                            points.addAll(ChoosePointsToGrabOutsideToInsideBuilding2(area, PosOfMarker));
                            break;
                        case "B2F2":
                            SecondFloorPoints = new ArrayList();
                            area = FindQuadrantForAreaBuilding2UP(marker.getPosition());
                            points.addAll(GetPathToSecondFloor("B2F2",marker.getPosition()));
                            SecondFloorPoints.addAll(ChoosePointsToGrabOutsideToInsideBuilding2UP(area,marker.getPosition()));
                            break;
                        case "3A":
                            points.addAll(ChoosePointsToGrabOutsideToInside3A(area,PosOfMarker));
                            break;
                        case "3AF2":
                            SecondFloorPoints = new ArrayList();
                            points.addAll(GetPathToSecondFloor("3AF2",marker.getPosition()));
                            SecondFloorPoints.addAll(ChoosePointsToGrabOutsideToInside3AF2(area,PosOfMarker));
                            break;
                        case "3B":
                            points.addAll(ChoosePointsToGrabOutsideToInsideFishBowl(area, PosOfMarker));
                            break;
                        case "3BC":
                            points.addAll(ChoosePointsToGrabOutsideToInside3BC(area,PosOfMarker));
                            break;
                        case "3C":
                            points.addAll(ChoosePointsToGrabOutsideToInside3C(area,PosOfMarker));
                            break;
                        case "3D":
                            points.addAll(ChoosePointToGrabOutsideToInside3D(area,PosOfMarker));
                            break;
                        case "3E":
                            points.addAll(ChoosePointToGrabOutsideToInside3E(area,PosOfMarker));
                    }


                    polylineOptions.addAll(points);
                    polylineOptions.width(15);
                    if (DarkorLight) {
                        polylineOptions.color(Color.BLUE);
                    } else {
                        polylineOptions.color(Color.parseColor("#FFA500"));
                    }
                    polylineOptions.geodesic(true);
                }
                Polyline poly = mMap.addPolyline(new PolylineOptions().addAll(points).color(Color.parseColor("#E65400")).width(15));
                linesShowing.add(poly);
                if(SecondFloorPoints != null){
                    Polyline second = mMap.addPolyline(new PolylineOptions().addAll(SecondFloorPoints).color(Color.parseColor("#E65400")).width(15));
                    second.setVisible(false);
                    linesShowing.add(second);
                    changeInmmediatelyFloor();
                }
                if (polylineOptions != null) {
//Q
                } else {
                    snack = Snackbar.make(findViewById(R.id.map), "Directions Not Found", Snackbar.LENGTH_SHORT);
                    snack.show();
                }
            }
        }
    }

    private void changeInmmediatelyFloor() {
        floorPicked = 1;

        downFloor.setVisibility(View.GONE);
        upFloor.setVisibility(View.VISIBLE);
        for (Marker markers : MarkersList) {
            if (markerFragment.MTouch.marker2 != null) {
                if (markers.getTitle().equals(markerFragment.MTouch.marker2.getTitle())) {
                    markers.setVisible(mMap.getCameraPosition().zoom > 18);
                }
            } else {
                markers.setVisible(mMap.getCameraPosition().zoom > 18);
            }
        }
        if (B1 != null && B1.size() > 0) {
            B1.get(1).setVisible(false);
        }
        if (B2 != null && B2.size() > 0) {
            B2.get(1).setVisible(false);
        }
        for (Marker marker : secondFloorMarkersList) {
            marker.setVisible(false);
        }
        for (Marker m : ThreeAMarkersF2) {
            m.setVisible(false);
        }
        for (Marker m2 : BuildingOneF2) {
            m2.setVisible(false);
        }
        for (Marker m3 : BuildingTwoF2) {
            m3.setVisible(false);
        }
        if (B3U2 != null) {
            B3U2.setVisible(false);
        }
        if(isTraveling){
            linesShowing.get(1).setVisible(false);
            linesShowing.get(0).setVisible(true);
        }
        removeAllOverlays();
        String results = DoTheChecks();
        String FinerResults = secondCheckForFinerArea(results);
        CheckResults(results,FinerResults);

    }
    public class DoDahClick extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            while(!CMReady && !FMReady){

            }
            return "succ";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("succ")){
                doTheClick(createdMarkers);
            }
        }
    }
    public class DoDahRemove extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            while(!CMReady && !FMReady && createdMarkers.size() > 0){

            }
            return "succ";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("succ")){
                removeSpot(MarkerToRemoveTitle);
            }
        }
    }
}
