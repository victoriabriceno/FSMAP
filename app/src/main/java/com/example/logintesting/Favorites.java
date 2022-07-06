package com.example.logintesting;

//BY SEBASTIAN JAZMIN
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Favorites extends AppCompatActivity {


    /*private ArrayList<ModelFavorite> listplaces ;
    private AdapterFavoriteList adapterFavoriteList;
    private FirebaseAuth firebaseAuth;
    private ActivityFavoritesBinding binding;*/

    DatabaseReference favoriteList;
    FirebaseDatabase databaseReference = FirebaseDatabase.getInstance();

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);



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
