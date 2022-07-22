package com.example.logintesting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomMarker extends AppCompatActivity {


    RecyclerView recyclerView;
    FirebaseDatabase databaseReference ;
    CustomMarkerAdapter customMarkerAdapter;
    ArrayList<CustomMarkersList> list ;
    FirebaseAuth firebaseAuth;
    CustomMarkersList customMarkersList ;
    List<String> markerList;
    ImageView markerCUstomMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custommarkers);

        recyclerView = findViewById(R.id.markersRV1);
        // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Favorites");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<CustomMarkersList>();

        customMarkerAdapter = new CustomMarkerAdapter(this,list);
        recyclerView.setAdapter(customMarkerAdapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/Users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/CustomMarkers/");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String markerTitle = dataSnapshot.getKey().toString();
                    customMarkersList = new CustomMarkersList(markerTitle);
                    list.add(customMarkersList);
                }
                customMarkerAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        markerCUstomMarkers = findViewById(R.id.backBTN);
        markerCUstomMarkers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomMarker.this,Settings.class));
            }
        });



    }

    public static void addToCustomMarkers(Context context, Marker marker){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser()==null){
            Toast.makeText(context, "You're not logged in", Toast.LENGTH_SHORT).show();
        }else{
            //Set up the data to add in firebase of current user for marker book
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put(marker.getTitle(), marker.getPosition());

            //Save to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("CustomMarkers")/*child(marker.getTitle())*/.updateChildren(hashMap)./*.
                     setValue(hashMap).*/addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(context, "Added to Markers.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Failed to add to you Marker list due to "+e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
    public static void removeFromCustomMarkers(Context context,Marker marker) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(context, "You're not logged in", Toast.LENGTH_SHORT).show();
        } else {
            //Save to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("CustomMarkers").child(marker.getTitle())
                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Removed from Custom Markers.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Failed to remove from your Marker from list due to " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }
}