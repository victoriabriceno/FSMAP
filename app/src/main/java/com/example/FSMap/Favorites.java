package com.example.FSMap;


//Favorites Screen
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class Favorites extends AppCompatActivity {

 RecyclerView recyclerView;
 FirebaseDatabase databaseReference ;
 public static AdapterUserFavoriteList adapterUserFavoriteList;
 public static ArrayList<UserFavoriteList> list ;
 FirebaseAuth firebaseAuth;
    UserFavoriteList userFavoriteList ;
    List<String>markerList;
    ImageView backFavorites;
    MapsActivity mapsActivity;
    public static HashMap<String,Object> markerEditHash = new HashMap<>();


     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerView = findViewById(R.id.markersRV);
       // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Favorites");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

       list = new ArrayList<UserFavoriteList>();
       backFavorites = findViewById(R.id.backBTN);
       backFavorites.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(Favorites.this,Settings.class));
           }
       });

       adapterUserFavoriteList = new AdapterUserFavoriteList(this,list,mapsActivity);
        recyclerView.setAdapter(adapterUserFavoriteList);

       DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/Users/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"/Favorites/");
         databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                list.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){

                    String marker = dataSnapshot.getValue().toString();
                    String originalName = dataSnapshot.getKey().toString();
                    userFavoriteList = new UserFavoriteList(marker,originalName);
                    list.add(userFavoriteList);
                }

                adapterUserFavoriteList.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });







    }

    public static void addToFavorite(Context context,Marker marker){
         FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
         if (firebaseAuth.getCurrentUser()==null){
             Toast.makeText(context, "You're not logged in", Toast.LENGTH_SHORT).show();
         }else{
             //Set up the data to add in firebase of current user for favorite book
             HashMap<String,Object> hashMap = new HashMap<>();
             hashMap.put(marker.getTitle(), "" +marker.getTitle());


             //Save to db
             DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

             ref.child(firebaseAuth.getUid()).child("Favorites")/*child(marker.getTitle())*/.updateChildren(hashMap)./*.
                     setValue(hashMap).*/addOnSuccessListener(new OnSuccessListener<Void>() {
                         @Override
                         public void onSuccess(Void unused) {
                             Toast.makeText(context, "Added to Favorites.", Toast.LENGTH_SHORT).show();
                         }
                     }).addOnFailureListener(new OnFailureListener() {
                         @Override
                         public void onFailure(@NonNull Exception e) {
                             Toast.makeText(context, "Failed to add to you favorite list due to "+e.getMessage(), Toast.LENGTH_SHORT).show();

                         }
                     });
         }
    }
    public static void removeFromFavorite(Context context,Marker marker){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser()==null){
            Toast.makeText(context, "You're not logged in", Toast.LENGTH_SHORT).show();
        }else{
            //Save to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("Favorites").child(marker.getTitle())
                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Removed from Favorites.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Failed to remove from your favorite list due to "+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

    public static void removeFromFavorite(Context context, String title) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(context, "You're not logged in", Toast.LENGTH_SHORT).show();
        } else {
            //Save to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("Favorites").child(title)
                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //Toast.makeText(context, "Removed from Favorites.", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Toast.makeText(context, "Failed to remove from your favorite list due to " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });


        }



    }

    public static void renameMarker(Context context,String title,String originalTitle){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser()==null){
            Toast.makeText(context, "You're not logged in", Toast.LENGTH_SHORT).show();
        }else{
            //Set up the data to add in firebase of current user for favorite
            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put(originalTitle, "" +title);

            //Save to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("Favorites")/*child(marker.getTitle())*/.child(originalTitle).setValue(title)./*.
                     setValue(hashMap).*/addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(context, "Marker rename", Toast.LENGTH_SHORT).show();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Marker not rename", Toast.LENGTH_SHORT).show();

                }
            });


        }

    }

}


