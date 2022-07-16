package com.example.logintesting;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity implements View.OnClickListener {

FirebaseAuth auth;
FirebaseDatabase database;
FirebaseStorage storage;
Uri imageUri;
CircleImageView profilePicture;
Button saveBTN,closeBTN;
TextView changeProfilePicture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //init variables
        profilePicture = findViewById(R.id.profile_image);
        changeProfilePicture = findViewById(R.id.change_profile_btn);
        changeProfilePicture.setOnClickListener(this);



    }


    @Override
    public void onClick(View view) {

    }
}