package com.example.logintesting;

//BY SEBASTIAN JAZMIN
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

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

public class Favorites extends AppCompatActivity {


 RecyclerView recyclerView;
 DatabaseReference databaseReference;
 AdapterUserFavoriteList adapterUserFavoriteList;
 ArrayList<UserFavoriteList> list;
 FirebaseAuth firebaseAuth;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerView = findViewById(R.id.markersRV);
       // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Favorites");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapterUserFavoriteList = new AdapterUserFavoriteList(this,list);
        recyclerView.setAdapter(adapterUserFavoriteList);

         DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Favorites");
         databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){


                    UserFavoriteList userFavoriteList = dataSnapshot.getValue(UserFavoriteList.class);

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
             hashMap.put("Markers",""+marker);


             //Save to db
             DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
             ref.child(firebaseAuth.getUid()).child("Favorites").child(marker.getTitle())
                     .setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
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









}
