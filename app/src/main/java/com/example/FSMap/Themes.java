package com.example.FSMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

//NO LONGER USED

//This class is the Themes screen, where the user can change their app to use light or dark mode in the app
public class Themes extends AppCompatActivity implements View.OnClickListener {


    //Creation of Themes Screen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themes);

        //Setting up buttons for the Themes screen to display
        ImageButton LightMode;
        ImageButton DarkMode;
        ImageView backThemes;

        //Back button on themes screen to exit the screen
        backThemes = findViewById(R.id.backBTN);
        backThemes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Themes.this, Settings.class));
            }
        });

        LightMode = findViewById(R.id.LightModeButton);
        LightMode.setOnClickListener(this);
        DarkMode = findViewById(R.id.DarkModeButton);
        DarkMode.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        //Create map data structure to save to Firebase
        Map<String, Object> DarkMode = new HashMap<>();

        DatabaseReference tdatabase;
        String userID;
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        tdatabase = FirebaseDatabase.getInstance().getReference("/Users/" + userID);

        //Runs when Light Mode is clicked
        if (view.getId() == R.id.LightModeButton) {
            setTheme(AppCompatDelegate.MODE_NIGHT_NO);

            //Add data to firebase
            DarkMode.put("DarkMode", Boolean.FALSE);
            tdatabase.updateChildren(DarkMode);

        //Runs when Dark Mode is clicked
        } else if (view.getId() == R.id.DarkModeButton) {
            setTheme((AppCompatDelegate.MODE_NIGHT_YES));
            //Add data to firebase
            DarkMode.put("DarkMode", Boolean.TRUE);
            tdatabase.updateChildren(DarkMode);
        }
    }


}