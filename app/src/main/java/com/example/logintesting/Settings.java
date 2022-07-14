package com.example.logintesting;

//BY SEBASTIAN JAZMIN

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Settings extends AppCompatActivity implements View.OnClickListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button About;
        Button Themes;
        Button Favorites;
        Button Logout;
        TextView user;

        FirebaseUser userFirebase;
        DatabaseReference reference;
        String useriD;

        About = (Button) findViewById(R.id.AboutButton);
        About.setOnClickListener(this);

        Themes = findViewById(R.id.ThemesButton);
        Themes.setOnClickListener(this);
        Favorites = findViewById(R.id.FavoritesButton);
        Favorites.setOnClickListener(this);
        Logout = findViewById(R.id.LogoutButton);
        Logout.setOnClickListener(this);
        user = findViewById(R.id.UserEmail);


        userFirebase = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        useriD = userFirebase.getUid();

        reference.child(useriD).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null){
                    String email = userProfile.email;

                    user.setText(email);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Settings.this, "Something wrong happened! ", Toast.LENGTH_SHORT).show();

            }
        });


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
                SharedPreferences preferences  = getSharedPreferences("checkBox",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember","false");
                editor.apply();
                finish();
                break;

        }
    }
}