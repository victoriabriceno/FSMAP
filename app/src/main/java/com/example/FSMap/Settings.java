package com.example.FSMap;

//Settings Screen

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends AppCompatActivity implements View.OnClickListener {


    TextView userNameSettings;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Binding Buttons
        //CircleImageView userProfileSetting;
        CardView About;
        CardView CreatedMarkers;
        CardView Favorites;
        CardView Logout;
        TextView user;
        Button editProfile;
        CircleImageView userProfileSetting;
        ImageView backButton;

        //FIREBASE
        FirebaseAuth fAuth;
        StorageReference storageReference;
        FirebaseUser userFirebase;
        DatabaseReference reference;
        String useriD;

        About = (CardView) findViewById(R.id.AboutButton);
        About.setOnClickListener(this);

        CreatedMarkers = findViewById(R.id.CustomMarkersButton);
        CreatedMarkers.setOnClickListener(this);
        Favorites = findViewById(R.id.FavoritesButton);
        Favorites.setOnClickListener(this);
        Logout = findViewById(R.id.LogoutButton);
        Logout.setOnClickListener(this);
        userProfileSetting = findViewById(R.id.userSettings);
        userProfileSetting.setOnClickListener(this);
        backButton = (ImageView) findViewById(R.id.backBTN);
        backButton.setOnClickListener(this);

        userNameSettings = findViewById(R.id.userNamesettings);


        //FIREBASE
        userFirebase = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        useriD = userFirebase.getUid();

        //PROFILE PICTURE THIS CODE IS GOOD IF EVERYTHING GOES TO SHIT UNCOMMENT THIS
        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("Users/" + fAuth.getCurrentUser().getUid() + "/ProfilePicture.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(userProfileSetting);
            }
        });

        reference.child(useriD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null) {
                    String email = userProfile.email;
                    String userName = userProfile.fullName;


                    userNameSettings.setText(userName);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Settings.this, "Something wrong happened! ", Toast.LENGTH_SHORT).show();

            }
        });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);


        // SWITCH
        SwitchCompat switchCompat;
        switchCompat = findViewById(R.id.Switch);

        SharedPreferences sharedPreferences = getSharedPreferences("save", MODE_PRIVATE);
        switchCompat.setChecked(sharedPreferences.getBoolean("value", true));

        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create map for firebase data
                Map<String, Object> DarkMode = new HashMap<>();

                DatabaseReference tdatabase;
                String userID;
                userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                tdatabase = FirebaseDatabase.getInstance().getReference("/Users/" + userID);

                if (switchCompat.isChecked()) {
                    SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value", true);
                    editor.apply();
                    switchCompat.setChecked(true);

                    //getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    setTheme((AppCompatDelegate.MODE_NIGHT_YES));
                    //Add data to firebase
                    DarkMode.put("DarkMode", Boolean.TRUE);
                    tdatabase.updateChildren(DarkMode);

                } else {
                    SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value", false);
                    editor.apply();
                    switchCompat.setChecked(false);

                    //Need to find alternative to below
                    //getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    setTheme(AppCompatDelegate.MODE_NIGHT_NO);

                    //Add data to firebase
                    DarkMode.put("DarkMode", Boolean.FALSE);
                    tdatabase.updateChildren(DarkMode);
                }
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //Starts activity corresponding to button
            case R.id.AboutButton:
                startActivity(new Intent(this, FSMapAbout.class));
                break;
            case R.id.FavoritesButton:
                startActivity(new Intent(this, Favorites.class));
                break;
            case R.id.CustomMarkersButton:
                startActivity(new Intent(this, CustomMarker.class));
                break;
            case R.id.ThemesButton:
                startActivity(new Intent(this, Themes.class));
                break;
            case R.id.LogoutButton:
                //Logs user out and returns to login
                gsc.signOut();
                FirebaseAuth.getInstance().signOut();
                // Returns to specified Screen
                Intent i = new Intent(Settings.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(i);
                SharedPreferences preferences = getSharedPreferences("checkBox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember", "false");
                editor.apply();
                finish();
                break;

            case R.id.userSettings:
                Intent intent = new Intent(view.getContext(), EditProfile.class);
                intent.putExtra("fullName", userNameSettings.getText().toString());
                startActivity(intent);
                break;

            case R.id.backBTN:
                startActivity(new Intent(this, MapsActivity.class));
                break;

        }
    }
}