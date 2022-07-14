package com.example.logintesting;

//BY SEBASTIAN JAZMIN
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class Themes extends AppCompatActivity implements View.OnClickListener {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themes);

        ImageButton LightMode;
        ImageButton DarkMode;

        LightMode = findViewById(R.id.LightModeButton);
        LightMode.setOnClickListener(this);
        DarkMode = findViewById(R.id.DarkModeButton);
        DarkMode.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Map<String, Object> DarkMode = new HashMap<>();

        DatabaseReference tdatabase;
        String userID;
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        tdatabase = FirebaseDatabase.getInstance().getReference("/Users/"+userID);
        if (view.getId() == R.id.LightModeButton) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            DarkMode.put("DarkMode", Boolean.FALSE);
            tdatabase.updateChildren(DarkMode);
        }
        else if (view.getId() == R.id.DarkModeButton) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            DarkMode.put("DarkMode", Boolean.TRUE);
            tdatabase.updateChildren(DarkMode);
        }
    }
}