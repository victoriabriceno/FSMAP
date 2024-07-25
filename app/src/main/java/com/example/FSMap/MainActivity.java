package com.example.FSMap;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.credentials.GetCredentialRequest;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

//Starting screen

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView register, forgetPassword;
    private TextInputLayout editTextEmail, editTextPassword;
    private Button login;

    //GOOGLE

    GoogleSignInOptions gso; //Deprecated as of 6/18/2024
    GoogleSignInClient gsc; //Deprecated as of 6/18/2024

    private FirebaseAuth mAuth;
    //Save email and password
    private CheckBox rememberMe;
    //Visible password
    boolean passwordVisible;
    boolean firstload = false;
    boolean connected;
    ConnectivityManager cm;


    ProgressBar progressBar;
    RelativeLayout relativeLayoutLoading;

    SharedPreferences onBoardingScreen;


    @SuppressLint({"MissingPermission", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Internet Check
        progressBar = (ProgressBar) findViewById(R.id.ProgressBarMain);
        relativeLayoutLoading = findViewById(R.id.LoadingDesign);

        register = (TextView) findViewById(R.id.RegisterBTN);
        register.setOnClickListener(this);

        forgetPassword = (TextView) findViewById(R.id.ForgotPassowrd);
        forgetPassword.setOnClickListener(this);

        login = (Button) findViewById(R.id.LoginBTN);
        login.setOnClickListener(this);

        editTextEmail = findViewById(R.id.EmailAddress);
        editTextPassword = findViewById(R.id.Password);

        mAuth = FirebaseAuth.getInstance();

        //Internet connection check
        cm = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkInfo nim = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo niw = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if ((nim != null && nim.isConnectedOrConnecting()) || (niw != null && niw.isConnectedOrConnecting())) {
                connected = true;
            } else {
                connected = false;
            }

        } else {
            connected = false;
        }

        // GOOGLE
        Button googleButton = findViewById(R.id.google_login);

        /*
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.Google_Client_ID))
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount gAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (gAccount != null) {
            navigateToMapsActivity();
        }

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = gsc.getSignInIntent();
                startActivityForResult(signInIntent, 1234);
            }
        });

         */


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(getString(R.string.Google_Client_ID))
                .requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount gAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (gAccount != null) {
            finish();
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
        }


        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = gsc.getSignInIntent();
                startActivityForResult(signInIntent, 1234);
            }
        });




    }


    int counter = 0;

    @Override
    public void onBackPressed() {


        if (counter == 2) {
            super.onBackPressed();
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            this.finish();
        }


    }

    //GOOGLE </

    /*

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    Log.d(TAG, "Google sign in successful, account ID: " + account.getId());
                    firebaseAuthWithGoogle(account.getIdToken());
                } else {
                    Log.w(TAG, "Google sign in failed, account is null");
                    Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed with exception: " + e.getStatusCode(), e);
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + idToken);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            handleSuccessfulSignIn();
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            relativeLayoutLoading.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void handleSuccessfulSignIn() {
        onBoardingScreen = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);
        boolean isFirstTime = onBoardingScreen.getBoolean("firstTime", true);

        if (isFirstTime) {
            SharedPreferences.Editor editor = onBoardingScreen.edit();
            editor.putBoolean("firstTime", false);
            editor.apply();
            startActivity(new Intent(getApplicationContext(), OnBoarding.class));
        } else {
            navigateToMapsActivity();
        }
        finish();
    }

    private void navigateToMapsActivity() {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
    }

     */


    //GOOGLE FINISH />


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        onBoardingScreen = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);

        boolean isFirsTime = onBoardingScreen.getBoolean("firsTime", true);
        relativeLayoutLoading.setVisibility(View.VISIBLE);
        if (requestCode == 1234) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),
                        null);
                FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            if(isFirsTime){
                                SharedPreferences.Editor editor = onBoardingScreen.edit();
                                editor.putBoolean("firsTime",false);
                                editor.commit();
                                startActivity(new Intent(getApplicationContext(), OnBoarding.class));
                                finish();
                            }else{
                                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                                startActivity(intent);
                            }

                        } else {
                            Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            relativeLayoutLoading.setVisibility(View.GONE);
                        }


                    }
                });

            } catch (ApiException e) {
                e.printStackTrace();
            }
        } else {

            relativeLayoutLoading.setVisibility(View.GONE);
        }


    }
    

    @Override
    protected void onStart() {
        super.onStart();
    }

    //Creates a save instance of the previous ui
    //Runs right before everything is destroyed
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("firstload", firstload);
    }

    //run right before onPostCreate
    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        firstload = inState.getBoolean("firstload");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (!connected) {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            return;
        } else {
            //SAVE THE USER AND PASSWORD
            rememberMe = (CheckBox) findViewById(R.id.rememberUser);
            SharedPreferences preferences = getSharedPreferences("checkBox", MODE_PRIVATE);
            String checkBox = preferences.getString("remember", "");
            if (!firstload) {
                if (checkBox.equals("true")) {
                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    startActivity(intent);

                } else if (checkBox.equals("false")) {
                    //  Toast.makeText(this, "You have Logout.", Toast.LENGTH_SHORT).show();
                }
                firstload = true;
            }


            rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (compoundButton.isChecked()) {
                        SharedPreferences preferences = getSharedPreferences("checkBox", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("remember", "true");
                        editor.apply();
                        Toast.makeText(MainActivity.this, "Checked", Toast.LENGTH_SHORT).show();
                    } else if (!compoundButton.isChecked()) {
                        SharedPreferences preferences = getSharedPreferences("checkBox", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("remember", "false");
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
        switch (view.getId()) {

            case R.id.RegisterBTN:
                startActivity(new Intent(this, RegisterUser.class));
                break;

            case R.id.LoginBTN:

                userLogin();
                break;
            case R.id.ForgotPassowrd:
                startActivity(new Intent(this, ForgotPassword.class));
                break;
        }
    }

    //This function handles everything that happens after pressing "log in"
    private void userLogin() {
        String email = editTextEmail.getEditText().getText().toString();
        String password = editTextPassword.getEditText().getText().toString();

        //These If blocks make sure everything is valid before signing in
        if (email.isEmpty()) {
            Toast.makeText(this, "Email is required!", Toast.LENGTH_SHORT).show();
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            Toast.makeText(this, "Please enter a valid email!", Toast.LENGTH_SHORT).show();
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Password is required!", Toast.LENGTH_SHORT).show();
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "The password should be at least 6 characters", Toast.LENGTH_SHORT).show();
            editTextPassword.requestFocus();
            return;
        }


        onBoardingScreen = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);

        //If it is the first time running the app on the device, the user will go through a tutorial once they log in
        boolean isFirsTime = onBoardingScreen.getBoolean("firsTime", true);

        relativeLayoutLoading.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user.isEmailVerified()) {
                        if (isFirsTime) {
                            SharedPreferences.Editor editor = onBoardingScreen.edit();
                            editor.putBoolean("firsTime", false);
                            editor.commit();
                            startActivity(new Intent(getApplicationContext(), OnBoarding.class));
                            finish();
                        } else {
                            startActivity(new Intent(MainActivity.this, MapsActivity.class));
                        }


                        //If the email hasn't been verified, they will be sent a verification link
                    } else {
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Check your email box to verify the email!", Toast.LENGTH_LONG).show();
                        relativeLayoutLoading.setVisibility(View.GONE);
                    }

                    //This runs if the email is verified but the password is wrong.
                } else {
                    Toast.makeText(MainActivity.this, "Check your email box to verify the email or you have the incorrect information", Toast.LENGTH_SHORT).show();
                    relativeLayoutLoading.setVisibility(View.GONE);
                }

            }
        });
    }


}