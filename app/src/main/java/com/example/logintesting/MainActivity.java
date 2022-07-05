package com.example.logintesting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView register, forgetPassword;
    private EditText editTextEmail, editTextPassword;
    private Button login;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    //Save email and password
    private CheckBox rememberMe;
    //Visible password
    boolean passwordVisible;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register = (TextView) findViewById(R.id.RegisterBTN);
        register.setOnClickListener(this);

        forgetPassword=(TextView)findViewById(R.id.ForgotPassowrd);
        forgetPassword.setOnClickListener(this);

        login = (Button) findViewById(R.id.LoginBTN);
        login.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.EmailAddress);
        editTextPassword=(EditText) findViewById(R.id.Password);

        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);
        mAuth = FirebaseAuth.getInstance();

        //SAVE THE USER AND PASSWORD
        rememberMe = (CheckBox) findViewById(R.id.rememberUser);
        SharedPreferences preferences = getSharedPreferences("checkBox",MODE_PRIVATE);
        String checkBox = preferences.getString("remember","");
        if (checkBox.equals("true")){
           Intent intent = new Intent(MainActivity.this,MapsActivity.class);
           startActivity(intent);

        }else if (checkBox.equals("false")){
            Toast.makeText(this, "You have Logout.", Toast.LENGTH_SHORT).show();

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

        //PASSWORD VISIBLE

        editTextPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int Right =2;
                if (motionEvent.getAction()==MotionEvent.ACTION_UP){
                    if (motionEvent.getRawX()>= editTextPassword.getRight()-
                    editTextPassword.getCompoundDrawables()[Right].getBounds().width()){
                        int selection = editTextPassword.getSelectionEnd();
                        if (passwordVisible){
                            //set drawable image here
                            editTextPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                    0,0,R.drawable.visible,0);

                            //for hide password
                            editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        }else{
                            editTextPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                    0,0,R.drawable.eye1,0);

                            //for show password
                            editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;
                        }
                        editTextPassword.setSelection(selection);
                        return true;
                    }

                }



                return false;
            }
        });



    }

    @Override
    public void onClick(View view) {
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


    }

    private void userLogin() {
         String email = editTextEmail.getText().toString().trim();
         String password = editTextPassword.getText().toString().trim();


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



         progressBar.setVisibility(View.VISIBLE);
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
                     progressBar.setVisibility(View.GONE);


                 }else{
                     Toast.makeText(MainActivity.this, "Failed to login check your email and password!", Toast.LENGTH_SHORT).show();
                     progressBar.setVisibility(View.GONE);
                 }

             }
         });
    }

}