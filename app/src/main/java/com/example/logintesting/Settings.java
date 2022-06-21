package com.example.logintesting;

//BY SEBASTIAN JAZMIN

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class Settings extends AppCompatActivity implements View.OnClickListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button About;
        Button Themes;
        Button Favorites;
        Button Logout;

        About = (Button) findViewById(R.id.AboutButton);
        About.setOnClickListener(this);

        Themes = findViewById(R.id.ThemesButton);
        Themes.setOnClickListener(this);
        Favorites = findViewById(R.id.FavoritesButton);
        Favorites.setOnClickListener(this);
        Logout = findViewById(R.id.LogoutButton);
        Logout.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.AboutButton:
                startActivity(new Intent(this,FSMapAbout.class));
                break;
            case R.id.FavoritesButton:
                startActivity(new Intent(this,Favorites.class));
                break;
            case R.id.ThemesButton:
                startActivity(new Intent(this,Themes.class));
                break;
            case R.id.LogoutButton:
                FirebaseAuth.getInstance().signOut();
                //Returns to specified Screen
                Intent i = new Intent(this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(i);
                break;

        }
    }
}