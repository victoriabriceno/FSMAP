package com.example.logintesting;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity  {

CircleImageView profileImage;
Button saveProfile, closeProfile;
Uri imageUri, imageUriSave;
StorageReference storageReference;
FirebaseAuth fAuth;
ImageView pencilProfilechange;
EditText changeUser;
DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //init variables
        profileImage = findViewById(R.id.profile_image);
        saveProfile = findViewById(R.id.Savebtn);
        closeProfile = findViewById(R.id.Closebtn);
        pencilProfilechange = findViewById(R.id.pencil_change_profile);
        changeUser = findViewById(R.id.UserChange);



    //FIREBASE

        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("Users/"+fAuth.getCurrentUser().getUid()+"/ProfilePicture.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });



        //CODE FRO CLOSE THE EDIT PROFILE CLASS
        closeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(EditProfile.this,Settings.class));
            }
        });

        //CODE FOR SAVE THE IMAGE WHEN THE USER CLICKS SAVE
     saveProfile.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {


             UploadImageToFirebase(imageUriSave);
         }
     });

        //CODE FOR CHANGE THE PICTURE
        pencilProfilechange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open the gallary
                Intent OpenGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(OpenGalleryIntent,1000);
            }
        });

      Intent data = getIntent();
      String fullName = data.getStringExtra("fullName");

      changeUser.setText(fullName);





    }




    //Before you have as a paramete Uri uri
    private void UploadImageToFirebase(Uri uri) {


        //IF EVERYTHING GOES TO SHIT THIS IS THE GOOD CODE!!!
        final StorageReference fileReference = storageReference.child("Users/"+fAuth.getCurrentUser().getUid()+"/ProfilePicture.jpg");
        fileReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               // Toast.makeText(EditProfile.this, "Image uploaded", Toast.LENGTH_SHORT).show();

                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfile.this, "Failed to uploaded.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000){
            if (resultCode == Activity.RESULT_OK){
                imageUri = data.getData();
               profileImage.setImageURI(imageUri); //you has this comment before
            }
        }

       imageUriSave = imageUri;
    }
}