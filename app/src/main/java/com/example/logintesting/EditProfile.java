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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
FirebaseUser firebaseAuth;
ImageView pencilProfilechange , backEditPROFILE;
EditText changeUser;

GoogleSignInOptions gso;
GoogleSignInClient gsc;

    DatabaseReference databaseReference;
    String fullName;
    String useriD;
    TextView emailProfile,getEmailProfile;


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
        emailProfile = findViewById(R.id.EmailEditprofile);

        backEditPROFILE =findViewById(R.id.backBTN);
        backEditPROFILE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfile.this,Settings.class));
            }
        });


        //FIREBASE
        firebaseAuth = FirebaseAuth.getInstance().getCurrentUser();
        useriD = firebaseAuth.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
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

             if (isNameChanged() && isImageChanged()){
                 Toast.makeText(EditProfile.this, "Profile changed", Toast.LENGTH_SHORT).show();
             }else if (isNameChanged() || isImageChanged()){
                 //UploadImageToFirebase(imageUriSave);
                 Toast.makeText(EditProfile.this, "Profile changed", Toast.LENGTH_SHORT).show();
             }

            // UploadImageToFirebase(imageUriSave);
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



        //SHOWING USER DATA

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);
        GoogleSignInAccount gAccount = GoogleSignIn.getLastSignedInAccount(this);

        Intent data = getIntent();
       fullName = data.getStringExtra("fullName");

      changeUser.setText(fullName);

        databaseReference.child(useriD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null && gAccount!= null){
                    String email = userProfile.email;



                    emailProfile.setText(email);


                    String emailGoogle = gAccount.getEmail();

                    emailProfile.setText(emailGoogle);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfile
                        .this, "Something wrong happened! ", Toast.LENGTH_SHORT).show();

            }
        });




    }

    private boolean isImageChanged() {

        UploadImageToFirebase(imageUri);
        return  true;
    }


  /*  private void UpdateUser(){

        if (isNameChanged()){

            Toast.makeText(this, "Profile has been updated", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(this, "Data is the same and there are not changes", Toast.LENGTH_SHORT).show();
        }
    }*/

    private boolean isNameChanged() {

         if (!fullName.equals(changeUser.getText().toString())){
            // Toast.makeText(this, "User name changed", Toast.LENGTH_SHORT).show();
             databaseReference.child(fAuth.getUid()).child("fullName").setValue(changeUser.getText().toString());
             fullName = changeUser.getText().toString();
             return true;
         }else{
            // Toast.makeText(this, "User name not updated", Toast.LENGTH_SHORT).show();
             return false;
         }
    }


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