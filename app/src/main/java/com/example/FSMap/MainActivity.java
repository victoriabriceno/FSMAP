package com.example.FSMap;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView register, forgetPassword;
    private TextInputLayout editTextEmail, editTextPassword;
    private Button login;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    //Save email and password
    private CheckBox rememberMe;
    //Visible password
    boolean passwordVisible;
    boolean firstload = false;
    boolean connected;
    ConnectivityManager cm;

    // Facebook
    CallbackManager mCallbackManager;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Internet Check


        register = (TextView) findViewById(R.id.RegisterBTN);
        register.setOnClickListener(this);

        forgetPassword=(TextView)findViewById(R.id.ForgotPassowrd);
        forgetPassword.setOnClickListener(this);

        login = (Button) findViewById(R.id.LoginBTN);
        login.setOnClickListener(this);

        editTextEmail =  findViewById(R.id.EmailAddress);
        editTextPassword= findViewById(R.id.Password);

        mAuth = FirebaseAuth.getInstance();

        //Internet connection check
        cm = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null)
        {
            NetworkInfo nim = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo niw = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if ((nim != null && nim.isConnectedOrConnecting()) || (niw != null && niw.isConnectedOrConnecting())) {
                connected = true;
            }
            else {
                connected = false;
            }

        }
        else {
            connected = false;
        }

        // Google
        Button googleButton = findViewById(R.id.google_login);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        GoogleSignInAccount gAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(gAccount != null){
            finish();
            Intent intent = new Intent(MainActivity.this,MapsActivity.class);
            startActivity(intent);
        }

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = gsc.getSignInIntent();
               startActivityForResult(signInIntent,1234);
            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1234){

                 Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                 try {
                     GoogleSignInAccount account = task.getResult(ApiException.class);
                     AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),
                             null);
                     FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                         @Override
                         public void onComplete(@NonNull Task<AuthResult> task) {

                             if(task.isSuccessful()){
                                 Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                                 startActivity(intent);
                             }else{
                                 Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                             }


                         }
                     });


                 }catch (ApiException e){
                     e.printStackTrace();
                 }
        }else{
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    //Creates a save instance of the previous ui
    //Runs right before everything is destroyed
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean("firstload", firstload);
    }
    //run right before onPostCreate
    @Override
    protected void onRestoreInstanceState(Bundle inState){
        super.onRestoreInstanceState(inState);
        firstload = inState.getBoolean("firstload");
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        if (!connected){
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            //SAVE THE USER AND PASSWORD
            rememberMe = (CheckBox) findViewById(R.id.rememberUser);
            SharedPreferences preferences = getSharedPreferences("checkBox",MODE_PRIVATE);
            String checkBox = preferences.getString("remember","");
            if (!firstload){
                if (checkBox.equals("true")){
                    Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                    startActivity(intent);

                }else if (checkBox.equals("false")){
                  //  Toast.makeText(this, "You have Logout.", Toast.LENGTH_SHORT).show();
                }
                firstload = true;
            }



            rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (compoundButton.isChecked()){
                        SharedPreferences preferences  = getSharedPreferences("checkBox",MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("remember","true");
                        editor.apply();
                        Toast.makeText(MainActivity.this, "Checked", Toast.LENGTH_SHORT).show();
                    }else if(!compoundButton.isChecked()){
                        SharedPreferences preferences  = getSharedPreferences("checkBox",MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("remember","false");
                        editor.apply();
                        Toast.makeText(MainActivity.this, "Unchecked", Toast.LENGTH_SHORT).show();
                    }


                }
            });

        }

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View view) {
//        if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
//                cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
//            connected = true;
//        else
//            connected= false;
//        if (!connected){
//            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
//
//        }
//        else{
            switch(view.getId()){

                case R.id.RegisterBTN:
                    startActivity(new Intent(this,RegisterUser.class));
                    break;

                case R.id.LoginBTN:
                    userLogin();
                    break;
                case R.id.ForgotPassowrd:
                    startActivity(new Intent(this,ForgotPassword.class));
                    break;
            }
//        }
    }

    private void userLogin() {
        String email = editTextEmail.getEditText().toString().trim();
        String password = editTextPassword.getEditText().toString().trim();


        if (email.isEmpty()){
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()){
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6){
            editTextPassword.setError("Password legnth is 6 characters!");
            editTextPassword.requestFocus();
            return;
        }



        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();
                    if (user.isEmailVerified()){
                        startActivity(new Intent(MainActivity.this, MapsActivity.class));


                    }else{
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this,"Check your email box to verify the email!",Toast.LENGTH_LONG).show();
                    }


                }else{
                    Toast.makeText(MainActivity.this, "Failed to login check your email and password!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }




}