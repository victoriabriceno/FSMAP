package com.example.FSMap;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

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
import java.util.Objects;
import java.util.logging.Handler;

public class MarkerTouch extends FrameLayout {

    private static final String FINE_lOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;


    //Change this to change clickable radius in pixels
    private static final int ClickRadius = 50;

    private GoogleMap mGoogleMap;
    public List<Marker> mMarkers, Favs, CM, AM, ThreeA, ThreeB;
    ArrayList<Marker> markersClicked = new ArrayList<>();
    public Marker createdMarker, marker2;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean mLocationPermissionsGranted, wasRemoveHit, csvReady, cReady, fReady = false;
    public boolean markerready, wasMarkerClicked = false;
    private Context C;
    private Activity A;
    private double Latitude, Longitude;
    int clickCount = 0;
    List<Polyline> linesShowing;
    MapsActivity mapsActivity = new MapsActivity();

    boolean slideup = mapsActivity.slidepup;

    public Button FilterMarker;

    LinearLayout slideupview;

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

    // This method insert the image of the Slide up window for the markers
    protected String getMarkerImage(Marker m) {

        String title = Objects.requireNonNull(m.getTitle());

        Bitmap building1Bitmap = decodeAndScaleBitmapFromResource(getResources(), R.drawable.bulding1, 1000, 800);
        //Bitmap building1 = Bitmap.createScaledBitmap(building1Bitmap, 1000, 800, false);
        Bitmap building2Bitmap = decodeAndScaleBitmapFromResource(getResources(), R.drawable.building2,1000,800);
        Bitmap buildingcBitmap = decodeAndScaleBitmapFromResource(getResources(), R.drawable.buildingc,1000,800);
        Bitmap buildingdBitmap = decodeAndScaleBitmapFromResource(getResources(), R.drawable.buildingd,1000,800);
        Bitmap libraryBitmap = decodeAndScaleBitmapFromResource(getResources(), R.drawable.library,1000,800);
        Bitmap buildingfBitmap = decodeAndScaleBitmapFromResource(getResources(), R.drawable.buildingf,1000,800);
        Bitmap campusExplorationMapBitmap = decodeAndScaleBitmapFromResource(getResources(), R.drawable.campusexplorermap,1000,800);
        Bitmap fs1bitmap= decodeAndScaleBitmapFromResource(getResources(), R.drawable.fs1,1000,800);
        Bitmap fs2bitmap= decodeAndScaleBitmapFromResource(getResources(), R.drawable.fs2,1000,800);


        ImageView image = slideupview.findViewById(R.id.imageForMarkers);
        // It looks for an specific marker for the image
        if (title.contains("FS3A")) {
            image.setImageBitmap(building1Bitmap);
        } else if (title.contains("FS3B")) {
            image.setImageBitmap(building2Bitmap);
        } else if (title.contains("FS3C")) {
            image.setImageBitmap(buildingcBitmap);
        } else if (title.contains("FS3D")) {
            image.setImageBitmap(buildingdBitmap);
        } else if (title.contains("FS3E")) {
            image.setImageBitmap(libraryBitmap);
        } else if (title.contains("FS3F")) {
            image.setImageBitmap(buildingfBitmap);
        }else if(title.contains("FS1")){
            image.setImageBitmap(fs1bitmap);
        }else if(title.contains("FS2")){
            image.setImageBitmap(fs2bitmap);
        } else {
            image.setImageBitmap(campusExplorationMapBitmap);
        }

        return "Not found";
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
                if (marker.isVisible()) {
                    wasMarkerClicked = true;
                    getLocationPermission();
                    //If you have location, get device location
                    if (mLocationPermissionsGranted) {
                        getDeviceLocation();
                    }
                    HideMarkersExcept(marker);
                    if (!wasRemoveHit) {
                        wasRemoveHit = true;
                    } else {
                        marker.remove();
                    }

                    //Favorites star; is filled in or is empty
                    ImageButton bntFavoritesRemove = A.findViewById(R.id.btnRemoveFavorites);
                    ImageButton btnFavoritesAdd = A.findViewById(R.id.btnAddFavorites);

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
                    slideupview = A.findViewById(R.id.design_bottom_sheet);
                    bottomSheetBehavior = BottomSheetBehavior.from(slideupview);
                    TextView text = slideupview.findViewById(R.id.roomnumber);
                    text.setText(marker.getTitle());

                    getMarkerImage(marker);


                    if (!slideup) {
                        slideupview.setVisibility(View.VISIBLE);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        bottomSheetBehavior.setFitToContents(false);
                        FilterMarker.setEnabled(false);
                        slideup = true;


                    } else {
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
                        //from.setBackgroundColor(Color.TRANSPARENT);
                        To.setEnabled(false);
                        To.setText(marker.getTitle());
                        To.setFocusable(false);
                        //To.setBackgroundColor(Color.TRANSPARENT);
                    }
                    //hide markers after one is clicked
//                for (Marker m : AM)
//                {
//                    if(!m.getTitle().equals(marker.getTitle())) {
//                        m.setVisible(false);
//                    }
//                }
                    marker2 = marker;
                    markerready = true;


                } else {
                    return true;
                }

            }
        }

        return super.dispatchTouchEvent(event);
    }

    public void ManualTouch(Marker marker) {
        if (!marker.isVisible()) {
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

        //Favorites star; is filled in or is empty
        ImageButton bntFavoritesRemove = A.findViewById(R.id.btnRemoveFavorites);
        ImageButton btnFavoritesAdd = A.findViewById(R.id.btnAddFavorites);

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


        if (!slideup) {
            slideupview.setVisibility(View.VISIBLE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            bottomSheetBehavior.setFitToContents(false);
            FilterMarker.setEnabled(false);
            slideup = true;


        } else {
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
            To.setEnabled(false);
            To.setText(marker.getTitle());
            To.setFocusable(false);

        }
        //hide markers after one is clicked
//                for (Marker m : AM)
//                {
//                    if(!m.getTitle().equals(marker.getTitle())) {
//                        m.setVisible(false);
//                    }
//                }
        marker2 = marker;
        markerready = true;
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
        if (Favs != null) {
            for (int i = 0; i < Favs.size(); i++) {
                if (Favs.get(i).getTitle().equals(marker.getTitle())) {
                    return true;
                }
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

    public void HideMarkersExcept(Marker m) {
        for (Marker marker : CM) {
            if (!marker.equals(m)) {
                marker.setVisible(false);
            }
        }
        for (Marker marker : AM) {
            if (!marker.equals(m)) {
                marker.setVisible(false);
            }
        }
        for (Marker marker : Favs) {
            if (!marker.equals(m)) {
                marker.setVisible(false);
            }
        }
        for (Marker marker : mMarkers) {
            if (!marker.equals(m)) {
                marker.setVisible(false);
            }
        }
    }

    public void moveCamera(LatLng latLng) {

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


