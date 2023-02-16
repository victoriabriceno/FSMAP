package com.example.FSMap;

//BY SEBASTIAN JAZMIN
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class FSMapAbout extends AppCompatActivity {

    ImageView backAbout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fsmap_about);

        backAbout = findViewById(R.id.backBTN);
        backAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FSMapAbout.this,Settings.class));
            }
        });
    }
}