package com.example.FSMap;
//Woohoo, imports

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
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
import androidx.core.splashscreen.SplashScreen;
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
    ArrayList<GroundOverlay> B3U = new ArrayList<>();
    ArrayList<GroundOverlay> B3D = new ArrayList<>();
    ArrayList<GroundOverlay> B4U = new ArrayList<>();
    ArrayList<GroundOverlay> B4D = new ArrayList<>();
    ArrayList<GroundOverlay> B2 = new ArrayList<>();
    ArrayList<GroundOverlay> B1 = new ArrayList<>();
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
    boolean wasRemoveHit = false;
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
    boolean isNOTfUCKED = false;
    int floorPicked = 1;
    CameraPosition cameraLoad;
    int altitudesCollectedNumber;
    List<LatLng> ltln = new ArrayList<>();
    private Double lat_decimal = 0.0, lng_decimal = 0.0;
    boolean isTraveling = false;
    Marker usermarker;
    Circle userLocationAccuracyCircle;
    Location currentLocation;
    String prevResult = "";
    String firstLoadResult = "";
    ArrayList<String> resultsList = new ArrayList<>();
    private MarkerFragment markerFragment;
    boolean isAndroidReady = false;
    boolean csvmarkerready, cmmarkerready = false;
    ArrayList<String> SearchList;
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
                    return "3BConnected";
                } else if (checklat < 28.59533160728655) {
                    return "FishBowl";
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

    protected void FirstLoad(String result, String firstLoadResults) {
        switch (result) {
            case "b3u"://check if result was building 3 top half.
                if (firstLoadResults.equals("3A")) {
                    B3U.add(mMap.addGroundOverlay(groundOverlaysf1.get(0)));
                    B3U2 = mMap.addGroundOverlay(groundOverlaysf2.get(0));
                    B3U.get(B3U.size() - 1).setDimensions(34, 28);
                    B3U2.setDimensions(34, 28);
                    if (floorPicked == 1) {
                        B3U2.setVisible(false);
                    } else {
                        B3U.get(B3U.size() - 1).setVisible(false);
                    }
                } else if (firstLoadResults.equals("FishBowl")) {
                    B3U.add(mMap.addGroundOverlay(groundOverlaysf1.get(1)));
                    B3U.get(B3U.size() - 1).setDimensions(84, 62);
                } else if (firstLoadResults.equals("3BConnected")) {
                    B3U.add(mMap.addGroundOverlay(groundOverlaysf1.get(2)));
                    B3U.get(B3U.size() - 1).setDimensions(64, 30);
                }

                break;
            case "b3d"://check if result was building 3 bottom half
                if (firstLoadResults.equals("3C")) {
                    B3D.add(mMap.addGroundOverlay(groundOverlaysf1.get(3)));
                    B3D.get(B3D.size() - 1).setDimensions(40, 42);
                } else if (firstLoadResults.equals("3D")) {
                    B3D.add(mMap.addGroundOverlay(groundOverlaysf1.get(4)));
                    B3D.get(B3D.size() - 1).setDimensions(40, 30);
                } else if (firstLoadResults.equals("3E")) {
                    B3D.add(mMap.addGroundOverlay(groundOverlaysf1.get(5)));
                    B3D.get(B3D.size() - 1).setDimensions(37, 28);
                } else if (firstLoadResults.equals("3F")) {
                    B3D.add(mMap.addGroundOverlay(groundOverlaysf1.get(6)));
                    B3D.get(B3D.size() - 1).setDimensions(100, 80);
                }
                break;
            case "b4u":
                if (firstLoadResults.equals("4A")) {
                    B4U.add(mMap.addGroundOverlay(groundOverlaysf1.get(7)));
                    B4U.get(B4U.size() - 1).setDimensions(90, 50);
                } else if (firstLoadResults.equals("4AWD2")) {
                    B4U.add(mMap.addGroundOverlay(groundOverlaysf1.get(8)));
                    B4U.get(B4U.size() - 1).setDimensions(56, 43);
                } else if (firstLoadResults.equals("4AFC")) {
                    B4U.add(mMap.addGroundOverlay(groundOverlaysf1.get(9)));
                    B4U.get(B4U.size() - 1).setDimensions(70, 50);
                } else if (firstLoadResults.equals("4B")) {
                    B4U.add(mMap.addGroundOverlay(groundOverlaysf1.get(10)));
                    B4U.get(B4U.size() - 1).setDimensions(68, 48);
                }
                break;
            case "b4d":
                if (firstLoadResults.equals("4C")) {
                    B4D.add(mMap.addGroundOverlay(groundOverlaysf1.get(11)));
                    B4D.get(B4D.size() - 1).setDimensions(72, 54);
                } else if (firstLoadResults.equals("4D")) {
                    B4D.add(mMap.addGroundOverlay(groundOverlaysf1.get(12)));
                    B4D.get(B4D.size() - 1).setDimensions(100, 60);
                } else if (firstLoadResults.equals("4E")) {
                    B4D.add(mMap.addGroundOverlay(groundOverlaysf1.get(13)));
                    B4D.get(B4D.size() - 1).setDimensions(100, 60);
                }
                break;
            case "b2":
                B2.add(mMap.addGroundOverlay(groundOverlaysf1.get(groundOverlaysf1.size() - 1)));
                B2.get(B2.size() - 1).setDimensions(157, 95);
                B2.add(mMap.addGroundOverlay(groundOverlaysf2.get(groundOverlaysf2.size() - 1)));
                B2.get(B2.size() - 1).setDimensions(157, 95);
                if (floorPicked == 1) {
                    B2.get(1).setVisible(false);
                } else {
                    B2.get(0).setVisible(false);
                }

                break;
            case "b1":
                B1.add(mMap.addGroundOverlay(groundOverlaysf1.get(groundOverlaysf1.size() - 2)));
                B1.get(B1.size() - 1).setDimensions(140, 90);
                B1.add(mMap.addGroundOverlay(groundOverlaysf2.get(groundOverlaysf2.size() - 2)));
                B1.get(B1.size() - 1).setDimensions(136, 94);
                if (floorPicked == 1) {
                    B1.get(1).setVisible(false);
                } else {
                    B1.get(0).setVisible(false);
                }
                break;

        }
    }

    protected void HideAllOverlays() {
        if (B3U.size() > 0) {
            for (int i = 0; i < B3U.size(); i++) {
                B3U.get(i).setVisible(false);
            }
        }
        if (B3D.size() > 0) {
            for (int i = 0; i < B3D.size(); i++) {
                B3D.get(i).setVisible(false);
            }
        }
        if (B4U.size() > 0) {
            for (int i = 0; i < B4U.size(); i++) {
                B4U.get(i).setVisible(false);
            }
        }
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

    public void CheckResults(String result) {
        if (prevResult != result) {
            HideAllOverlays();
            prevResult = result;
        }
        switch (result) {
            case "b3u"://check if result was building 3 top half.
                if (floorPicked == 1) {
                    for (GroundOverlay overlay : B3U) {
                        overlay.setVisible(true);
                    }
                } else {
                    HideAllOverlays();
                    if (B3U2 != null) {
                        B3U2.setVisible(true);
                    }
                }
                break;
            case "b3d"://check if result was building 3 bottom half
                if (floorPicked == 1) {
                    for (GroundOverlay overlay : B3D) {
                        overlay.setVisible(true);
                    }
                }
                break;
            case "b4u":
                if (floorPicked == 1) {
                    for (GroundOverlay overlay : B4U) {
                        overlay.setVisible(true);
                    }
                }
                break;
            case "b4d":
                if (floorPicked == 1) {
                    for (GroundOverlay overlay : B4D) {
                        overlay.setVisible(true);
                    }
                }
                break;
            case "b2":
                if (B2.size() > 0) {
                    if (floorPicked == 1) {
                        B2.get(0).setVisible(true);
                    } else {
                        HideAllOverlays();
                        B2.get(1).setVisible(true);
                    }
                }
                break;
            case "b1":
                if (B1.size() > 0) {
                    if (floorPicked == 1) {
                        B1.get(0).setVisible(true);
                    } else {
                        HideAllOverlays();
                        B1.get(1).setVisible(true);
                    }
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
                markerTitle2 = extras.getString("marker_ToMap");
                isNOTfUCKED = true;
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

        // FAVORITES
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if (extras == null) {
                //Extra bundle is null
                isNOTfUCKED = false;
            } else {
                markerTitle2 = extras.getString("marker");
                isNOTfUCKED = true;
            }
        }
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
                        newMarker.showInfoWindow();
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
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                BitmapDescriptor build3aF1BitMap = BitmapDescriptorFactory.fromResource(R.drawable.building_3a_blackmoore_1f_rotated);

                //Set the bounds for overlays
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
                //create map overlayoptions
                GroundOverlayOptions buildLibraryOverlay = new GroundOverlayOptions()
                        .positionFromBounds(buildLibrary)
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.buildinglibrary_rotated_1_left))
                        .anchor(0.43f, 0.45f);
                GroundOverlayOptions build3aOverlay = new GroundOverlayOptions()
                        .positionFromBounds(build3A)
                        .image(build3aF1BitMap)
                        .anchor(1.0f, -0.1f)
                        .bearing(-2);
                GroundOverlayOptions build3aF2Overlay = new GroundOverlayOptions()
                        .positionFromBounds(build3AF2)
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.building_3a_blackmoore_2f))
                        .anchor(1.0f, -0.1f)
                        .bearing(-2);
                GroundOverlayOptions building3BOverlay = new GroundOverlayOptions()
                        .positionFromBounds(build3B)
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.building_3b_fishbowl))
                        .anchor(0.45f, 0.45f);
                GroundOverlayOptions build3BConnected = new GroundOverlayOptions()
                        .positionFromBounds(build3BConnect)
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.building_3b_gd))
                        .anchor(0.08f, 0.77f);
                GroundOverlayOptions build3COverlay = new GroundOverlayOptions()
                        .positionFromBounds(build3C)
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.building_3c_gd));
                GroundOverlayOptions build3DOverlay = new GroundOverlayOptions()
                        .positionFromBounds(build3CMP)
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.building_3c_mp))
                        .anchor(0.7f, 0.7f);
                GroundOverlayOptions build3FOverlay = new GroundOverlayOptions()
                        .positionFromBounds(build3F)
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.building_3f_1f));
                GroundOverlayOptions build4COverlay = new GroundOverlayOptions()
                        .positionFromBounds(build4C)
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.building_4c))
                        .bearing(135)
                        .anchor(0.48f, 0.62f);
                GroundOverlayOptions build4BOverlay = new GroundOverlayOptions()
                        .positionFromBounds(build4B)
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.building_4b_1f));
                GroundOverlayOptions build4AOverlay = new GroundOverlayOptions()
                        .positionFromBounds(build4A)
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.building_4a_wd1));
                GroundOverlayOptions build4AWD2Overlay = new GroundOverlayOptions()
                        .positionFromBounds(build4AWD2)
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.building_4a_wd2))
                        .bearing(44)
                        .anchor(0.6f, 0.75f);
                GroundOverlayOptions build4AFCOverlay = new GroundOverlayOptions()
                        .positionFromBounds(build4AFC)
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.building_4a_fc))
                        .bearing(-46);
                GroundOverlayOptions build4DOverlay = new GroundOverlayOptions()
                        .positionFromBounds(build4D)
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.building_4d_1f))
                        .bearing(45);
                GroundOverlayOptions build4EOverlay = new GroundOverlayOptions()
                        .positionFromBounds(build4E)
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.building_4e_distrubution))
                        .bearing(45)
                        .anchor(0.5f, 0.5f);
                GroundOverlayOptions build1f1Overlay = new GroundOverlayOptions()
                        .positionFromBounds(build1_1f)
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.building_1_1f))
                        .bearing(180)
                        .anchor(0.56f, 0.5f);
                GroundOverlayOptions build1f2Overlay = new GroundOverlayOptions()
                        .positionFromBounds(build1_2f)
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.building_1_2f))
                        .bearing(180)
                        .anchor(0.558f, 0.485f);

                GroundOverlayOptions build2f1Overlay = new GroundOverlayOptions()
                        .positionFromBounds(build2_1f)
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.building_2_1f))
                        .bearing(-117.5f)
                        .anchor(0.605f, 0.387f);
                GroundOverlayOptions build2f2Overlay = new GroundOverlayOptions()
                        .positionFromBounds(build2_2f)
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.building_2_2f))
                        .bearing(-117.5f)
                        .anchor(0.605f, 0.387f);
                //add groundOverlay and create reference.
                groundOverlaysf1.add(build3aOverlay);
                groundOverlaysf2.add(build3aF2Overlay);
                groundOverlaysf1.add(building3BOverlay);
                groundOverlaysf1.add(build3BConnected);
                groundOverlaysf1.add(build3COverlay);
                groundOverlaysf1.add(build3DOverlay);
                groundOverlaysf1.add(buildLibraryOverlay);
                groundOverlaysf1.add(build3FOverlay);
                groundOverlaysf1.add(build4AOverlay);
                groundOverlaysf1.add(build4AWD2Overlay);
                groundOverlaysf1.add(build4AFCOverlay);
                groundOverlaysf1.add(build4BOverlay);
                groundOverlaysf1.add(build4COverlay);
                groundOverlaysf1.add(build4DOverlay);
                groundOverlaysf1.add(build4EOverlay);
                groundOverlaysf1.add(build1f1Overlay);
                groundOverlaysf1.add(build2f1Overlay);
                groundOverlaysf2.add(build1f2Overlay);
                groundOverlaysf2.add(build2f2Overlay);
                String result = DoTheChecks();
                String secondResult = secondCheckForFinerArea(result);
                FirstLoad(result, secondResult);
//                CheckResults(result);
                prevResult = result;

            }
        });
        SharedPreferences settings = getSharedPreferences("SOME_NAME", 0);
        float longitude = settings.getFloat("longitude", (float) Longitued);
        float latitude = settings.getFloat("latitude", (float) Latitude);
        float zoomload = settings.getFloat("zoom", zoom);
        floorPicked = settings.getInt("floorPicked", floorPicked);


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
        BitmapDrawable bitmapdraw1 = (BitmapDrawable) getResources().getDrawable(R.drawable.pixilart_drawing);
        Bitmap b1 = bitmapdraw1.getBitmap();
        Bitmap smallMarker1 = Bitmap.createScaledBitmap(b1, 100, 100, false);

        //Markers for Bathrooms
        BitmapDrawable bitmapdraw2 = (BitmapDrawable) getResources().getDrawable(R.drawable.pixil_frame_0);
        Bitmap b2 = bitmapdraw2.getBitmap();
        Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, 100, 100, false);

        //Markers for WaterZones
        BitmapDrawable bitmapdraw3 = (BitmapDrawable) getResources().getDrawable(R.drawable.pixilart_drawing__1_);
        Bitmap b3 = bitmapdraw3.getBitmap();
        Bitmap smallMarker3 = Bitmap.createScaledBitmap(b3, 100, 100, false);

        //Markers for ETCRooms
        BitmapDrawable bitmapdraw4 = (BitmapDrawable) getResources().getDrawable(R.drawable.etc_marker);
        Bitmap b4 = bitmapdraw4.getBitmap();
        Bitmap smallMarker4 = Bitmap.createScaledBitmap(b4, 100, 100, false);

        //Markers for OFRooms
        BitmapDrawable bitmapdraw5 = (BitmapDrawable) getResources().getDrawable(R.drawable.office_marker);
        Bitmap b5 = bitmapdraw5.getBitmap();
        Bitmap smallMarker5 = Bitmap.createScaledBitmap(b5, 100, 100, false);

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
                    ClassRoom.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker1));
                }

                //Set Markers image for bathrooms
                for (Marker Bathrooms : BathroomMarkers) {
                    Bathrooms.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker2));
                }

                //Set Markers image for Waterzones
                for (Marker WaterStation : WaterZones) {
                    WaterStation.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker3));
                }
                //Set Markers image for ETC rooms
                for (Marker etc : ETCRooms) {
                    etc.setIcon(BitmapDescriptorFactory.fromBitmap((smallMarker4)));
                }
                //Set Marker image for OF rooms
                for (Marker etc : OFRooms) {
                    etc.setIcon(BitmapDescriptorFactory.fromBitmap((smallMarker5)));
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
            if (groundOverlaysf1.size() > 0) {
                String result = DoTheChecks();
                String FinerResult = secondCheckForFinerArea(result);
                if (FinerResult == "3BConnected" || FinerResult == "FishBowl") {
                    FinerResult = "3B";
                }
                if (prevResult != FinerResult) {
                    prevResult = FinerResult;
                    if (!Filtering) {
                        HideAllOtherMarkers(FinerResult);
                        showMarkerInArea(FinerResult);
                    } else {
                        ShowTheseMarkers();
                        HideAllOtherMarkers(FinerResult);
                    }
                }
                if (CheckResultLoadType(result)) {
                    CheckResults(result);
                } else {
                    String secondResult = secondCheckForFinerArea(result);
                    if (!CheckResultLoadType(secondResult)) {
                        FirstLoad(result, secondResult);
                    }
                }
            }
//            if (wasRemoveHit) {
//                for (int i = 0; i < createdMarkers.size(); i++) {
//                    if (createdMarkers.get(i).getTitle().equals(markerFragment.MTouch.createdMarker.getTitle())) {
//                        createdMarkers.get(i).remove();
//                        createdMarkers.remove(i);
//                    }
//                }
//            }
        });

        //On Marker Click Override
        markerFragment.MTouch.setGoogleMap(mMap, linesShowing, this.getApplicationContext(), this);

        // disable marker click processing
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
            }
        });

        //when camera is still (used for searchbar since it doesn't count as camera moving)
        mMap.setOnCameraIdleListener(() -> {

//            if (groundOverlaysf1.size() > 0) {
//                String result = DoTheChecks();
//                String FinerResult = secondCheckForFinerArea(result);
//                if(FinerResult == "3BConnected" || FinerResult == "FishBowl")
//                {
//                    FinerResult = "3B";
//                }
//                if(prevResult != FinerResult)
//                {
//
//                    prevResult = FinerResult;
//                    if (!Filtering) {
//                        HideAllOtherMarkers(FinerResult);
//                        showMarkerInArea(FinerResult);
//                    } else {
//                        HideAllOtherMarkers(FinerResult);
//                        ShowTheseMarkers();
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
//
//            }
            if (groundOverlaysf1.size() > 0) {
                String result = DoTheChecks();
                String FinerResult = secondCheckForFinerArea(result);
                if (FinerResult == "3BConnected" || FinerResult == "FishBowl") {
                    FinerResult = "3B";
                }
                if (prevResult != FinerResult) {

                    prevResult = FinerResult;
                    if (!Filtering) {
                        HideAllOtherMarkers(FinerResult);
                        showMarkerInArea(FinerResult);
                    } else {
                        HideAllOtherMarkers(FinerResult);
                        ShowTheseMarkers();
                    }
                }
                if (CheckResultLoadType(result)) {
                    CheckResults(result);
                } else {
                    String secondResult = secondCheckForFinerArea(result);
                    if (!CheckResultLoadType(secondResult)) {
                        FirstLoad(result, secondResult);
                    }
                }

            }
            if (FollowUser || wasMarkerClicked) {
                navloc();
            }
//            if (wasRemoveHit) {
//                for (int i = 0; i < createdMarkers.size(); i++) {
//                    if (createdMarkers.get(i).getTitle().equals(markerFragment.MTouch.createdMarker.getTitle())) {
//                        createdMarkers.get(i).remove();
//                        createdMarkers.remove(i);
//                    }
//                }
//                for (int i = 0; i < favoritedMarkers.size(); i++) {
//                    if (favoritedMarkers.get(i).getTitle().equals(markerFragment.MTouch.createdMarker.getTitle())) {
//                        favoritedMarkers.remove(i);
//                    }
//                }
//            }
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
                        newMarker.showInfoWindow();
                        createdMarkers.add(newMarker);
                    }
                    if (floor == 1 && newMarker != null) {
                        MarkersList.add(newMarker);
                    } else if (newMarker != null) {
                        secondFloorMarkersList.add(newMarker);
                    }
                }
                markerFragment.MTouch.CustomMarkers(createdMarkers);
                if (createdMarkers != null) {
                    if (createdMarkers.size() > 0) {
                        doTheClick(createdMarkers);
                    }
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
        }else if(_latitdue < latToCheckQ3UP){

            return "Q3";

        }else if(_longitude < longToCheckQ4Up){

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

                if(Latitude > 28.596310709965394){

                    for (int i =1; i < 6; i++){

                        points.add(Q1Building2UP.get(i));

                    }
                    for (LatLng point: Q2Building2UP){
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
                    }

                }else{

                    points.add(Q3Building2UP.get(1));
                    points.add(Q3Building2UP.get(2));
                    points.add(Q3Building2UP.get(4));
                    points.add(Q3Building2UP.get(0));

                    if(Destination.longitude <-81.30245883017778){

                        for (LatLng point1: Q2Building2UP){
                            if(point1.latitude <= Destination.latitude){
                                points.add(point1);
                            }

                        }
                    }else{
                        for (LatLng point2: Q2Building2UP){
                            if(point2.latitude <=28.596422867567355){

                                points.add(point2);
                            }
                        }
                    }

                }




            } else {

                if(Latitude > 28.596310709965394){

                    for (int i = Q1Building2UP.size()- 1; i > 5;i--){
                        points.add(Q1Building2UP.get(i));
                    }
                    for (LatLng point: Q2Building2UP){
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
                    }
                }else{


                    points.add(Q3Building2UP.get(0));

                    for (LatLng point: Q2Building2UP){
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
                    }
                }
            }


        } else if (QuadrantDestination.equals("Q3")) {


            if (rightside){
                if(Destination.latitude > 28.59595156933239 && Destination.longitude > -81.30241021513939){
                    for (LatLng point :Q3Building2UP){

                        if(point.latitude >= 28.596157045025098 && point.longitude >= -81.30236964672804)
                        points.add(point);
                    }
                }else {

                   if(Destination.latitude > 28.596147036189763 && Destination.longitude < -81.30244106054306){
                       for (LatLng point : Q3Building2UP){
                           if(point.latitude > 28.596147036189763 && point.latitude < 28.59622269118608){
                               points.add(point);
                           }
                       }
                   }
                   else if(Destination.longitude < -81.3025004044175)
                   {
                       for(LatLng point : Q3Building2UP){
                           if(point.latitude > 28.596147036189763 && point.latitude < 28.59622269118608 && point.longitude > -81.30248330533504 && point.latitude >= Destination.latitude){
                               points.add(point);
                           }
                       }
                   } else if (Destination.longitude  > -81.3025004044175 && Destination.latitude > 28.59595156933239) {
                       for(LatLng point : Q3Building2UP){
                           if(point.latitude > 28.596147036189763 && point.latitude < 28.59622269118608 && point.longitude > -81.30245681852102 && point.latitude >= Destination.latitude){
                               points.add(point);
                           }
                       }
                   } else if (Destination.longitude <-81.30233678966759){

                       for(LatLng point : Q3Building2UP){
                           if(point.latitude > 28.596147036189763 && point.latitude < 28.59622269118608 && point.longitude > -81.30245681852102){
                               points.add(point);
                           }
                       }
                   }
                   else {
                       for(LatLng point : Q3Building2UP){
                           if(point.latitude > 28.596147036189763 && point.latitude < 28.59622269118608 && point.longitude > -81.30245681852102 &&point.latitude >= Destination.latitude){
                               points.add(point);
                           }
                       }
                   }
                }

            }else{
                if(Destination.latitude > 28.59595156933239 && Destination.longitude > -81.30241021513939){
                    points.add(Q3Building2UP.get(0));
                    points.add(Q3Building2UP.get(4));
                    points.add(Q3Building2UP.get(2));
                    points.add(Q3Building2UP.get(3));

                }else {

                    if(Destination.latitude > 28.596147036189763 && Destination.longitude < -81.30244106054306){
                        for (LatLng point : Q3Building2UP){
                            if(point.latitude > 28.596147036189763 && point.latitude < 28.59622269118608 && point.longitude <-81.30236964672804 ){
                                points.add(point);
                            }
                        }
                    }
                    else if(Destination.longitude < -81.3025004044175)
                    {
                        for(LatLng point : Q3Building2UP){
                            if(point.latitude > 28.596147036189763 && point.latitude < 28.59622269118608 && point.longitude < -81.30236964672804 &&point.longitude > -81.30248330533504 && point.latitude >= Destination.latitude){
                                points.add(point);
                            }
                        }
                    } else if (Destination.longitude  > -81.3025004044175 && Destination.latitude > 28.59595156933239) {
                        for(LatLng point : Q3Building2UP){
                            if(point.latitude > 28.596147036189763 && point.latitude < 28.59622269118608 && point.longitude > -81.30245681852102 && point.longitude < -81.30234517157078&& point.latitude >= Destination.latitude){
                                points.add(point);
                            }
                        }
                    } else if (Destination.longitude <-81.30233678966759){
                        points.add(Q3Building2UP.get(0));
                        points.add(Q3Building2UP.get(4));
                        points.add(Q3Building2UP.get(5));
                        points.add(Q3Building2UP.get(6));
                        for(int i = 9 ; i < Q3Building2UP.size(); i++){
                             LatLng point = Q3Building2UP.get(i);
                            if(point.latitude > 28.596147036189763 && point.latitude < 28.59622269118608 && point.longitude > -81.30245681852102){
                                points.add(point);
                            }
                        }
                    }
                    else {
                        points.add(Q3Building2UP.get(0));
                        points.add(Q3Building2UP.get(4));
                        points.add(Q3Building2UP.get(5));
                        points.add(Q3Building2UP.get(6));
                        for(int i = 9 ; i < Q3Building2UP.size(); i++){
                            LatLng point = Q3Building2UP.get(i);
                            if(point.latitude > 28.596147036189763 && point.latitude < 28.59622269118608 && point.longitude > -81.30245681852102 && point.latitude >= Destination.latitude){
                                points.add(point);
                            }
                        }
                    }
                }

            }

        } else if (QuadrantDestination.equals("Q4")) {


            if(rightside){

                for (LatLng point: Q3Building2UP){
                    if(point.longitude > -81.30248330533504 &&
                            point.latitude > 28.596147036189763 && point.latitude <28.59622269118608 ){
                        points.add(point);
                    }
                }
                for (int i = 9; i <Q3Building2UP.size();i++){
                    LatLng point = Q1Building2UP.get(i);
                    if(point.latitude>=28.595911239528938 && point.longitude > -81.30245681852102){

                        points.add(point);
                    }
                }
                if(Destination.longitude <-81.30211886018515 ){
                    points.add(Q4Building2UP.get(0));
                    points.add(Q4Building2UP.get(1));
                }else{
                    for(LatLng point1: Q4Building2UP){
                        if(point1.longitude <= Destination.longitude){
                            points.add(point1);
                        }
                    }
                }

            }else{

                points.add(Q3Building2UP.get(0));
                points.add(Q3Building2UP.get(4));
                points.add(Q3Building2UP.get(5));
                points.add(Q3Building2UP.get(6));

                for (int i = 9; i <Q3Building2UP.size();i++){
                    LatLng point = Q1Building2UP.get(i);
                    if(point.latitude>=28.595911239528938 && point.longitude > -81.30245681852102){

                        points.add(point);
                    }
                }
                if(Destination.longitude <-81.30211886018515 ){
                    points.add(Q4Building2UP.get(0));
                    points.add(Q4Building2UP.get(1));
                }else{
                    for(LatLng point1: Q4Building2UP){
                        if(point1.longitude <= Destination.longitude){
                            points.add(point1);
                        }
                    }
                }
            }




        }
        return points;
    }


    //Victoria Building 2

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
            new LatLng(28.59585177511897,-81.30226135253905),
            new LatLng(28.59585236387567,-81.30220972001553),
            new LatLng(28.595854718902423,-81.30219262093307),
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
            new LatLng(28.59594891992973,-81.30205381661654),
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

        double latToCheck5=28.595936556049697;
        double longToCheckQ5 = -81.30215071141718;

        if (_latitdue > latToCheckQ1) {
            return "Q1";
        } else if (_latitdue > latToCheckQ2) {
            return "Q2";
        } else if (_latitdue > latToCheckQ3 && _longitude < longToCheckQ5) {
            return "Q3";

        } else if (_latitdue > latToCheckQ4 && _longitude < longToCheckQ5) {
            return "Q4";
        }
        else if(_longitude > longToCheckQ5){
            return "Q5";
        }else {
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
            if(rightside) {
                //top hallway
                if (Destination.latitude > 28.595894754349356 && Destination.longitude > -81.30226403474809) {
                    for (LatLng point: Q3Building3Down) {
                        if(point.longitude >= -81.30242429673672)
                        {
                            points.add(point);
                        }
                    }

                    for (LatLng point: Q4Building4Down){

                        if(point.latitude > 28.595894754349356 && point.longitude > -81.30226403474809){
                            if(point.longitude < Destination.longitude){
                                points.add(point);
                            }


                        }
                    }
                }
                //bot hallway
                else {
                    //add start point of hallways for right side hallway
                    for (LatLng point: Q3Building3Down) {
                        if(point.latitude >= 28.5961888377898 && point.longitude >= -81.30239512771368)
                        {
                            points.add(point);
                        }
                    }
                    for (int i = Q3Building3Down.size() - 1; i >= 0;i--){

                        LatLng point = Q3Building3Down.get(i);
                        if(point.latitude < 28.596184422128612 && point.longitude > -81.30253527313471 && point.longitude < -81.30239512771368
                        && point.latitude > 28.596126724138692){
                            points.add(point);
                        }
                    }
                    for (LatLng point: Q3Building3Down)
                    {
                        if(point.latitude <= 28.596126724138692 && point.longitude < -81.30239512771368){
                            points.add(point);
                        }
                    }
                    if(Destination.latitude >28.595861783982404 && Destination.longitude < -81.30230896174908){

                        for (LatLng point: Q4Building4Down) {
                            if(Destination.latitude <= point.latitude){
                                points.add(point);
                            }
                        }
                    }
                    else if(Destination.latitude <28.595861783982404 && Destination.longitude <  -81.30230896174908){

                        if(Destination.latitude > 28.595829991118773){

                            for (LatLng point: Q4Building4Down){

                                if(point.longitude <= -81.30230896174908 && point.latitude >=28.595829991118773){
                                    points.add(point);
                                }else{
                                    break;
                                }
                            }

                        }else{
                            for (LatLng point: Q4Building4Down){

                                if(point.longitude <= -81.30230896174908){
                                    points.add(point);
                                }
                            }

                        }

                    }else{


                        for (LatLng point: Q4Building4Down){
                            if (point.latitude > 28.595861783982404 && point.longitude <= -81.30230896174908){
                                points.add(point);
                            }
                        }
                        for (LatLng point: Q4Building4Down){
                            if (point.longitude >= -81.30230896174908 && point.latitude < 28.595894754349356){
                                if(point.longitude <= Destination.longitude){
                                    points.add(point);
                                }

                            }
                        }
                    }

                }
            }else{
                //top hallway right side
                if (Destination.latitude > 28.595894754349356 && Destination.longitude > -81.30226403474809) {
                    for (LatLng point: Q3Building3Down) {
                        if(point.latitude < 28.596174707673327 && point.latitude > 28.596126724138692 && point.longitude<-81.30239512771368)
                        {
                            points.add(point);
                        }
                    }
                    for(LatLng point2: Q3Building3Down){
                        if(point2.latitude <=28.5961888377898 && point2.longitude >=-81.30239512771368)
                        {
                            points.add(point2);
                        }
                    }
                    for (LatLng point: Q4Building4Down){

                        if(point.latitude > 28.595894754349356 && point.longitude > -81.30226403474809){
                            if(point.longitude < Destination.longitude){
                                points.add(point);
                            }


                        }
                    }
                }
                //bot hallway left side
                else{
                    for (LatLng point: Q3Building3Down )
                    {
                        if(point.latitude < 28.596162638197345 && point.longitude < -81.30239512771368)
                        {
                            points.add(point);
                        }
                    }
                    if(Destination.latitude >28.595861783982404 && Destination.longitude < -81.30230896174908){

                        for (LatLng point: Q4Building4Down) {
                            if(Destination.latitude <= point.latitude){
                                points.add(point);
                            }
                        }
                    }
                    else if(Destination.latitude <28.595861783982404 && Destination.longitude <  -81.30230896174908){

                        if(Destination.latitude > 28.595829991118773){

                            for (LatLng point: Q4Building4Down){

                                if(point.longitude <= -81.30230896174908 && point.latitude >=28.595829991118773){
                                    points.add(point);
                                }else{
                                    break;
                                }
                            }

                        }else{
                            for (LatLng point: Q4Building4Down){

                                if(point.longitude <= -81.30230896174908){
                                    points.add(point);
                                }
                            }

                        }

                    }else{


                        for (LatLng point: Q4Building4Down){
                            if (point.latitude > 28.595861783982404 && point.longitude <= -81.30230896174908){
                                points.add(point);
                            }
                        }
                        for (LatLng point: Q4Building4Down){
                            if (point.longitude >= -81.30230896174908 && point.latitude < 28.595894754349356){
                                if(point.longitude <= Destination.longitude){
                                    points.add(point);
                                }

                            }
                        }
                    }
                }
            }

        }else if(QuadrantDestination.equals("Q5")){

            if(rightside){
                for (LatLng point: Q3Building3Down) {
                    if(point.longitude >= -81.30242429673672)
                    {
                        points.add(point);
                    }
                }
                for (LatLng point: Q4Building4Down){
                        if(point.longitude < Destination.longitude){
                            points.add(point);
                        }
                }

                if(Destination.latitude < 28.595902408183058 && Destination.longitude < -81.30203671753407){

                    points.add(Q5Building5Down.get(0));
                    points.add(Q5Building5Down.get(1));
                }else if(Destination.latitude > 28.59594891992973 && Destination.longitude < -81.30205381661654){
                    points.add(Q5Building5Down.get(0));
                    points.add(Q5Building5Down.get(1));
                    points.add(Q5Building5Down.get(2));
                } else if (Destination.latitude >28.596037527693976 && Destination.longitude > -81.30190093070269) {
                    for (LatLng point: Q5Building5Down){
                        if(point.latitude >28.595902408183058 ) {
                            if(point.longitude < Destination.longitude){
                                points.add(point);
                            }

                        }
                    }
                } else if (Destination.longitude < -81.30190093070269 && Destination.latitude > 28.595950391820107) {
                    for (LatLng point:  Q5Building5Down) {
                        if(point.latitude < 28.596056367873416 && point.longitude < -81.30197033286095 && point.latitude > 28.595902408183058){
                            points.add(point);
                        }
                    }
                } else if (Destination.longitude > -81.30190093070269 && Destination.latitude > 28.596016332488063) {
                    for (LatLng point:  Q5Building5Down) {
                        if(point.latitude > 28.596056367873416){
                            if(point.longitude < Destination.longitude) {
                                points.add(point);
                            }
                        }
                    }
                } else if (Destination.latitude > 28.595930668487295 && Destination.longitude >-81.3018412515521 ) {
                    for (LatLng point:  Q5Building5Down) {
                        if(point.latitude > 28.596056367873416){
                            if(point.longitude <= -81.30190093070269) {
                                points.add(point);
                            }
                        }
                    }
                    for (LatLng point: Q5Building5Down){
                        if(point.longitude > -81.30190093070269 && point.latitude > 28.595930668487295){
                            points.add(point);
                        }
                    }
                } else if (Destination.latitude < 28.595930668487295 && Destination.longitude >-81.3018412515521) {
                    for (LatLng point:  Q5Building5Down) {
                        if(point.latitude > 28.596056367873416){
                            if(point.longitude <= -81.30190093070269) {
                                points.add(point);
                            }
                        }
                    }
                    for (LatLng point: Q5Building5Down){
                        if(point.longitude > -81.30190093070269 && point.latitude > 28.595930668487295){
                            points.add(point);
                        }
                    }
                }else{
                    for (LatLng point:  Q5Building5Down) {
                        if(point.latitude > 28.596056367873416){
                            if(point.longitude <= -81.30190093070269) {
                                points.add(point);
                            }
                        }
                    }
                    for (LatLng point: Q5Building5Down){
                        if(point.longitude > -81.30190093070269){
                            points.add(point);
                        }
                    }
                }

            }else{
                for (LatLng point: Q3Building3Down) {
                    if(point.latitude < 28.596174707673327 && point.latitude > 28.596126724138692 && point.longitude<-81.30239512771368)
                    {
                        points.add(point);
                    }
                }
                for(LatLng point2: Q3Building3Down){
                    if(point2.latitude <=28.5961888377898 && point2.longitude >=-81.30239512771368)
                    {
                        points.add(point2);
                    }
                }
                for (LatLng point: Q4Building4Down){

                    if(point.latitude > 28.595894754349356 && point.longitude > -81.30226403474809){
                            points.add(point);
                    }
                }
                if(Destination.latitude < 28.595902408183058 && Destination.longitude < -81.30203671753407){

                    points.add(Q5Building5Down.get(0));
                    points.add(Q5Building5Down.get(1));
                }else if(Destination.latitude > 28.59594891992973 && Destination.longitude < -81.30205381661654){
                    points.add(Q5Building5Down.get(0));
                    points.add(Q5Building5Down.get(1));
                    points.add(Q5Building5Down.get(2));
                } else if (Destination.latitude >28.596037527693976 && Destination.longitude > -81.30190093070269) {
                    for (LatLng point: Q5Building5Down){
                        if(point.latitude >28.595902408183058 ) {
                            if(point.longitude < Destination.longitude)
                                points.add(point);
                        }
                    }
                } else if (Destination.longitude < -81.30190093070269 && Destination.latitude > 28.595950391820107) {
                    for (LatLng point:  Q5Building5Down) {
                        if(point.latitude < 28.596056367873416 && point.longitude < -81.30197033286095 && point.latitude > 28.595902408183058){
                            points.add(point);
                        }
                    }
                } else if (Destination.longitude > -81.30190093070269 && Destination.latitude > 28.596016332488063) {
                    for (LatLng point:  Q5Building5Down) {
                        if(point.latitude > 28.596056367873416){
                            if(point.longitude < Destination.longitude) {
                                points.add(point);
                            }
                        }
                    }
                } else if (Destination.latitude > 28.595930668487295 && Destination.longitude >-81.3018412515521 ) {
                    for (LatLng point:  Q5Building5Down) {
                        if(point.latitude > 28.596056367873416){
                            if(point.longitude <= -81.30190093070269) {
                                points.add(point);
                            }
                        }
                    }
                    for (LatLng point: Q5Building5Down){
                        if(point.longitude > -81.30190093070269 && point.latitude > 28.595930668487295){
                            points.add(point);
                        }
                    }
                } else if (Destination.latitude < 28.595930668487295 && Destination.longitude >-81.3018412515521) {
                    for (LatLng point:  Q5Building5Down) {
                        if(point.latitude > 28.596056367873416){
                            if(point.longitude <= -81.30190093070269) {
                                points.add(point);
                            }
                        }
                    }
                    for (LatLng point: Q5Building5Down){
                        if(point.longitude > -81.30190093070269 && point.latitude > 28.595930668487295){
                            points.add(point);
                        }
                    }
                }else{
                    for (LatLng point:  Q5Building5Down) {
                        if(point.latitude > 28.596056367873416){
                            if(point.longitude <= -81.30190093070269) {
                                points.add(point);
                            }
                        }
                    }
                    for (LatLng point: Q5Building5Down){
                        if(point.longitude > -81.30190093070269){
                            points.add(point);
                        }
                    }
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
    public ArrayList<LatLng> ChoosePointsToGrabOutsideToInside3C(String QuadrantDestination, LatLng Destination) {
        ArrayList<LatLng> points = new ArrayList<>();
        //all of these paths only consider travel from outside to inside. Not travel withing the building.
        //Within building travel will require different logic because there may be shortcuts to be taken within the building.

        if (QuadrantDestination.equals("Q1")) {

        } else if (QuadrantDestination.equals("Q2")) {

        } else if (QuadrantDestination.equals("Q3")) {

        } else if (QuadrantDestination.equals("Q4")) {

        }
        return points;
    }
    ArrayList<LatLng> Q13A = new ArrayList<>(Arrays.asList(new LatLng(28.595617744069802,-81.30403161048888),new LatLng(28.595562400802088,-81.30402825772762),new LatLng(28.595628341713468,-81.30408894270658)));
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
            for (LatLng point: Q13A) {
                if(point.longitude>-81.30404636263847)
                {
                    if(Destination.latitude < 28.595605674529853 && Destination.longitude < -81.30404636263847 && point.latitude > 28.595520598952945) {
                        points.add(point);
                    }
                    else
                    {
                        if(point.latitude < Destination.latitude)
                        {
                            points.add(point);
                        }
                    }
                }
            }
        } else if (QuadrantDestination.equals("Q2")) {

        } else if (QuadrantDestination.equals("Q3")) {

        } else if (QuadrantDestination.equals("Q4")) {

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
        } else if (ThreeBMarkers.contains(marker)) {
            if (SearchType == 1) {
                returnArea = new LatLng(28.59504105401512, -81.30434174090624);
            } else {
                returnArea = new LatLng(-81.30386296659708, 28.595124658078248);
            }
        } else if (ThreeCMarkers.contains(marker)) {
            if(SearchType == 1)
            {
                returnArea = new LatLng(28.594441398979445,-81.30426932126284);
            }else{
                returnArea = new LatLng(28.594457001240205,-81.30404971539974);
            }
        } else if (ThreeDMarkers.contains(marker)) {
//            return "3D";
        } else if (ThreeEMarkers.contains(marker)) {
//            return "3E";
        } else if (ThreeFMarkers.contains(marker)) {
//            return "3F";
        } else if (FourAMarkers.contains(marker)) {
//            return "4A";
        } else if (BuildingOne.contains(marker)) {

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
                } else if (Latitude > -81.30249939858913) {
                    return new LatLng(28.596334260127957, -81.302160769701);
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

    public boolean CheckResultLoadType(String Result) {
        boolean wasFound = false;
        if (!Result.equals("b3u") && !Result.equals("b3d") && !Result.equals("b4u") && !Result.equals("b4d") && !Result.equals("b1") && !Result.equals("b2")) {
            for (int i = 0; i < resultsList.size(); i++) {
                if (resultsList.get(i).equals(Result)) {
                    wasFound = true;
                    break;
                }
            }
            if (!wasFound) {
                resultsList.add(Result);
            }
        } else {
            switch (Result) {
                case "b3u":

                    if (B3U.size() >= 3 && resultsList.contains("3A") && resultsList.contains("FishBowl") && resultsList.contains("3BConnected")) {
                        if (!resultsList.contains("b3u")) {

                            resultsList.add("b3u");
                        }
                        wasFound = true;
                    }
                    break;
                case "b3d":

                    if (B3D.size() >= 4 && resultsList.contains("3C") && resultsList.contains("3D") && resultsList.contains("3E") && resultsList.contains("3F")) {
                        if (!resultsList.contains("b3d")) {
                            resultsList.add("b3d");
                        }
                        wasFound = true;
                    }
                    break;
                case "b4u":
                    if (B4U.size() == 4) {
                        if (!resultsList.contains("b4u")) {
                            resultsList.add("b4u");
                        }
                        wasFound = true;
                    }
                    break;
                case "b4d":
                    if (B4D.size() >= 3 && resultsList.contains("4D") && resultsList.contains("4C") && resultsList.contains("4E")) {
                        if (!resultsList.contains("b4d")) {
                            resultsList.add("b4d");
                        }
                        wasFound = true;
                    }
                    break;
                case "b1":
                    if (B1.size() == 2) {
                        if (!resultsList.contains("b1")) {
                            resultsList.add("b1");
                        }
                        wasFound = true;
                    }
                    break;
                case "b2":
                    if (B2.size() == 2) {
                        if (!resultsList.contains("b2")) {
                            resultsList.add("b2");
                        }
                        wasFound = true;
                    }
            }
        }
        return wasFound;
    }

    public void doTheClick(ArrayList<Marker> ListToClick) {
        if (isNOTfUCKED) {

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

                getDirectionPoly(markerFragment.MTouch.marker2);
                isTraveling = true;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Latitude, Longitued), 20f));
                // checkDistPoint = new LatLng(Latitude, Longitued);
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
                break;

            case R.id.NavDone:
                slideupview.setVisibility(View.GONE);
                markerFragment.MTouch.slideup = false;
                Filter.setEnabled(true);
                wasMarkerClicked = false;
                isTraveling = false;
                FollowUser = false;
                //Remove all lines
                RemoveAllLines();
                for (Marker m : createdMarkers) {
                    m.setVisible(true);
                }
//                checkIfMarkerNeedVisible();
                onMapClick(new LatLng(28.595085, -81.308305));
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

                for (int i = 0; i < favoritedMarkers.size(); i++) {
                    if (favoritedMarkers.get(i).getTitle().equals(markerFragment.MTouch.createdMarker.getTitle())) {
                        Favorites.removeFromFavorite(MapsActivity.this, markerFragment.MTouch.createdMarker);
                        favoritedMarkers.remove(i);
                    }
                }

                for (int i = 0; i < createdMarkers.size(); i++) {
                    if (createdMarkers.get(i).getTitle().equals(markerFragment.MTouch.createdMarker.getTitle())) {
                        createdMarkers.get(i).remove();
                        CustomMarker.removeFromCustomMarkers(MapsActivity.this, markerFragment.MTouch.createdMarker);
                        createdMarkers.get(i).setVisible(false);
                        CustomMarker.removeFromCustomMarkers(MapsActivity.this, markerFragment.MTouch.createdMarker);
                        createdMarkers.remove(i);
                        markerFragment.MTouch.createdMarker.remove();
                        markerFragment.MTouch.createdMarker = null;
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
                            newMarker.showInfoWindow();
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(LongClickPoint));
                            createdMarkers.add(newMarker);
                        } else {
                            name = "CustomMarkerNumber0" + createdMarkers.size();
                            newMarker = mMap.addMarker(new MarkerOptions().position(LongClickPoint).title(name));
                            newMarker.showInfoWindow();
                            createdMarkers.add(newMarker);
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

                snack = Snackbar.make(findViewById(R.id.map), "Added To Favorites", Snackbar.LENGTH_SHORT);
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
                removeAllOverlays();
                String result = DoTheChecks();
                CheckResults(result);
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
                removeAllOverlays();
                String results = DoTheChecks();
                CheckResults(results);
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


    @Override
    public boolean onMarkerClick(Marker marker) {
        dest = FindMarkerAreaForTravel(markerFragment.MTouch.marker2, 1);
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
                PolylineOptions polylineOptions = null;

                for (List<HashMap<String, String>> path : lists) {
                    points = new ArrayList();
                    polylineOptions = new PolylineOptions();

                    for (HashMap<String, String> point : path) {
                        double lat = Double.parseDouble(point.get("lat"));
                        double lon = Double.parseDouble(point.get("lon"));

                        points.add(new LatLng(lat, lon));
                    }
//                    LatLng PosToCheck = FindMarkerAreaForTravel(marker,2);
//                    String area = FindQuadrantForArea(marker.getPosition(),PosToCheck);
//                    points.addAll(ChoosePointsToGrabOutsideToInsideFishBowl(area, marker.getPosition()));

                    LatLng PosToCheck = FindMarkerAreaForTravel(marker, 2);
                    String area = FindQuadrantForAreaBuilding2(marker.getPosition());
                    points.addAll(ChoosePointsToGrabOutsideToInsideBuilding2(area, marker.getPosition()));

                    polylineOptions.addAll(points);
                    polylineOptions.width(15);
                    if (DarkorLight) {
                        polylineOptions.color(Color.BLUE);
                    } else {
                        polylineOptions.color(Color.parseColor("#FFA500"));
                    }
                    polylineOptions.geodesic(true);
                }
                Polyline poly = mMap.addPolyline(new PolylineOptions().addAll(points).color(Color.parseColor("#22808080")).width(15));
                linesShowing.add(poly);
                if (polylineOptions != null) {
//                    if (!CheckMarkerType(marker)) {
//                        List<LatLng> outToInPoly = customPolyLines.get(0).getPoints();
//                        polylineOptions.add(outToInPoly.get(0));
//                    }
//                    String outToPolys = "outsideTo" + marker.getTitle();
//                    for (int i = 0; i < LinesTitles.size(); i++) {
//                        if (LinesTitles.get(i).equals(outToPolys)) {
//                            linesShowing.add(mMap.addPolyline(customPolyLines.get(i)));
//                        }
//                    }
                    linesShowing.add(mMap.addPolyline(polylineOptions));
                } else {
                    snack = Snackbar.make(findViewById(R.id.map), "Directions Not Found", Snackbar.LENGTH_SHORT);
                    snack.show();
                }
            }
        }

        private class Overlays extends AsyncTask<Void, Void, String> {
            GoogleMap maps;

            public void sendMap(GoogleMap _maps) {
                maps = _maps;
            }

            @Override
            protected String doInBackground(Void... voids) {

                return "succ";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

            }
        }
    }
}
