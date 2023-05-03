package com.example.FSMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
    FirebaseDatabase databaseReference;
    CustomMarkerAdapter customMarkerAdapter;
    ArrayList<CustomMarkersList> list;
    FirebaseAuth firebaseAuth;
    CustomMarkersList customMarkersList;
    List<String> markerList;
    ImageView markerCUstomMarkers;
    Button rmvallcustom;
    MapsActivity mapsActivity = new MapsActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custommarkers);

        recyclerView = findViewById(R.id.markersRV1);
        // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Favorites");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<CustomMarkersList>();

        customMarkerAdapter = new CustomMarkerAdapter(this, list, mapsActivity);
        recyclerView.setAdapter(customMarkerAdapter);


        Context c = this;
        rmvallcustom = findViewById(R.id.rmvallcustom);
        rmvallcustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllFromCustomMarkers(c);
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/Users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/CustomMarkers/");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();
                String markerTitle = null;
                String originalName = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if(dataSnapshot != null){
                        markerTitle = dataSnapshot.getKey().toString();
                        originalName =dataSnapshot.getKey().toString();
                    }

                    int floorCustom = Integer.parseInt(dataSnapshot.child("Floor").getValue().toString());
                    double latitude1 = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                    double longitude1 = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());
                    LatLng positionCustom = new LatLng(latitude1, longitude1);

                    customMarkersList = new CustomMarkersList(markerTitle, originalName, floorCustom, positionCustom);
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
                onBackPressed();
            }
        });


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }


    public static void addToCustomMarkers(Context context, Marker marker, int floor) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(context, "You're not logged in", Toast.LENGTH_SHORT).show();
        } else {
            //Set up the data to add in firebase of current user for marker book
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(marker.getTitle(), marker.getPosition());
            HashMap<String, Object> hashMap1 = new HashMap<>();
            hashMap1.put("Floor", floor);
            HashMap<String,Object> hashMap2 = new HashMap<>();
            hashMap2.put("CustomName",marker.getTitle());

            //Save to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("CustomMarkers").updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(context, "Added to Markers.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Failed to add to you Marker list due to " + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
            ref.child(firebaseAuth.getUid()).child("CustomMarkers").child(marker.getTitle()).updateChildren(hashMap2).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(context, "Added to Markers.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Failed to add to you Marker list due to " + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
            ref.child(firebaseAuth.getUid()).child("CustomMarkers").child(marker.getTitle()).updateChildren(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(context, "Added to Markers.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Failed to add to you Marker list due to " + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    public static void renameCUstomMarkers(Context context,String titleCustom, String OriginalCustomTitle, int floor, LatLng position) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(context, "You're not logged in", Toast.LENGTH_SHORT).show();
        } else {
            //Set up the data to add in firebase of current user for marker book
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put(OriginalCustomTitle, position);
            HashMap<String, Object> hashMap1 = new HashMap<>();
            hashMap1.put("Floor", floor);
            HashMap<String,Object> hashMap2 = new HashMap<>();
            hashMap2.put("CustomName",titleCustom);

            //Save to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

//            ref.child(firebaseAuth.getUid()).child("CustomMarkers").child(OriginalCustomTitle).setValue(titleCustom).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void unused) {
//                    Toast.makeText(context, "Added to Markers.", Toast.LENGTH_SHORT).show();
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Toast.makeText(context, "Failed to add to you Marker list due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
//
//                }
//            });

            ref.child(firebaseAuth.getUid()).child("CustomMarkers").child(OriginalCustomTitle).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(context, "Added to Markers.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Failed to add to you Marker list due to " + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

            ref.child(firebaseAuth.getUid()).child("CustomMarkers").child(OriginalCustomTitle).updateChildren(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(context, "Added to Markers.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Failed to add to you Marker list due to " + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
            ref.child(firebaseAuth.getUid()).child("CustomMarkers").child(OriginalCustomTitle).updateChildren(hashMap2).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(context, "Added to Markers.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Failed to add to you Marker list due to " + e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });


        }
    }

    public static void removeFromCustomMarkers(Context context, Marker marker) {
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

    public static void removeFromCustom(Context context, String title) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(context, "You're not logged in", Toast.LENGTH_SHORT).show();
        } else {
            //Save to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("CustomMarkers").child(title)
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

    public static void removeAllFromCustomMarkers(Context context) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(context, "You're not logged in", Toast.LENGTH_SHORT).show();
        } else {
            //Save to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("CustomMarkers")
                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Removed all Custom Markers.", Toast.LENGTH_SHORT).show();
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